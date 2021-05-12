package com.emoney.pointweb.repository.dao.entity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class PointTaskConfigInfoVO {
    private Integer id;
    private String taskId;
    private String subId;
    private Integer taskType;
    private String taskName;
    private Float taskPoints;
    private Date taskStartTime;
    private Date taskEndTime;
    private Date activationStartTime;
    private Date activationEndTime;
    private Date expireStartTime;
    private Date expireEndTime;
    private Boolean isDirectional;
    private Boolean isDailyTask;
    private String productVersion;
    private String publishPlatFormType;
    private Boolean isShowInHomePage;
    private Integer taskOrder;
    private Integer dailyJoinTimes;
    private String pcRedirectUrl;
    private String appRedirectUrl;
    private String wechatRedirectUrl;
    private String taskRemark;
    private String taskButtonText;
    private Boolean isBigImg;
    private String pcTaskImgUrl;
    private String appTaskImgUrl;
    private String wechatTaskImgUrl;
    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;
    private String remark;
    private String userGroup;
    private Boolean sendType;
}
