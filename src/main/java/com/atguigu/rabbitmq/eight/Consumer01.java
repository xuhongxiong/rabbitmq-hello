package com.atguigu.rabbitmq.eight;

import com.atguigu.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列  消费者01
 */
public class Consumer01 {

    //普通交换机
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    //死信交换机
    private static final String DEAD_EXCHANGE = "dead_exchange";
    //普通队列
    private static final String NORMAL_QUEUE = "normal_queue";
    //死信队列
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        //声明对列
        Map<String, Object> arguments = new HashMap<>();
        //过期时间 ms
        //arguments.put("x-message-ttl",10000);
        //正常队列设置过期之后死信交换机
        arguments.put("x-dead-letter-exchange",DEAD_EXCHANGE);
        //设置死信routingKey
        arguments.put("x-dead-letter-routing-key","lisi");
        //设置正常队列长度的限制
//        arguments.put("x-max-length",6);

        //设置队列的最大优先级 最大可以设置到 255 官网推荐 1-10 如果设置太高比较吃内存和 CPU
        arguments.put("x-max-priority",10);

        channel.queueDeclare(NORMAL_QUEUE,false,false,false,arguments);
        //死信队列
        channel.queueDeclare(DEAD_QUEUE,false,false,false,null);
        //绑定
        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"zhangsan");
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"lisi");

        System.out.println("等待接收消息.....");

        DeliverCallback deliverCallback = (String consumerTag, Delivery message) -> {
            String msg = new String(message.getBody(), "UTF-8");
            if (msg.equals("info5")){
                System.out.println("Consumer01接收的消息内容是" + msg + "：此消息是被C1拒收");
                channel.basicReject(message.getEnvelope().getDeliveryTag(),false);
            } else {
                System.out.println("Consumer01接收的消息内容是" + msg);
                channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
            }
            System.out.println("123");
        };
        CancelCallback cancelCallback = var1 -> {};
        //拒绝（开启手动应答）
        channel.basicConsume(NORMAL_QUEUE,false,deliverCallback,cancelCallback);

    }
}
