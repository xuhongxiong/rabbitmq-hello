package com.atguigu.rabbitmq.three;

import com.atguigu.rabbitmq.util.RabbitMqUtils;
import com.atguigu.rabbitmq.util.SleepUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

/**
 * 消费者03
 */
public class Work03 {
    public static final String QUEUE_NAME = "ACK_QUEUE";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("C1等待接收消息处理时间较短");
        //设置不公平分发
//        int prefetchCount = 1;
        int prefetchCount = 2; // 预取值
        channel.basicQos(prefetchCount);
        boolean autoAck = false;
        DeliverCallback deliverCallback = (String var1, Delivery var2) -> {
            //睡一秒
            SleepUtils.sleep(1);
            System.out.println("接收到的消息：" + new String(var2.getBody(), "UTF-8"));
            /**
             * 手动应答
             * 1、消息的标记 tag
             * 2、是否批量应答 false：不批量应答
             */
            channel.basicAck(var2.getEnvelope().getDeliveryTag(), false);
        };
        CancelCallback cancelCallback = var1 -> {
            System.out.println(var1 + "消费者取消消费接口回调逻辑");
        };
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, cancelCallback);
    }
}
