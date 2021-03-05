package cn.silince.silincemall.order.web;

import cn.silince.silincemall.order.entity.OrderEntity;
import cn.silince.silincemall.order.service.OrderService;
import cn.silince.silincemall.order.vo.OrderConfirmVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-27 15:28
 **/
@Controller
public class HelloController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
    * @description: 创建订单测试
    */
    @GetMapping("/test/createOrder")
    public String createOrderTest(){
        // 订单下单成功
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(UUID.randomUUID().toString());
        entity.setModifyTime(new Date());

        // 给mq发送消息
        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",entity);
        return "ok";

    }



    @GetMapping("/{page}.html")
    public String listPage(@PathVariable("page") String page) throws ExecutionException, InterruptedException {

        return page;
    }

}
