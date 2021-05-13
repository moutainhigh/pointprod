package com.emoeny.pointfacade.model.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class PointRecordCreateDTO {
    @NotNull(message = "用户id不能为空")
    private Long uid;

    @NotNull(message = "任务id不能为空")
    private Long taskId;

    private String taskName;

    @NotNull(message = "客户端类型不能为空")
    private Integer platform;

    private String subId;

    private String pid;

    private String emNo;

    private String remark;

    private Float manualPoint;

    private Integer lockDays;
}
