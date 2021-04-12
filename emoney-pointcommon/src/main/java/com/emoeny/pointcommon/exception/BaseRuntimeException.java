package com.emoeny.pointcommon.exception;

import com.emoeny.pointcommon.enums.EnumBase;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BaseRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 7998866133252755116L;

    /** 异常码 */
    protected String          code;
    /** 异常信息 */
    protected String          message;
    /** 异常枚举 */
    protected EnumBase        errorEnum;
    /**
     * 空构造器。
     */
    public BaseRuntimeException() {
        super();
    }
    /**
     * 构造器。
     *
     * @param message 消息
     */
    public BaseRuntimeException(String message) {
        super(message);
        this.message = message;
    }
    /**
     * 构造器。
     *
     * @param baseEnum 消息
     */
    public BaseRuntimeException(EnumBase baseEnum) {
        super(baseEnum.msg());
        this.code = baseEnum.code();
        this.message = baseEnum.msg();
        this.errorEnum = baseEnum;
    }
    /**
     * 构造器。
     *
     * @param message 消息
     */
    public BaseRuntimeException(EnumBase baseEnum, String message) {
        super(message);
        this.code = baseEnum.code();
        this.errorEnum = baseEnum;
        this.message = message;
    }
    /**
     * 构造器。
     *
     * @param message  消息
     */
    public BaseRuntimeException(String errorCode, String message) {
        super(message);
        this.code = errorCode;
        this.message = message;
    }
    /**
     * 构造器。
     *
     * @param cause 原因
     */
    public BaseRuntimeException(Throwable cause) {
        super(cause);
    }
    /**
     * 构造器。
     *
     * @param message 消息
     * @param cause  原因
     */
    public BaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * 构造器。
     *
     * @param message
     *            消息
     * @param cause
     *            原因
     */
    public BaseRuntimeException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode;
        this.message = message;
    }
    /**
     * @see java.lang.Throwable#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    public String getCode() {
        return code;
    }
    @Override
    public String getMessage() {
        return message;
    }
    /**
     * Getter method for property <tt>errorEnum</tt>.
     *
     * @return property value of errorEnum
     */
    public EnumBase getErrorEnum() {
        return errorEnum;
    }
}
