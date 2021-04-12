package com.emoeny.pointfacade.model.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class PointRecordRecevieDTO {

    @NotNull(message = "用户id不能为空")
    private Long uid;

    @NotNull(message = "积分记录id不能为空")
    private Long id;
}
