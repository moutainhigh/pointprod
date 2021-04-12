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
