package com.yq.jforgame.common;

/**
 * 返回结果集的状态码
 */
public interface StatusCode {
    /**
     *  good
     */
    static final int OK=20000;
    /**
     *    错误
     */
    static final int ERROR =20001;
    /**
     *   登陆失败
     */
    static final int LOGINERROR =20002;
    /**
     * 访问错误
     */
    static final int ACCESSERROR =20003;
    /**
     *我也不知道是什么错误
     */
    static final int REMOTEERROR =20004;
    /**
     * 重复错误
     */
    static final int REPERROR =20005;

    /**
     * 空参数
     */
    static final int EMPTY=20006;
}
