package com.atguigu.rabbitmq.four;

import com.atguigu.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 发布确认模式
 * 1、单个确认模式
 * 2、批量确认
 * 3、异步批量确认
 */
public class ConfirmMessage {

    public static final int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception {
        //1、单个确认模式
//        ConfirmMessage.publishMessageIndividually();//发布1000个单独确认消息耗时735ms
        //2、批量确认
//        ConfirmMessage.publishMessageBatch();//发布1000个批量确认消息耗时139ms
        //3、异步批量确认
        ConfirmMessage.asyncPublishMessage();//发布1000个异步发布确认消息耗时61ms
    }

    public static void publishMessageIndividually() throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();//声明队列
        channel.queueDeclare(queueName,false,false,false,null);
        //开启发布确认
        channel.confirmSelect();
        long begin = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            //单个消息就马上进行发布确认
            boolean flag = channel.waitForConfirms();
            if (flag){
                System.out.println("消息发送成功");
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("发布"+ MESSAGE_COUNT + "个单独确认消息耗时" + (end - begin) + "ms");
    }

    public static void publishMessageBatch() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();//声明队列
        channel.queueDeclare(queueName,false,false,false,null);
        //开启发布确认
        channel.confirmSelect();
        long begin = System.currentTimeMillis();
        //批量发送消息
        int batchSize = 100;
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName,null,message.getBytes());

            //判断达到100条数据，确认一次
            if (i % batchSize == 0){
                //发布确认
                channel.waitForConfirms();
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("发布"+ MESSAGE_COUNT + "个批量确认消息耗时" + (end - begin) + "ms");
    }

    //异步发布确认
    public static void asyncPublishMessage() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();//声明队列
        channel.queueDeclare(queueName,false,false,false,null);
        //开启发布确认
        channel.confirmSelect();
        long begin = System.currentTimeMillis();

        /**
         * 线程安全有序的一个哈希表 适用于高并发的情况下
         * 1、轻松的将序号与消息进行关联 map
         * 2、轻松的批量删除 只要给到序号
         * 3、支持高并发（多线程）
         */
        ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();
        //消息确认成功回调函数
        ConfirmCallback ackCallback = (long var1, boolean var3) -> {
            if (var3){
                //2、删除已经确认的消息，剩下就是未确认的消息
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(var1);
                confirmed.clear();
            } else {
                outstandingConfirms.remove(var1);
            }

            System.out.println("确认的消息" + var1);
        };
        //消息确认失败回调函数
        /**
         * 1、消息的标记
         * 2、是否为批量确认
         */
        ConfirmCallback nackCallback = (long var1, boolean var3) -> {
            System.out.println("未确认的消息" + var1);
        };
        //准备消息的监听器，监听哪些消息成功，失败（异步通知）
        /**
         * 1、监听成功消息
         * 2、监听失败消息
         */
        channel.addConfirmListener(ackCallback,nackCallback);

        //异步发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "消息";
            channel.basicPublish("",queueName, null,message.getBytes("UTF-8"));
            //1、记录下所有要发送的消息
            outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
        }

        long end = System.currentTimeMillis();
        System.out.println("发布"+ MESSAGE_COUNT + "个异步发布确认消息耗时" + (end - begin) + "ms");

    }
}
