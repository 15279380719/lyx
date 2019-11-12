package com.yq.jforgame.task;


//所以基本上排行榜的功能是实现了,你们是每周多少进行清除?周一几点12中午吗?嗯
//所以需要开个定时器

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 开启定时器任务,清理redis中的分数值 周一几点12中午
 */
@Component
public class RedisTask {
    @Resource
    private RedisTemplate redisTemplate;
    //我好像忘了等下ok
    //这样子就是每周一执行,你可以去查下springboot的定时器 然后redis中的数据就这样子
    @Scheduled(cron = "0 0 12 1 * ?")
    public void redisTaskRank(){
        //大概功能点就是这个样子了  排行榜  定时器 也已经完成  后面就看你逻辑需要进行修改了
        //https://www.cnblogs.com/chenziyu/p/9225233.html  这个是看redisZsetD  基本上排行榜都是要看这个
        //拜拜
        redisTemplate.delete("player:scoreRank");
    }
}
