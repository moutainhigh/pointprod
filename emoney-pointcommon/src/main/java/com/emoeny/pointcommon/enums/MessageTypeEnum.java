package com.emoeny.pointcommon.enums;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/4/12 15:26
 */
public enum MessageTypeEnum implements EnumBase {
    /**
     * 消息类型
     */
    TYPE0("0", "全部"),
    TYPE1("1", "积分到期"),
    TYPE2("2", "商品上架"),
    TYPE3("3", "待支付"),
    TYPE4("4", "最新活动"),
    TYPE5("5", "意见反馈"),
    TYPE6("6", "实物商品通知"),
    ;
    /**
     * 枚举编号
     */
    private String code;
    /**
     * 枚举详情
     */
    private String msg;

    MessageTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 通过枚举<code>code</code>获得枚举。
     *
     * @param code 枚举编号
     * @return
     */
    public static MessageTypeEnum getResultCodeEnumByCode(String code) {
        for (MessageTypeEnum param : values()) {
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
