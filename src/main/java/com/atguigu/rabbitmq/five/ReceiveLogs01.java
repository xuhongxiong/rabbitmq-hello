package com.atguigu.rabbitmq.five;

import com.atguigu.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

/**
 * 消息的接收
 */
public class ReceiveLogs01 {

    //交换机名称
    public static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明一个交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        //声明一个临时队列
        /**
         * 队列的名称是随机的
         * 当消费者断开与队列的连接的时候，队列就会自动删除
         */
        String queueName = channel.queueDeclare().getQueue();
        /**
         * 绑定交换机与队列
         */
        channel.queueBind(queueName,EXCHANGE_NAME,"");
        System.out.println("等待接收接收消息");

        //接收消息
        DeliverCallback deliverCallback = (String var1, Delivery var2) -> {
            String message = new String(var2.getBody(),"UTF-8");
            System.out.println("ReceiveLogs01控制台打印接收到的消息" + message);
        };

        CancelCallback cancelCallback = var1 -> {};

        channel.basicConsume(queueName,true,deliverCallback,cancelCallback);
    }
}
