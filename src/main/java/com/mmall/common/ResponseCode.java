package com.mmall.common;

/**
 * @author : mengmuzi
 * create at:  2019-02-27  16:17
 * @description:
 */
public enum  ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    ILLEGAL(2,"ILLEGAL_ARGUMENT"),
    NEED_LOGIN(10,"NEED_LOGIN");

    private final int code;
    private final String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode(){
        return code;
    }
    public String getdesc(){
        return desc;
    }
}
