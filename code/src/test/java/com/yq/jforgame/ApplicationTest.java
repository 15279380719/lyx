package com.yq.jforgame;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {
    @Resource
    private RedisTemplate redisTemplate;
    @Test
    public  void   redisTest(){
        //数据源尽量少动  我都是用配置的方式去配置的 所以你就不要去配置数据源  当然了 如果项目需要的话是必须要配置的
        //例如多分服合服
        //我给你示范一下排行榜  你在场的话打个字ok
        //你先把redis连接上去吧
        //我怀疑是你配置出了问题
        //player:scoreRank
        redisTemplate.opsForZSet().add("player:scoreRank","1",1020);
        redisTemplate.opsForZSet().add("player:scoreRank","2",1100);
        redisTemplate.opsForZSet().add("player:scoreRank","3",1040);
    }
}
