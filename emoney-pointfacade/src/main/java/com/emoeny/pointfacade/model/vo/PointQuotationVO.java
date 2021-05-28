package com.emoeny.pointfacade.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/4/8 20:14
 */
@Data
public class PointQuotationVO {
    /**
     * 主键Id
     */
    private Integer id;

    /**
     * 语录内容
     */
    private String content;

    private Date showTime;

    private String publishPlatFormType;

    /**
     * 0:全部 1:小智盈 2:深度资金版 3:掘金版
     */
    private String productVersion;

    private String userGroup;

    /**
     * 是否有效（1有效；0无效）
     */
    private Boolean isValid;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}
