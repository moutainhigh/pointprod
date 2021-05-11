package com.emoeny.pointcommon.enums;

/**
 * 积分记录状态
 */
public enum PointRecordStatusEnum implements EnumBase {
    /**
     * 操作成功
     */
    UNCLAIMED("1", "待领取"),
    FINISHED("2", "已完成"),
    CONVERTED("3", "已兑换"),
    CANCELED("4", "已取消"),
    ;
    /**
     * 枚举编号
     */
    private String code;
    /**
     * 枚举详情
     */
    private String msg;

    PointRecordStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 通过枚举<code>code</code>获得枚举。
     *
     * @param code 枚举编号
     * @return
     */
    public static PointRecordStatusEnum getResultCodeEnumByCode(String code) {
        for (PointRecordStatusEnum param : values()) {
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
