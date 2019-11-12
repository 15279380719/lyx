package com.yq.jforgame.controller.login;

import lombok.Data;

@Data
public class OpenIdJson {
    private String openid;
    private String session_key;
}