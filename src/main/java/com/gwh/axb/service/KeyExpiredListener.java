package com.gwh.axb.service;

import redis.clients.jedis.JedisPubSub;

/**
 * Created by gaowenhui on 2020/6/18.
 */
public class KeyExpiredListener extends JedisPubSub {
    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        System.out.println("onPSubscribe " + pattern + " " + subscribedChannels);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        System.out.println(
                "pattern = [" + pattern + "], channel = [" + channel + "], message = [" + message + "]");
    }
}