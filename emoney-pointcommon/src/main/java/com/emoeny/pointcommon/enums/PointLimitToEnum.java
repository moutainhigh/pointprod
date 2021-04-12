package com.emoeny.pointcommon.enums;

/**
 * 积分限额对象
 */
public enum PointLimitToEnum implements EnumBase {
    /**
     * 操作成功
     */
    SYSTEM("0", "系统"),
    PERSONAL("1", "个人"),
    ;
    /**
     * 枚举编号
     */
    private String code;
    /**
     * 枚举详情
     */
    private String msg;

    PointLimitToEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 通过枚举<code>code</code>获得枚举。
     *
     * @param code 枚举编号
     * @return
     */
    public static PointLimitToEnum getResultCodeEnumByCode(String code) {
        for (PointLimitToEnum param : values()) {
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
