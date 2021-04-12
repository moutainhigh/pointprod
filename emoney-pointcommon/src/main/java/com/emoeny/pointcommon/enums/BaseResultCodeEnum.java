package com.emoeny.pointcommon.enums;


public enum BaseResultCodeEnum implements EnumBase{
    /** 操作成功 */
    SUCCESS("200", "操作成功"),

    /** 系统异常*/
    SYSTEM_ERROR("500", "系统异常，请联系管理员！"),
    /** 外部接口调用异常*/
    INTERFACE_SYSTEM_ERROR("501", "外部接口调用异常，请联系管理员！"),
    /** 业务连接处理超时 */
    CONNECT_TIME_OUT("502", "系统超时，请稍后再试!"),
    /** 系统错误 */
    SYSTEM_FAILURE("503", "系统错误"),
    /** 参数为空 */
    NULL_ARGUMENT("504", "参数为空"),
    /** 非法参数 */
    ILLEGAL_ARGUMENT("505", "非法参数"),
    /** 非法状态 */
    ILLEGAL_STATE("506", "非法状态"),
    /** 错误的枚举编码 */
    ENUM_CODE_ERROR("507", "错误的枚举编码"),
    /** 逻辑错误 */
    LOGIC_ERROR("508", "逻辑错误"),
    /**并发异常*/
    CONCURRENT_ERROR("509", "并发异常"),
    /** 非法操作 */
    ILLEGAL_OPERATION("510", "非法操作"),
    /** 重复操作 */
    REPETITIVE_OPERATION("511", "重复操作"),
    /** 无操作权限 */
    NO_OPERATE_PERMISSION("512", "无操作权限"),
    /** 数据异常 */
    DATA_ERROR("513", "数据异常"),

    ACCESS_TOKEN_EMPTY("700","ACCESS_TOKEN 不能为空"),

    ACCESS_TOKEN_EXPIRE("701","ACCESS_TOKEN 已过期"),

    ACCESS_TOKEN_ILLEGAL("703","非法接口访问"),

    FREQUENT_INTERFACE_ACCESS("705","接口访问频繁"),

    LOGIN_CONCURRENT("704","您的账号在别处登陆,被迫下线"),
    ;
    /** 枚举编号 */
    private String code;
    /** 枚举详情 */
    private String msg;

    /**
     * 构造方法
     *
     * @param code         枚举编号
     * @param msg      枚举详情
     */
    private BaseResultCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    /**
     * 通过枚举<code>code</code>获得枚举。
     *
     * @param code         枚举编号
     * @return
     */
    public static BaseResultCodeEnum getResultCodeEnumByCode(String code) {
        for (BaseResultCodeEnum param : values()) {
            if (param.getCode().equals(code)) {
                return param;
            }
        }
        return null;
    }
    /**
     * Getter method for property <tt>code</tt>.
     *
     * @return property value of code
     */
    public String getCode() {
        return code;
    }
    /**
     * Getter method for property <tt>message</tt>.
     *
     * @return property value of message
     */
    public String getMsg() {
        return msg;
    }

    @Override
    public String msg() {
        return msg;
    }

    @Override
    public String code(){return code;}
}