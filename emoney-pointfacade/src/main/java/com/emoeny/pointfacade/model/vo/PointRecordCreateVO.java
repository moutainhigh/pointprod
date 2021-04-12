package com.emoeny.pointfacade.model.vo;

import lombok.Data;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/18 9:04
 */

@Data
public class PointRecordCreateVO {
    private Long id;
    private Long taskId;
    private String subId;
    private Float taskPoint;
    private String taskName;
    private Integer pointStatus;
}
