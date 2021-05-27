package com.emoney.pointweb.repository.dao.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lipengcheng
 * @date 2021-05-07
 */
@Data
public class PointQuestionDO implements Serializable{
    /**
     *
     */
    private Integer id;

    /**
     * 题目类型（1单选；2多选；3其他）
     */
    private Integer questionType;

    /**
     * 题目内容
     */
    private String questionContent;

    /**
     * 题目选项（”|“分隔）
     */
    private String questionOptions;

    /**
     * 正确选项（用数字表示，多个正确选项用“|”分隔）
     */
    private String questionRightoptions;

    /**
     * 希望展示时间，为空则随机展示
     */
    private Date showTime;

    private String publishPlatFormType;

    /**
     * 0:全部 1:小智盈 2:深度资金版 3:掘金版
     */
    private String productVersion;

    private String userGroup;

    /**
     * 是否有效
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
     * 更新时间
     */
    private Date updateTime;
}
