package com.yq.jforgame.controller.rank.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yq.jforgame.common.Result;
import com.yq.jforgame.common.StatusCode;
import com.yq.jforgame.controller.rank.service.RankService;
import com.yq.jforgame.dao.PlayerInfoMapper;
import com.yq.jforgame.pojo.PlayInfo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service("rankService")
public class RankServiceImpl   implements RankService {
    @Resource
    private RedisTemplate redisTemplate;
    //其实这边应该注入service,但是只是一个sql 的话就直接注入dao
    @Resource
    private PlayerInfoMapper playerInfoMapper;

    /**
     * 排行榜信息
     */
private String  rankInfo = "player:scoreRank";

    /**
     *    //接收
     *       {
     *           "cmd":"协议,也可以的数字也可以是英文",
     *           "data":{
     *               "username":"张三"    //里面就是前端传给你的
     *          }
     *       }
     *
     *
     *
     * */

    @Override
    public Result entrance(JSONObject jsonObject) {
        //进行任务分发
        if (jsonObject!=null&&jsonObject.size()>0){
            String cmd = jsonObject.getString("cmd");
            JSONObject data = jsonObject.getJSONObject("data");
            if (Strings.isNotEmpty(cmd)&&data!=null&&data.size()>0){
                //其实也不能完全说是工厂模式,但是思想上是的
                switch (cmd){
                    //这个 case  就是前端行为
                    case  "rank":
                        //排行榜
                        return this.rank(jsonObject);
                        //用户分数加减
                    case"playerScoreAdd":
                        return  this.playerScoreAdd(jsonObject);
                    default:
                         return new Result("error", StatusCode.ERROR,"服务器无法理解你的行为");
                }
            }else {
                return new Result("error", StatusCode.ERROR,"服务器接收到的参数不完整");
            }
        }else {
            return new Result("error", StatusCode.ERROR,"服务器接收到的参数为空");
        }
    }

    /**
     * @param jsonObject 排行榜json数据
     * @return 返回排行榜的结果集
     */
    @Override
    public Result rank(JSONObject jsonObject) {
        String cmd = jsonObject.getString("cmd");
        JSONObject data = jsonObject.getJSONObject("data");
        //username 可以不用
        //前端传给你什么?写下openid username ,score
        String openId = data.getString("openId");
        if (Strings.isNotBlank(openId)){
            //返回的结果集,肯定是要用户的排名和拉取的排行榜
            //返回头像昵称和排名和积分吗?就这三个
            //是什么排行榜?积分score
            //定义一个redis的排名行为
            Set<ZSetOperations.TypedTuple<Object>> tuples  = redisTemplate.opsForZSet().reverseRangeWithScores("player:scoreRank", 0, 49);
            if (tuples!=null&&tuples.size()>0){
                //在redis中查询得到的数量
                int size = tuples.size();
                List<String> playerIdList = new ArrayList<>(size);
                List<Double> playerScoreList = new ArrayList<>(size);
                for (ZSetOperations.TypedTuple<Object> tuple : tuples) {
                    //分数
                    Double score = tuple.getScore();
                    //唯一标识,你可以放用户的id  然后去数据库里面进行查询并返回
                    String playerId = (String) tuple.getValue();
                    playerIdList.add(playerId);
                    playerScoreList.add(score);
                }
                if (playerIdList.size()>0){
                    //看起来很多if  else  但是游戏上的逻辑是不能出错的
                    List<PlayInfo> playInfos = playerInfoMapper.playerInfoList(playerIdList);
                    if (playInfos!=null&&playInfos.size()>0){
                        int index = 0 ;
                        for (PlayInfo playInfo :    playInfos) {
                            //数组的下标都是从0 开始的 所以要加1
                            playInfo.setTop(index+1);
                            //java 出来了 ,但是我们需要返回分数给前端
                            playInfo.setScore(playerScoreList.get(index));
                            index++;
                        }
                        return new Result(cmd,StatusCode.OK,playInfos);
                    }else {
                        return new Result(cmd, StatusCode.OK,Collections.emptyList());
                    }
                }else {
                    //redis中没有的话就直接返回一个空数组
                    return new Result(cmd, StatusCode.OK,Collections.emptyList());
                }
            }else {
                //如果没有结果集的话就返回一个空数组,这样前端也好看 "[]"
                return new Result(cmd, StatusCode.OK,Collections.emptyList());
            }
        }else {
            return new Result(cmd, StatusCode.ERROR,"服务器接收到的openId为空");
        }
    }

    /**
     * @param jsonObject 用户分数改变 ,里面肯定有用户的openId 和分数值
     *                   出来的是 2  3  1   就是排名的rank
     * @return  现在就示范一下用户的排行榜功能  我在redis中存入了  1  2  3  个玩家  在mysql中是存在的 所以 我先拉取排行榜
     */
    //测试一下这个加分数  比如数据库中有个玩家没去玩排行榜 ,所以要先在redis中存在才能,模拟一下A玩家存了很多分数
    @Override
    public Result playerScoreAdd(JSONObject jsonObject) {
        String cmd = jsonObject.getString("cmd");
        //获取date里面的对象
        JSONObject data = jsonObject.getJSONObject("data");
        String openId = data.getString("openId");
        Integer score = data.getInteger("score");
        if (Strings.isNotBlank(openId)&&score!=null){
            //获取排名
            Long rank = redisTemplate.opsForZSet().reverseRank(rankInfo, openId);
            //玩家的排名是从1最大开始排下去的 所以永远是大于0
            if (rank!=null&&rank>0){
                redisTemplate.opsForZSet().incrementScore(rankInfo,openId,score);
                rank = redisTemplate.opsForZSet().reverseRank(rankInfo, openId);
                return new Result(cmd,StatusCode.OK,"恭喜玩家"+openId+"加入竞技场成功,加入的积分为:"+score+",加入后的排名为"+rank);
            }else {
                //如果redis中没有的话就是新玩家第一次参加排行榜
                redisTemplate.opsForZSet().add(rankInfo,openId,score);
                rank = redisTemplate.opsForZSet().reverseRank(rankInfo, openId);
                return new Result(cmd,StatusCode.OK,"恭喜玩家"+openId+"首次加入竞技场成功,加入的积分为:"+score+",当前排名为"+rank);
            }
        }else {
            return new Result(cmd, StatusCode.ERROR,"服务器接收到的openId和分数值");
        }
    }
}
