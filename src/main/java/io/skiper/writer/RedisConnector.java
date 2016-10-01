package io.skiper.writer;

import org.apache.spark.streaming.StreamingContext;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;



/**
 * 정해진 형식에 맞춰 Redis에 로그를 작성
 */

public class RedisConnector implements Serializable {

    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    JedisPool jedisPool = null;
    String host = null;

    int port = 6379;

    /**
     * 생성자. 주어진 host로 접속하여 redisPool을 생성.
     */
    public RedisConnector(String host) {
        this.host = host;
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxWaitMillis(1000);
        jedisPool = new JedisPool(this.host, this.port);
    }


    public List<String> getBlackList(String key)
    {
        List<String> result = new ArrayList<String>();
        Jedis jedis = jedisPool.getResource();
        result = jedis.lrange("blacklist", 0, -1);
        jedisPool.returnResourceObject(jedis);
        return result;
    }

    public List<String> zadd(String key, String value, int score)
    {
        List<String> result = new ArrayList<String>();
        Jedis jedis = jedisPool.getResource();
        result = jedis.lrange("blacklist", 0, -1);
        jedisPool.returnResourceObject(jedis);
        return result;
    }
}