package com.emoeny.pointfacade.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author lipengcheng
 * @date 2021-04-19
 */
@Data
public class PointFeedBackCreateDTO {
    @NotNull(message = "用户id不能为空")
    private Long uid;
    @NotNull(message = "反馈类型不能为空")
    private Integer feedType;
    private String email;
    @NotNull(message = "建议不能为空")
    private String suggest;
    @NotNull(message = "任务id不能为空")
    private Long taskId;

    private String subId;

    @NotNull(message = "客户端类型不能为空")
    private Integer platform;


    private String imgUrl;
    private String account;
    private String pid;
    private String mobileX;
    private Integer status;
}
