package com.atguigu.rabbitmq.six;

import com.atguigu.rabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class EmitLogDirect {

    //交换机名称
    public static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);//路由模式（直接）

        Map<String, String> map = new HashMap<>();
        map.put("info","info消息");
        map.put("warning","warning消息");
        map.put("error","error消息");
        for (Map.Entry<String, String> entry : map.entrySet()){
            String key = entry.getKey();
            String message = entry.getValue();
            channel.basicPublish(EXCHANGE_NAME, key, null, message.getBytes("UTF-8"));
            System.out.println("生产者发送消息" + message);
        }
    }
}
