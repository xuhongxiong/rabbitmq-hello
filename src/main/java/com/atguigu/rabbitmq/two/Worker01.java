package com.atguigu.rabbitmq.two;

import com.atguigu.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

/**
 * 工作线程01
 */
public class Worker01 {

    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        DeliverCallback deliverCallback = (String var1, Delivery var2) -> {
            String message = new String(var2.getBody());
            System.out.println("接受到的消息：" + message);
        };
        CancelCallback cancelCallback = var1 -> {
            System.out.println("消息消费被中断" + var1);
        };
        System.out.println("C2等待接收消息...");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
