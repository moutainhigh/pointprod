package com.emoney.pointweb.repository.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @author lipengcheng
 * @date 2021-04-12
 */
@Data
public class PointFeedBackDO {
    private Integer id;
    /*
     * 类型 1产品建议；2使用心得；3提问咨询；4其他建议
     */
    private Integer feedType;
    private String pid;
    private String account;
    private String mobileX;
    private String email;
    private String suggest;
    private String imgUrl;
    /*
     * 是否采纳 0未采纳；1已采纳
     */
    private Integer status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String createBy;
    private String updateBy;
    /*
     * 回复意见
     */
    private String remark;

    private String adoptRemark;
}
