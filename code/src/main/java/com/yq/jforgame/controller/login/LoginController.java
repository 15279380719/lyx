package com.yq.jforgame.controller.login;

import com.alibaba.fastjson.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yq.jforgame.utils.HttpUtil;
import com.yq.jforgame.controller.login.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;


@RestController
@RequestMapping(value = "/user", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
public class  LoginController{

    //先别用这个
    @Value("${app.appID}")
    private String appID;
    @Value("${app.appSecret}")
    private String appSecret;
    @Resource
    private LoginService loginService;
private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @RequestMapping("/login")
    public String  userLogin(@RequestBody JSONObject jsonObject ,String code)  throws IOException {
        logger.info("appId  "+appID);
        logger.info("appSecret  "+appSecret);
     // return   loginService.entrance(jsonObject);
        String result = "";
        try{//请求微信服务器，用code换取openid
            result = HttpUtil.doGet(
                    "https://api.weixin.qq.com/sns/jscode2session?appid="
                            + this.appID + "&secret="
                            + this.appSecret + "&js_code="
                            + code
                            + "&grant_type=authorization_code", null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        OpenIdJson openIdJson = mapper.readValue(result,OpenIdJson.class);
        System.out.println(result.toString());
        System.out.println(openIdJson.getOpenid());
        return result;
    }
}

