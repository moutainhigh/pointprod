package com.emoeny.pointcommon.enums;

public interface EnumBase {
    /**
     * 获取枚举名(建议与enumCode保持一致)
     *
     * @return
     */
    public String code();
    /**
     * 获取枚举消息
     *
     * @return
     */
    public String msg();
}
