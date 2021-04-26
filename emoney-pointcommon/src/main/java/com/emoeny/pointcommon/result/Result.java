package com.emoeny.pointcommon.result;


import com.emoeny.pointcommon.enums.BaseResultCodeEnum;
import com.emoeny.pointcommon.enums.EnumBase;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -1705884776333329123L;

    /**
     * 成功失败标识 (悲观模式默认失败)
     */
    protected boolean success = false;
    /**
     * 结果码
     */
    protected String code = BaseResultCodeEnum.SYSTEM_ERROR.getCode();
    /**
     * 返回信息
     */
    protected String msg = BaseResultCodeEnum.SYSTEM_ERROR.getMsg();

    private T data;

    /**
     * 构造方法
     *
     * @param success 成功标识
     */
    public Result(boolean success) {
        this.success = success;
        if (success) {
            this.code = BaseResultCodeEnum.SUCCESS.getCode();
            this.msg = BaseResultCodeEnum.SUCCESS.getMsg();
        }
    }

    public Result() {
    }

    protected Result(boolean success, String code, String msg) {
        this.success = success;
        this.code = code;
        this.msg = msg;
    }

    protected Result(boolean success, T data, String code, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.success = success;
    }

    public static <T> Result<T> buildErrorResult(String message) {
        return new Result<>(false, null, message);
    }

    /**
     * 组装error result
     *
     * @param code
     * @param message
     * @return
     */
    public static <T> Result<T> buildErrorResult(String code, String message) {
        return new Result<>(false, code, message);
    }

    public static <T> Result<T> buildErrorResult(String code, String message, T data) {
        return new Result<>(false, data, code, message);
    }


    /**
     * 组装seccess result
     *
     * @param data
     * @return
     */
    public static <T> Result<T> buildSuccessResult(T data) {
        return new Result<>(true, data, BaseResultCodeEnum.SUCCESS.getCode(), BaseResultCodeEnum.SUCCESS.getMsg());
    }

    public static <T> Result<T> buildSuccessResult() {
        return new Result<>(true);
    }

    public static <T> Result<T> buildSuccessResult(String code, String message, T data) {
        return new Result<>(true, data, code, message);
    }
    /**
     * 设置结果集
     *
     * @param success
     * @param resultCode
     */
    public void markResult(boolean success, EnumBase resultCode) {
        markResult(success, resultCode, null);
    }

    /**
     * 由结果码枚举设置结果集
     *
     * @param resultCode
     */
    public void markResult(EnumBase resultCode) {
        markResult(null, resultCode, null);
    }

    /**
     * 由结果码枚举和message设置结果集
     *
     * @param resultCode
     * @param message
     */
    public void markResult(EnumBase resultCode, String message) {
        markResult(null, resultCode, message);
    }

    public void markResult(Boolean success, EnumBase resultCode, String message) {
        if (null != success) {
            this.success = success;
        }
        if (null != resultCode) {
            this.code = resultCode.code();
            this.msg = resultCode.msg();
        }
        if (StringUtils.isNotBlank(message)) {
            this.msg = message;
        }
    }

    @Override
    public String toString() {
        return "{\"success\":\"" + success + "\",\"code\":\"" + code + "\",\"message\":\"" + msg + "\"}";
    }

}