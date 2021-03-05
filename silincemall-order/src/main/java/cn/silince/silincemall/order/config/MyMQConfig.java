package cn.silince.silincemall.order.config;

import cn.silince.silincemall.order.entity.OrderEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: SilinceMall
 * @description: 容器中的Binding Queue Exchange 都会自动创建(RabbitMq中没有的情况下)
 *                  RabbitMQ中若如果已经存在，@Bean声明属性发生变化也不会覆盖
 * @author: Silince
 * @create: 2021-03-02 13:37
 **/


@Configuration
public class MyMQConfig {



    @Bean
    public Queue orderDelayQueue() {

        Map<String, Object> arguments = new HashMap<>();
        /**
         * x-dead-letter-exchange: order-event-exchange
         * x-dead-letter-routing-key: order.release.order
         * x-message-ttl: 60000
         */
        arguments.put("x-dead-letter-exchange", "order-event-exchange"); // 死信路由
        arguments.put("x-dead-letter-routing-key", "order.release.order"); // 死信路由键
        arguments.put("x-message-ttl", 60000); // 消息过期时间 1min
        //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        Queue queue = new Queue("order.delay.queue", true, false, false, arguments);
        return queue;
    }

    @Bean
    public Queue orderReleaseOrderQueue() {
        Queue queue = new Queue("order.release.order.queue", true, false, false);
        return queue;
    }

    @Bean
    public Exchange orderEventExchange() {
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return new TopicExchange("order-event-exchange", true, false);
    }

    @Bean
    public Binding orderCreateOrderBingding() {
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        //			Map<String, Object> arguments
        return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null);

    }

    @Bean
    public Binding orderReleaseOrderBingding() {
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);
    }


    /**
     * 订单释放直接和库存释放进行绑定
     *
     * @return
     */
    @Bean
    public Binding orderReleaseOtherBingding() {
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#",
                null);
    }


    /**
    * @description: 秒杀单监听队列 用于消峰
    */
    @Bean
    public Queue orderSeckillOrderQueue() {
        //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        return new Queue("order.seckill.order.queue", true, false, false);
    }
    /**
     * @description: 秒杀单监听队列的绑定
     */
    @Bean
    public Binding orderSeckillOrderQueueBinding() {
        return new Binding("order.seckill.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.seckill.order",
                null);
    }

}



