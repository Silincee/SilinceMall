package cn.silince.silincemall.order;

import cn.silince.silincemall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SilincemallOrderApplication.class})
public class SilincemallOrderApplicationTests {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
    * @description: 发送消息 如果发送的消息是对象，必须实现序列化接口
    */
    @Test
    public void sendMessage(){
        OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
        reasonEntity.setId(1L);
        reasonEntity.setCreateTime(new Date());
        rabbitTemplate.convertAndSend("hello.java.exchange","hello.java",reasonEntity);
    }


    /**
    * @description:  AmqpAdmin:管理组件. 创建一个交换机[hello.java.exchange]
    */
    @Test
    public void createExchange() {
        DirectExchange directExchange
                = new DirectExchange("hello.java.exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
    }

    /**
    * @description: 创建队列
    */
    @Test
    public void createQueue(){
        Queue queue = new Queue("hello-java-queue",true,false,false);
        amqpAdmin.declareQueue(queue);
    }

    /**
     * @description: 创建绑定关系
     */
    @Test
    public void createBinding(){
        Binding binding = new Binding("hello-java-queue",
                Binding.DestinationType.QUEUE,"hello.java.exchange",
                "hello.java",null);
        amqpAdmin.declareBinding(binding);
    }
}
