package cn.silince.silincemall.order.listener;

import cn.silince.silincemall.order.entity.OrderEntity;
import cn.silince.silincemall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @program: SilinceMall
 * @description: 定时关单 监听服务
 * @author: Silince
 * @create: 2021-03-02 20:26
 **/
@RabbitListener(queues = "order.release.order.queue")
@Service
public class OrderCloseListener {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listener(OrderEntity entity, Channel channel, Message message) throws IOException {

        System.out.println("收到过期的订单信息：准备关闭订单"+entity.getOrderSn()+"==>"+entity.getId());

        try {
            orderService.closeOrder(entity);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

        // 手动确认消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
