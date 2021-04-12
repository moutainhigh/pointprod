package com.emoeny.pointfacade.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ToString
public class SignInRecordCreateDTO {
    @NotNull(message = "用户id不能为空")
    private Long uid;
    @NotNull(message = "客户端类型不能为空")
    private Integer platform;
}
