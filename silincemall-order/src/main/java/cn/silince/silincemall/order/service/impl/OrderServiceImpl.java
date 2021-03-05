package cn.silince.silincemall.order.service.impl;

import cn.silince.common.constant.OrderConstant;
import cn.silince.common.exception.NoStockException;
import cn.silince.common.to.mq.OrderTo;
import cn.silince.common.to.mq.SeckillOrderTo;
import cn.silince.common.utils.R;
import cn.silince.common.vo.MemberRespVo;
import cn.silince.silincemall.order.dao.OrderItemDao;
import cn.silince.silincemall.order.entity.OrderItemEntity;
import cn.silince.silincemall.order.entity.PaymentInfoEntity;
import cn.silince.silincemall.order.enume.OrderStatusEnum;
import cn.silince.silincemall.order.feign.CartFeignService;
import cn.silince.silincemall.order.feign.MemberFeignService;
import cn.silince.silincemall.order.feign.ProductFeignService;
import cn.silince.silincemall.order.feign.WmsFeignService;
import cn.silince.silincemall.order.interceptor.LoginUserInterceptor;
import cn.silince.silincemall.order.service.OrderItemService;
import cn.silince.silincemall.order.service.PaymentInfoService;
import cn.silince.silincemall.order.to.OrderCreateTo;
import cn.silince.silincemall.order.vo.*;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.sun.xml.internal.ws.util.CompletedFuture;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.silince.common.utils.PageUtils;
import cn.silince.common.utils.Query;

import cn.silince.silincemall.order.dao.OrderDao;
import cn.silince.silincemall.order.entity.OrderEntity;
import cn.silince.silincemall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();

    @Resource
    private MemberFeignService memberFeignService;

    @Resource
    private CartFeignService cartFeignService;

    @Resource
    private ThreadPoolExecutor executor;

    @Resource
    private WmsFeignService wmsFeignService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private ProductFeignService productFeignService;

    @Resource
    private OrderDao orderDao;

    @Resource
    private PaymentInfoService paymentInfoService;

    @Resource
    private OrderItemService orderItemService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        // 从拦截器的ThreadLocal中获取当前用户
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        // 获取之前的请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            // 1. 远程查询所有的收获地址列表
            RequestContextHolder.setRequestAttributes(requestAttributes); // 异步编排的每一个线程都共享之前的数据
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setAddress(address);
        }, executor);


        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            // 2 远程查询购物车所有选中的购物项
            // ⚠️ Feign在远程调用之前要构造请求，调用很多的拦截器
            RequestContextHolder.setRequestAttributes(requestAttributes); // 异步编排的每一个线程都共享之前的数据
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(items);
        }, executor).thenRunAsync(()->{
            // 继续异步查询库存信息
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> collect = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            R hasStock = wmsFeignService.getSkuHasStock(collect);
            List<SkuStockVo> data = hasStock.getData(new TypeReference<List<SkuStockVo>>() {
            });
            if (data!=null){
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(map);
            }
        },executor);

        // 3 得到用户的积分信息
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegration(integration);

        // 4 订单总额/应付价格 通过get方法自动计算

        // 5 接口幂等性 防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberRespVo.getId()
                ,token,30, TimeUnit.MINUTES); // 给服务器存一份令牌
        confirmVo.setOrderToken(token); // 给页面存一份令牌


        CompletableFuture.allOf(getAddressFuture,cartFuture).get();
        return confirmVo;
    }

    /** 
    * @description: 下单： 去创建订单，验证令牌，验证价格，锁定库存 
    */
