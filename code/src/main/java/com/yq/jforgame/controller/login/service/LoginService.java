package com.yq.jforgame.controller.login.service;

import com.alibaba.fastjson.JSONObject;
import com.yq.jforgame.common.Result;

public interface LoginService {

    /**
     * @param jsonObject 接收前端请求的json接口
     * @return   返回结果集
     */
    Result entrance(JSONObject jsonObject);


}
