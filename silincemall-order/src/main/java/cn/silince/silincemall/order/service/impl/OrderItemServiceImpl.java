package cn.silince.silincemall.order.service.impl;

import cn.silince.silincemall.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.silince.common.utils.PageUtils;
import cn.silince.common.utils.Query;

import cn.silince.silincemall.order.dao.OrderItemDao;
import cn.silince.silincemall.order.entity.OrderItemEntity;
import cn.silince.silincemall.order.service.OrderItemService;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
    * @description: 声明需要监听的所有队列
     * 1 Message message 原生消息详细信息 包含了消息头和消息体
     * 2 T<发送的消息的类型> OrderReturnReasonEntity content;
     * 3 Channel channel 当前传输数据的通道
    */

//    @RabbitListener(queues = {"hello-java-queue"})
    public void receiveMessage(Message message,
                               OrderReturnReasonEntity content,
                               Channel channel){
        byte[] body = message.getBody(); // 消息体
        MessageProperties messageProperties = message.getMessageProperties(); // 消息头属性
        System.out.println("接收到的消息内容: "+message+"==>类型: "+content);

        // 签收消息 非批量模式
        long deliveryTag = message.getMessageProperties().getDeliveryTag();// channel内按顺序自增的
        try {
            channel.basicAck(deliveryTag,false);
        } catch (IOException e) {
            // 网络中断
        }
    }

}