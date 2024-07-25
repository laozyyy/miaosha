package com.example.miaosha.service;

import java.util.concurrent.TimeUnit;

public interface IRedisService {

    <T> void setValue(String key, T value);

    <T> T getValue(String key);

    boolean delKey(String key);

    int decr(String key);

    Boolean setNx(String key);

    Boolean setNx(String key, long expired, TimeUnit timeUnit);

}
