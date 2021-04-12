package com.emoeny.pointcommon.enums;

/**
 * 积分限额类型
 */
public enum PointLimitTypeEnum implements EnumBase {
    /**
     * 操作成功
     */
    SEND("0", "发送"),
    EXCHANGE("1", "兑换"),
    ;
    /**
     * 枚举编号
     */
    private String code;
    /**
     * 枚举详情
     */
    private String msg;

    PointLimitTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 通过枚举<code>code</code>获得枚举。
     *
     * @param code 枚举编号
     * @return
     */
    public static PointLimitTypeEnum getResultCodeEnumByCode(String code) {
        for (PointLimitTypeEnum param : values()) {
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
    public String code() {
        return code;
    }
}
