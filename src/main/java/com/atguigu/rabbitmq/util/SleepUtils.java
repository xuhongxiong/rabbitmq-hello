package com.atguigu.rabbitmq.util;

public class SleepUtils {
    public static void sleep(int second) {
        try {
            Thread.sleep(1000*second);
        } catch (InterruptedException _ignored)
        {Thread.currentThread().interrupt();
        }
    }
}