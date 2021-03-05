package cn.silince.silincemall.order.listener;

import cn.silince.common.to.mq.SeckillOrderTo;
import cn.silince.silincemall.order.entity.OrderEntity;
import cn.silince.silincemall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @program: SilinceMall
 * @description: 监听商品秒杀成功mq消息 然后创建订单
 * @author: Silince
 * @create: 2021-03-04 15:12
 **/

@Slf4j
@RabbitListener(queues = "order.seckill.order.queue")
@Component
public class OrderSeckillListener {

    @Resource
    private OrderService orderService;

    @RabbitHandler
    public void listener(SeckillOrderTo seckillOrder, Channel channel, Message message) throws IOException {

        try {
            log.info("准备创建秒杀单的详细信息...");
            orderService.createSeckillOrder(seckillOrder);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

}
