package com.atguigu.rabbitmq.eight;

import com.atguigu.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.impl.AMQBasicProperties;

/**
 * 死信队列  生产者
 */
public class Producer {

    //普通交换机
    private static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        //设置TTL时间 （存活时间） ms
        /*AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                .expiration("10000").build();*/

        //死信消息
        for (int i = 1; i < 11; i++) {
            String message = "info" + i;
            channel.basicPublish(NORMAL_EXCHANGE,"zhangsan",null,message.getBytes());
        }

    }
}
