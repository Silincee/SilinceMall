package cn.silince.silincemall.order.service;

import cn.silince.common.to.mq.SeckillOrderTo;
import cn.silince.silincemall.order.vo.*;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.order.entity.OrderEntity;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 19:13:54
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /** 
    * @description: 返回订单确认需要用的数据 
    */ 
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    /** 
    * @description: 下单： 去创建订单，验证令牌，验证价格，锁定库存 
    */ 
    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    /**
     * @description: 根据订单号查询订单详细信息
     */
    OrderEntity getOrderByOrderSn(String orderSn);

    /** 
    * @description: 定时关闭订单 --mq 
    */
    void closeOrder(OrderEntity entity);

    /**
    * @description: 获取订单的支付信息
    */
    PayVo getOrderPay(String orderSn);

    /**
     * 分页查询当前登陆用户的所有订单
     */
    PageUtils queryPageWithItem(Map<String, Object> params);

    /**
    * @description: 处理支付宝异步通知的返回数据
    */
    String handlePayResult(PayAsyncVo vo);

    /** 
    * @description: 创建秒杀消息 的订单详情 
    */ 
    void createSeckillOrder(SeckillOrderTo seckillOrder);
}

