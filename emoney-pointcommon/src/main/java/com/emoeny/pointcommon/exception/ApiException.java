package com.emoeny.pointcommon.exception;


import lombok.Data;

@Data
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 7998862133252755116L;

    private String code;
    private String msg;
    private Object data;

    public ApiException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public ApiException(String code, String msg, Object data) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * error result 中需要data的场景
     *
     * @param apiException
     * @param data
     * @return
     */
    public static ApiException data(ApiException apiException, Object data) {
        return new ApiException(apiException.getCode(), apiException.getMsg(), data);
    }

    /**
     * 判断异常是否同一个
     *
     * @param apiException
     * @return
     */
    public boolean isEqual(ApiException apiException) {
        return this.getCode().equals(apiException.getCode());
    }

    /**
     * 用code判断是否同一个异常
     *
     * @param code
     * @return
     */
    public boolean isEqual(String code) {
        return this.getCode().equals(code);
    }
}
