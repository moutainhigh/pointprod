package com.emoeny.pointfacade.model.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/4/12 15:34
 */
@Data
@ToString
public class PointMessageVO {
    private Integer id;
    private Long uid;
    private Integer msgType;
    private String msgContent;
    private String msgSrc;
    private String msgExt;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String createBy;
    private String updateBy;
    private String remark;
}
