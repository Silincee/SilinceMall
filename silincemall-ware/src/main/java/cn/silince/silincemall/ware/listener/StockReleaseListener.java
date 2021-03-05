package cn.silince.silincemall.ware.listener;

import cn.silince.common.to.mq.OrderTo;
import cn.silince.common.to.mq.StockDetailTo;
import cn.silince.common.to.mq.StockLockedTo;
import cn.silince.common.utils.R;
import cn.silince.silincemall.ware.dao.WareSkuDao;
import cn.silince.silincemall.ware.entity.WareOrderTaskDetailEntity;
import cn.silince.silincemall.ware.entity.WareOrderTaskEntity;
import cn.silince.silincemall.ware.feign.OrderFeignService;
import cn.silince.silincemall.ware.feign.ProductFeignService;
import cn.silince.silincemall.ware.service.WareOrderTaskDetailService;
import cn.silince.silincemall.ware.service.WareOrderTaskService;
import cn.silince.silincemall.ware.service.WareSkuService;
import cn.silince.silincemall.ware.vo.OrderVo;
import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @program: SilinceMall
 * @description: rabbit监听器
 * @author: Silince
 * @create: 2021-03-02 19:08
 **/
@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {

    @Resource
    private WareSkuService wareSkuService;

    /**
     * @description: 库存自动解锁 ：
     *      1 下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚。之前锁定的库存就要自动解锁。
     *      2 锁库存失败(已全部回滚)导致订单失败  无需解锁
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {

        System.out.println("收到解锁库存的消息...");
        try {
            wareSkuService.unlockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false); // 消息消费成功，手动确认
        } catch (Exception e) {
            // 拒绝该消息 重新放到队列中
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }

    /**
    * @description: 处理订单定时关系消息
    */
    @RabbitHandler
    public void handleStockLockedRelease(OrderTo orderTo, Message message, Channel channel) throws IOException {
        System.out.println("订单关闭，准备解锁库存...");
        try {
            wareSkuService.unlockStock(orderTo); // 方法重载
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false); // 消息消费成功，手动确认
        } catch (Exception e) {
            // 拒绝该消息 重新放到队列中
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }


}
