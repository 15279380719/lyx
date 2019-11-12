package com.yq.jforgame.controller.rank.service;

import com.alibaba.fastjson.JSONObject;
import com.yq.jforgame.common.Result;


public interface RankService {


    /**
     * @param jsonObject 接收前端请求的json接口
     * @return   返回结果集
     */
    Result entrance(JSONObject jsonObject);


    /**
     * @param jsonObject 排行榜json数据
     * @return  返回结果集
     */
    Result  rank(JSONObject jsonObject);


    /**
     * @param jsonObject  用户分数改变 ,里面肯定有用户的openId 和分数值
     * @return
     */
    Result  playerScoreAdd(JSONObject jsonObject);

}