//    @GlobalTransactional // 下单是典型的高并发场景，SeataAT模式效率太低了，不适合。主要还是考虑采用 mq的方案。
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        responseVo.setCode(0);// 默认成功状态码 有任何异常都会发生改变
        confirmVoThreadLocal.set(vo);

        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get(); // 从拦截器中获取当前登陆的用户
        // 1 验证令牌【令牌的对比和删除必须保证原子性】
        String orderToken = vo.getOrderToken();
        // Lua脚本原子验证令牌和删除令牌 0-校验失败 1-校验且删除成功
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] " +
                "then return redis.call('del', KEYS[1]) else return 0 end";
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class)
                , Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()), orderToken);
        if (result==0L){
            // 令牌验证失败
            responseVo.setCode(1);
            return responseVo;
        }else {
            // 令牌验证成功 下单： 去创建订单，验证价格，锁定库存...
            // 创建订单，订单项等信息
            OrderCreateTo order = this.createOrder();
            // 2 验价/金额对比
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue())<0.01){
                // 3 验价成功，保存订单到数据库
                this.saveOrder(order);
                // 4 库存锁定 只要有异常就回滚订单数据
                // 需要数据：订单号，所有订单项(skuId,SkuName,num)
                SkuWareLockVo lockVo = new SkuWareLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> orderItemVos = order.getOrderItems().stream().map(item -> {
                    OrderItemVo itemVo = new OrderItemVo();
                    itemVo.setSkuId(item.getSkuId());
                    itemVo.setCount(item.getSkuQuantity());
                    itemVo.setTitle(item.getSkuName());
                    return itemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(orderItemVos);
                // 远程锁库存 .为了保证高并发。库存服务自己回滚。通过mq实现库存解锁
                R r = wmsFeignService.orderLockStock(lockVo);
                if (r.getCode()==0){
                    // 库存锁定成功
                    responseVo.setOrder(order.getOrder());
                    // TODO 5 远程扣减积分
//                    int i =10/0;// 订单回滚，库存不滚
                    // TODO MQ--- 订单创建成功发送消息给MQ(为了实现延时队列定时关单功能)
                    rabbitTemplate.convertAndSend("order-event-exchange","order.create.order"
                            ,order.getOrder());
                    return responseVo;
                }else {
                    // 库存锁定失败
                    String msg = (String) r.get("msg");
                    throw new NoStockException(msg);
                }
            }else {
                responseVo.setCode(2);
                return responseVo;
            }

        }

    }

    /**
    * @description: 保存订单数据
    */
    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity); // 保存订单

        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.saveBatch(orderItems); // 保存订单项


    }

    /**
    * @description: 创建订单
    */
    private OrderCreateTo createOrder(){
        OrderCreateTo createTo = new OrderCreateTo();
        // 1. 生成一个订单号
        String orderSn = IdWorker.getTimeId();

        // 构建订单总信息  OrderCreateTo.OrderEntity
        OrderEntity orderEntity = this.buildOrder(orderSn);

        // 2 获取到所有的订单项
        List<OrderItemEntity> itemEntities = this.buildOrderItems(orderSn);

        // 3 计算价格相关
        this.computePrice(orderEntity,itemEntities);
        createTo.setOrder(orderEntity);
        createTo.setOrderItems(itemEntities);

        return createTo;
    }

    /**
    * @description:  计算价格相关 验价
    */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> itemEntities) {
        BigDecimal total = new BigDecimal("0.0");

        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");

        BigDecimal gift = new BigDecimal("0.0"); // 总积分
        BigDecimal growth = new BigDecimal("0.0"); // 总成长值
        //订单的总额，叠加每一个订单项的总额信息
        for (OrderItemEntity entity : itemEntities) {
            coupon = coupon.add(entity.getCouponAmount());
            integration = integration.add(entity.getIntegrationAmount());
            promotion = promotion.add(entity.getPromotionAmount());
            total = total.add(entity.getRealAmount());

            gift = gift.add(new BigDecimal(entity.getGiftIntegration().toString()));
            growth = growth.add(new BigDecimal(entity.getGiftGrowth().toString()));

        }
        //1、订单价格相关
        orderEntity.setTotalAmount(total);
        //应付总额
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setCouponAmount(coupon);

        //设置积分等信息
        orderEntity.setIntegration(gift.intValue());
        orderEntity.setGrowth(growth.intValue());
        orderEntity.setDeleteStatus(0);//未删除
    }

    /**
    * @description: 构建订单数据  OrderCreateTo.OrderEntity
    */
    private OrderEntity buildOrder(String orderSn) {
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(orderSn);
        // 保存会员id
        entity.setMemberId(LoginUserInterceptor.loginUser.get().getId());
        // 获取收货地址信息
        OrderSubmitVo submitVo = confirmVoThreadLocal.get();
        R fare = wmsFeignService.getFare(submitVo.getAddrId());
        FareVo fareResp = fare.getData(new TypeReference<FareVo>() {
        });
        // 设置运费信息和收货人信息
        entity.setFreightAmount(fareResp.getFare());
        entity.setReceiverCity(fareResp.getAddress().getCity());
        entity.setReceiverDetailAddress(fareResp.getAddress().getDetailAddress());
        entity.setReceiverName(fareResp.getAddress().getName());
        entity.setReceiverPhone(fareResp.getAddress().getPhone());
        entity.setReceiverPostCode(fareResp.getAddress().getPostCode());
        entity.setReceiverProvince(fareResp.getAddress().getProvince());
        entity.setReceiverRegion(fareResp.getAddress().getRegion());

        // 设置订单的相关状态信息
        entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        entity.setAutoConfirmDay(7); // 自动确认时间

        return entity;
    }

    /**
    * @description: 构建所有订单项数据  OrderCreateTo.List<OrderItemEntity>
    */
    private List<OrderItemEntity> buildOrderItems(String orderSn ) {
        // 最后确定一遍每个购物项的价格
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        if (currentUserCartItems!=null&&currentUserCartItems.size()>0){
            List<OrderItemEntity> itemEntities = currentUserCartItems.stream().map(item -> {
                OrderItemEntity itemEntity = this.buildOrderItem(item); // 构建订单项
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
            return itemEntities;
        }
        return null;

    }

    /**
    * @description: 构建一个订单项数据
    */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem){
        OrderItemEntity itemEntity = new OrderItemEntity();
        // 1 订单信息：订单号  构建所有订单项数据时最后添加
        // 2 商品的spu信息
        Long skuId = cartItem.getSkuId();
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {
        });
        itemEntity.setSpuId(data.getId());
        itemEntity.setSpuBrand(data.getBrandId().toString());
        itemEntity.setSpuName(data.getSpuName());
        itemEntity.setCategoryId(data.getCatalogId());
        // 3 商品的sku信息
        itemEntity.setSkuId(cartItem.getSkuId());
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPic(cartItem.getImage());
        itemEntity.setSkuPrice(cartItem.getPrice());
        String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
        itemEntity.setSkuAttrsVals(skuAttr);
        itemEntity.setSkuQuantity(cartItem.getCount());

        // 4 TODO 优惠信息
        // 5 积分信息
        itemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        itemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());

        // 6 订单项的价格信息
        itemEntity.setPromotionAmount(new BigDecimal("0"));
        itemEntity.setCouponAmount(new BigDecimal("0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0"));
        //当前订单项的实际金额。 总额-各种优惠
        BigDecimal orign = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        BigDecimal subtract = orign.subtract(itemEntity.getCouponAmount())
                .subtract(itemEntity.getPromotionAmount())
                .subtract(itemEntity.getIntegrationAmount());
        itemEntity.setRealAmount(subtract);

        return itemEntity;
    }


    /**
     * @description: 根据订单号查询订单详细信息
     */
    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity order_sn = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return order_sn;
    }

    /**
     * @description: 定时关闭订单 --mq
     */
    @Override
    public void closeOrder(OrderEntity entity) {
        // 关闭订单之前先查询订单的最新状态
        OrderEntity orderEntity = this.getById(entity.getId());
        if (orderEntity.getStatus()==OrderStatusEnum.CREATE_NEW.getCode()){
            // 时间到后 待付款的订单需要关闭
            OrderEntity update = new OrderEntity();
            update.setId(entity.getId());
            update.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(update);
            // 在给订单交换机发送消息路由给库存队列
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity,orderTo);
            try {
                // TODO 保证消息一定会发送出去，每一个消息都可以做好日志记录(给数据库保存每一个消息的详细信息)
                // TODO 定期扫描数据库将失败的消息再发送一遍
                rabbitTemplate.convertAndSend("order-event-exchange","order.release.other"
                        ,orderTo);
            } catch (Exception e) {
                // TODO 将没法发送的消息进行重试发送 while()
            }
        }

    }

    /**
     * @description: 获取订单的支付信息
     */
    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity orderByOrderSn = this.getOrderByOrderSn(orderSn);
        // 向上取值到两位小数
        BigDecimal bigDecimal = orderByOrderSn.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(bigDecimal.toString());
        payVo.setOut_trade_no(orderByOrderSn.getOrderSn());
        // 查询所有订单项
        List<OrderItemEntity> order_sn = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        payVo.setSubject(order_sn.get(0).getSkuName()); // 订单的标题
        payVo.setBody(order_sn.get(0).getSkuAttrsVals()); // 备注
        return payVo;
    }

    /**
     * 分页查询当前登陆用户的所有订单
     */
    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        IPage<OrderEntity> page = this.page(new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id", memberRespVo.getId())
                        .orderByDesc("id"));
        List<OrderEntity> order_sn = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> itemEntities = orderItemService.list(
                    new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItemEntities(itemEntities); // 设置订单项
            return order;
        }).collect(Collectors.toList());

        page.setRecords(order_sn);

        return new PageUtils(page);
    }

    /**
     * @description: 处理支付宝异步通知的返回数据
     */
    @Override
    public String handlePayResult(PayAsyncVo vo) {
        //1、保存交易流水
        PaymentInfoEntity infoEntity = new PaymentInfoEntity();
        infoEntity.setAlipayTradeNo(vo.getTrade_no());
        infoEntity.setOrderSn(vo.getOut_trade_no());
        infoEntity.setPaymentStatus(vo.getTrade_status());
        infoEntity.setCallbackTime(vo.getNotify_time());

        paymentInfoService.save(infoEntity); // 一个订单对应一个流水

        //2、修改订单的状态信息
        if (vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISHED")) {
            //支付成功状态
            String outTradeNo = vo.getOut_trade_no();
            this.baseMapper.updateOrderStatus(outTradeNo,OrderStatusEnum.PAYED.getCode());
        }
        return "success";

    }


    /**
     * @description: 创建秒杀消息 的订单详情
     */
    @Override
    public void createSeckillOrder(SeckillOrderTo seckillOrder) {
        //TODO 保存订单信息
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(seckillOrder.getOrderSn());
        orderEntity.setMemberId(seckillOrder.getMemberId());

        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());

        BigDecimal multiply = seckillOrder.getSeckillPrice()
                .multiply(new BigDecimal("" + seckillOrder.getNum()));
        orderEntity.setPayAmount(multiply);
        this.save(orderEntity);

        //TODO 保存订单项信息
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderSn(seckillOrder.getOrderSn());
        orderItemEntity.setRealAmount(multiply);
        //TODO 获取当前SKU的详细信息进行设置  productFeignService.getSpuInfoBySkuId()
        orderItemEntity.setSkuQuantity(seckillOrder.getNum());

        orderItemService.save(orderItemEntity);

    }
}