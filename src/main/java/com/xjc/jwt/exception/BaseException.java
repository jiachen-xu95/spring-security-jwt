package com.xjc.jwt.exception;

import com.xjc.jwt.enums.ErrorEnum;

/**
 * @Version 1.0
 * @ClassName BaseException
 * @Author jiachenXu
 * @Date 2020/11/8
 * @Description
 */
public class BaseException extends RuntimeException {

    private final ErrorEnum errorEnum;

    private String msg;

    public BaseException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage( ));
        this.errorEnum = errorEnum;
    }

    public BaseException(ErrorEnum errorEnum, String msg) {
        super(errorEnum.getMessage( ));
        this.errorEnum = errorEnum;
        this.msg = msg;
    }

    BaseException(ErrorEnum errorEnum, String msg, Throwable cause) {
        super(errorEnum.getMessage( ), cause);
        this.errorEnum = errorEnum;
        this.msg = msg;
    }

    public ErrorEnum getErrorEnum() {
        return errorEnum;
    }

    public String getMsg() {
        return msg;
    }
}
