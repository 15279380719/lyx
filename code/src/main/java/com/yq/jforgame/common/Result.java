package com.yq.jforgame.common;


import lombok.Data;

/**
 * 结果集
 */
@Data
//开启lombok,简化代码
public class Result {
    private String cmd;
    private Integer code;
    private Object data;

    public Result() {
        super();
    }


    public Result(String cmd, Integer code ,Object data) {
        super();
        this.code=code;
        this.cmd = cmd;
        this.data = data;
    }
    public Result(String cmd, Object data) {
        super();
        this.cmd = cmd;
        this.data = data;
    }
}
