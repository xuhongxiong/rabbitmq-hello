package com.atguigu.rabbitmq.eight;

import com.atguigu.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

public class Consumer02 {
    //死信交换机
    private static final String DEAD_EXCHANGE = "dead_exchange";
    //死信队列
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        System.out.println("等待接受消息");

        DeliverCallback deliverCallback = (String consumerTag, Delivery message) -> {
            System.out.println("Consumer02接收的消息内容是" + new String(message.getBody(),"UTF-8"));
        };
        CancelCallback cancelCallback = var1 -> {};
        channel.basicConsume(DEAD_QUEUE,true,deliverCallback,cancelCallback);
    }
}
