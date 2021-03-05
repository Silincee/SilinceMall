package cn.silince.silincemall.ware.service.impl;

import cn.silince.common.exception.NoStockException;
import cn.silince.common.to.mq.OrderTo;
import cn.silince.common.to.mq.StockDetailTo;
import cn.silince.common.to.mq.StockLockedTo;
import cn.silince.common.utils.R;
import cn.silince.silincemall.ware.entity.WareOrderTaskDetailEntity;
import cn.silince.silincemall.ware.entity.WareOrderTaskEntity;
import cn.silince.silincemall.ware.feign.OrderFeignService;
import cn.silince.silincemall.ware.feign.ProductFeignService;
import cn.silince.silincemall.ware.service.WareOrderTaskDetailService;
import cn.silince.silincemall.ware.service.WareOrderTaskService;
import cn.silince.silincemall.ware.vo.*;
import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.silince.common.utils.PageUtils;
import cn.silince.common.utils.Query;

import cn.silince.silincemall.ware.dao.WareSkuDao;
import cn.silince.silincemall.ware.entity.WareSkuEntity;
import cn.silince.silincemall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    private WareSkuDao wareSkuDao;

    @Resource
    private ProductFeignService productFeignService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Resource
    private WareOrderTaskService wareOrderTaskService;

    @Resource
    private OrderFeignService orderFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();

        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", skuId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params), queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        // 1 判断 如果还没有这个库存记录则变为新增
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(
                new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (wareSkuEntities == null || wareSkuEntities.size() == 0) {
            WareSkuEntity skuEntity = new WareSkuEntity();
            skuEntity.setSkuId(skuId);
            skuEntity.setStock(skuNum);
            skuEntity.setWareId(wareId);
            skuEntity.setStockLocked(0); // 默认锁定库存为0
            // 远程查询sku的名字,如果失败 整个事务不需要回滚
            // 方法一 自己catch异常
            // TODO 方法二 还有什么办法 高级部分讲解
            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                if (info.getCode() == 0) {
                    skuEntity.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {
                log.error("远程查询sku名字失败");
            }
            wareSkuDao.insert(skuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            // 查询当前sku的总库存量
            // select sum(stock-stock_lock) from `wms_ware_sku` where sku_id=1
            Long count = this.baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count == null ? false : count > 0);
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }


    /**
     * @description: 为某个订单锁库存
     * 库存解锁的场景：
     * 1. 下订单成功，订单过期没有支付被系统自动取消、被用户手动取消。都要解锁库存。
     * 2. 下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚。之前锁定的库存就要自动解锁。
     */
    @Transactional
    @Override
    public Boolean orderLockStock(SkuWareLockVo vo) {
        /**
         * 保存库存工作单。为了追溯方便
         */
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.save(taskEntity);


        // 1 找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();

        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            // 查询这个商品在哪里有库存
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());

        // 2 锁定库存
        Boolean allLock = true; // 只要有一个商品没被锁住，就改为false
        for (SkuWareHasStock hasStock : collect) {
            Boolean skuStocked = false; //当前商品的锁定状态
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if (wareIds == null || wareIds.size() == 0) {
                // 没有任何仓库有这个商品的库存
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
                // 锁定指定仓库的指定数量的商品 成功返回1(受影响的行数)否则为0
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                if (count == 1) {
                    // 当前仓库锁定成功
                    skuStocked = true;
                    // TODO 告诉MQ锁定成功,并保存锁定成功的详情
                    // 如果每一个商品都锁定成功，将当前商品锁定了几件的工作单记录发给MQ
                    WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity(null, skuId,
                            "", hasStock.getNum(), taskEntity.getId(), wareId, 1);
                    wareOrderTaskDetailService.save(entity);
                    StockLockedTo lockedTo = new StockLockedTo();
                    lockedTo.setId(taskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(entity, stockDetailTo);
                    lockedTo.setDetail(stockDetailTo); // 只发id不行，防止回滚以后找不到数据
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", lockedTo);
                    break;
                } else {
                    // 当前仓库锁定失败，尝试锁定下一个仓库
                }
            }

            if (skuStocked == false) {
                // 当前商品所有仓库都没有锁住
                throw new NoStockException(skuId);
            }

        }

        // 3 肯定全部都是锁定成功的
        return true;
    }

    /**
     * @description: 只要有任何异常 都是有消息消费失败
     */
    @Override
    public void unlockStock(StockLockedTo to) {

        StockDetailTo detail = to.getDetail();
        Long detailId = detail.getId();
        /**
         * 查询数据库关于这个订单的锁定库存信息
         *
         * - 有数据,说明锁定库存成功了，但还需要查看订单是否被创建：
         *   - 如果不存在这个订单，说明接下来的业务调用失败，导致订单回滚。之前锁定的库存就要必须要解锁。
         *   - 存在该订单，还需要查看订单的状态：
         *     - 已取消，说明订单超时被系统取消，需要解锁库存
         *     - 没有取消，就不能解锁
         * - 没有数据。说明锁库存失败(库存工作单已全部回滚)导致订单失败
         *   - 无需解锁
         */
        WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(detailId);
        if (byId != null) {
            // 有数据,说明锁定库存成功了，但还需要查看订单是否被创建：
            Long id = to.getId();
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(id);
            String orderSn = taskEntity.getOrderSn(); // 根据订单号查询订单的状态
            R r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 0) {
                // 存在订单 根据订单状态决定是否要解锁库存
                OrderVo data = r.getData(new TypeReference<OrderVo>() {
                });
                // 不存在这个订单/订单已经被取消了，进行解锁库存
                if (data == null || data.getStatus() == 4) {
                    if (byId.getLockStatus() == 1) {
                        // 当前库存工作单详情，状态为 已锁定 时才能解锁库存
                        this.unLockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
                    }
                }
            } else {
                // 远程服务失败 拒绝该消息然后重新消费
                throw new RuntimeException("远程服务失败");
            }

        } else {
            // 没有数据。说明锁库存失败(库存工作单已全部回滚)导致订单失败 无需解锁
        }
    }

    /**
     * @description: 解锁库存方法
     */
    private void unLockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        // 库存解锁
        wareSkuDao.unlockStock(skuId, wareId, num);
        // 更新库存工作单状态为 已解锁
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(taskDetailId);
        entity.setLockStatus(2); // 更新为已解锁状态
        wareOrderTaskDetailService.updateById(entity);
    }

    /**
     * @description: 解锁库存 -- 处理来自订单关闭的消息
     * 防止订单服务卡顿，导致订单状态消息一直改不了，库存消息优先到期。查订单状态新建状态，什么都不做就走了。
     * 导致卡顿的订单永远得不到解锁。
     */
    @Transactional
    @Override
    public void unlockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        // 查询最新库存的状态，防止重复解锁
        WareOrderTaskEntity task = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        // 按照工作单找到所有没有解锁的库存进行解锁
        List<WareOrderTaskDetailEntity> entities = wareOrderTaskDetailService
                .list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", task.getId()).eq("lock_status", 1));
        for (WareOrderTaskDetailEntity entity : entities) {
            this.unLockStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum(),entity.getId());
        }
    }
}