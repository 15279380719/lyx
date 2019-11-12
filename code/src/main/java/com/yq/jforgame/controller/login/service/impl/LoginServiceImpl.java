package com.yq.jforgame.controller.login.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yq.jforgame.common.Result;
import com.yq.jforgame.common.StatusCode;
import com.yq.jforgame.controller.login.service.LoginService;
import com.yq.jforgame.dao.PlayerInfoMapper;
import com.yq.jforgame.pojo.PlayInfo;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
@Data
@Service
public class LoginServiceImpl  implements LoginService {
    @Resource
    private PlayerInfoMapper playerInfoMapper;
    private String openId;

    private String sKey;

    private Long createTime;

    private Long lastVisitTime;

    private String sessionKey;

    private String city;

    private String province;

    private String country;

    private Integer gender;

    private String source;

    private String nickName;

    private String avatarUrl;

    @Override
    public Result entrance(JSONObject jsonObject) {

        //进行任务分发
        if (jsonObject != null && jsonObject.size() > 0) {
            String cmd = jsonObject.getString("cmd");
            JSONObject data = jsonObject.getJSONObject("data");
            if (Strings.isNotEmpty(cmd) && data != null && data.size() > 0) {
                //其实也不能完全说是工厂模式,但是思想上是的
                switch (cmd) {

                    case "login":
                        //登入
                        return this.login(jsonObject);
                    default:
                        return new Result("error", StatusCode.ERROR, "服务器无法理解你的行为");
                }
            } else {
                return new Result("error", StatusCode.ERROR, "服务器接收到的参数不完整");
            }
        } else {
            return new Result("error", StatusCode.ERROR, "服务器接收到的参数为空");
        }
    }

    private Result login(JSONObject jsonObject) {
        String cmd = jsonObject.getString("cmd");
        JSONObject data = jsonObject.getJSONObject("data");//json转换data
        String openId = data.getString("openId");//
        if (Strings.isNotBlank(openId)) {
            PlayInfo playInfo = playerInfoMapper.selectByPrimaryKey(openId);
            if (playInfo != null) {
                //返回逻辑

                return new Result(cmd, StatusCode.OK, playInfo);
            } else {
                //创建逻辑
                //新用户添加
                PlayInfo insert_pl = new PlayInfo();
                insert_pl.setOpenId(openId);
                insert_pl.setSource(source);
                insert_pl.getNickName();
                insert_pl.setCity(city);
                insert_pl.setSKey(sKey);
                insert_pl.setLastVisitTime(lastVisitTime);
                insert_pl.setSessionKey(sessionKey);
                insert_pl.setProvince(province);
                insert_pl.setCountry(country);
                insert_pl.setCreateTime(createTime);
                insert_pl.setGender(gender);
                insert_pl.setAvatarUrl(avatarUrl);
                int flag = playerInfoMapper.insertSelective(insert_pl);


                //  new 对象 set进去

                    return new Result(cmd, StatusCode.OK, null);

            }
        } else {
            //后期再换成code
            return new Result(cmd, StatusCode.ERROR, "服务器接收不到openId");
        }
    }

}
