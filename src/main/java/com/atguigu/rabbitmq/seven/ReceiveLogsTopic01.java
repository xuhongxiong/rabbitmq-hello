package com.atguigu.rabbitmq.seven;

import com.atguigu.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.*;

public class ReceiveLogsTopic01 {
    //交换机名称
    public static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明一个交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //声明队列
        String queueName = "Q1";
        channel.queueDeclare(queueName,false,false,false,null);

        /**
         * 绑定交换机与队列
         */
        channel.queueBind(queueName,EXCHANGE_NAME,"*.orange.*");
        System.out.println("等待接收接收消息");

        //接收消息
        DeliverCallback deliverCallback = (String var1, Delivery var2) -> {
            String message = new String(var2.getBody(),"UTF-8");
            System.out.println("ReceiveLogsTopic01控制台打印接收到的消息" + message);
        };

        CancelCallback cancelCallback = var1 -> {};

        channel.basicConsume(queueName,true,deliverCallback,cancelCallback);
    }
}
