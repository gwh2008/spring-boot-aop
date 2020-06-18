package com.gwh.axb.entity;

/**
 * Created by gaowenhui on 2020/6/18.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisMessageListener implements MessageListener {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    //key 过期时调用
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("618 onPMessage pattern " + pattern + " " + " " + message);
        String channel = new String(message.getChannel());
        String str = (String) redisTemplate.getValueSerializer().deserialize(message.getBody());
        System.out.println("0618超时监听： "+str);

    }
}


