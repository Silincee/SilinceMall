package cn.silince.silincemall.ware.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-26 22:29
 **/
@Configuration
public class MyRabbitConfig {



    /**
     * 使用JSON序列化机制，进行消息转换
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }


//    @RabbitListener(queues = "stock.release.stock.queue")
//    public void  handle(Message message){
//
//    }

    /**
    * @description: 创建库存服务的交换机 stock-event-exchange(topic类型)
    */
    @Bean
    public Exchange stockEventExchange(){
        return  new TopicExchange("stock-event-exchange",true,false);
    }

    /**
    * @description: 创建库存释放队列 stock.release.stock.queue
    */
    @Bean
    public Queue stockReleaseStockQueue(){
        //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        return new Queue("stock.release.stock.queue",true,false,false);
    }

    /**
     * @description: 创建库存延时队列 stock.delay.queue
     */
    @Bean
    public Queue stockDelayQueue(){
        /**
         * x-dead-letter-exchange: stock-event-exchange
         * x-dead-letter-routing-key: order.release.order
         * x-message-ttl: 60000
         */
        Map<String,Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange","stock-event-exchange");
        args.put("x-dead-letter-routing-key","stock.release");
        args.put("x-message-ttl",120000);
        return new Queue("stock.delay.queue",true,false,false,args);
    }

    
    /** 
    * @description: 创建绑定
    */ 
    @Bean
    public Binding stockReleaseBinding(){
        /**
         * String destination, DestinationType destinationType, String exchange, String routingKey,
         * 			Map<String, Object> arguments
         */
        return  new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#",
                null);
    }

    @Bean
    public Binding stockLockedBinding(){
        /**
         * String destination, DestinationType destinationType, String exchange, String routingKey,
         * 			Map<String, Object> arguments
         */
        return  new Binding("stock.delay.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.locked",
                null);
    }

}




