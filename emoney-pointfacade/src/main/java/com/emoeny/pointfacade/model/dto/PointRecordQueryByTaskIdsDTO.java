package com.emoeny.pointfacade.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/4/2 14:24
 */
@Data
public class PointRecordQueryByTaskIdsDTO {

    @NotNull(message = "用户id不能为空")
    private Long uid;

    @NotNull(message = "任务id不能为空")
    private List<Long> taskIds;
}
