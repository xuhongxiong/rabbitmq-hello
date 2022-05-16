package com.atguigu.rabbitmq.one;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消费者
 */
public class Consumer {

    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.138.132");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("123");

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            /**
             * 消费者消费消息
             * 1、消费哪个队列
             * 2、消费成功之后是否要自动应答 true自动应答
             * 3、消费者未成功消费的回调
             * 4、消费者取消消费的回调
             */
            DeliverCallback deliverCallback = (String var1, Delivery var2) -> {
                String message = new String(var2.getBody());
                System.out.println(message);
            };
            CancelCallback cancelCallback = (String var1) -> {
                System.out.println("消息消费被中断");
            };
            channel. basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
