package com.yq.jforgame.pojo;


import lombok.Data;

@Data
public class PlayInfo {
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


    /**
     * 排名
     */
    private Integer  top;


    /**
     * 分数值
     */
    private  Double  score;



}
