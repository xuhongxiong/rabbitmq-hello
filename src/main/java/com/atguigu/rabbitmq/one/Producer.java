package com.atguigu.rabbitmq.one;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 生产者
 */
public class Producer {

    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        //创建工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //工厂IP
        connectionFactory.setHost("192.168.138.132");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("123");
        //创建连接
        try {
            Connection connection = connectionFactory.newConnection();
            //获取信道
            Channel channel = connection.createChannel();
            /**
             * 创建队列
             * 1、对列名称
             * 2、队列里消息是否持久化，默认情况消息存储在内存中
             * 3、该队列是否只供一个消费者进行消费，是否进行消息共享，true可以多个消费者消费
             * 4、是否自动删除，最后一个消费者断开连接以后，该队一句是否自动删除 true自动删除
             * 5、其他参数
             */
            //优先级队列
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("x-max-priority",10);
            //args.put("x-queue-mode", "lazy") 惰性队列
            channel.queueDeclare(QUEUE_NAME,false,false,false,arguments);
            //发消息
//            String message = "hello word";
            /**
             * 发送消息
             * 1、发送到哪个交换机
             * 2、路由的key值是哪个，本次是队列名称
             * 3、其他参数信息
             * 4、发送消息的消息体
             */
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().priority(5).build();
            for (int i = 1; i < 11; i++) {
                String message = "info" + i;
                if (i == 5){
                    channel.basicPublish("",QUEUE_NAME,properties,message.getBytes());
                } else {
                    channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
                }
            }
            System.out.println("消息发送完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
