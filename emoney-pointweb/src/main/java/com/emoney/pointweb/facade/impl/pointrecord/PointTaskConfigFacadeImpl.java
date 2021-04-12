package com.emoney.pointweb.facade.impl.pointrecord;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.result.userperiod.Software;
import com.emoeny.pointcommon.result.userperiod.UserPeriodResult;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointfacade.facade.pointrecord.PointTaskConfigFacade;
import com.emoeny.pointfacade.model.vo.PointTaskConfigVO;
import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;
import com.emoney.pointweb.service.biz.PointTaskConfigInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Validated
@Slf4j
public class PointTaskConfigFacadeImpl implements PointTaskConfigFacade {

    @Autowired
    private PointTaskConfigInfoService pointTaskConfigInfoService;

    @Override
    public Result<List<PointTaskConfigVO>> queryPointTaskConfigs(@NotNull(message = "用户id不能为空") Long uid, @NotNull(message = "产品版本不能为空") String productVersion, @NotNull(message = "发布平台不能为空") String publishPlatFormType) {
        try {
            List<PointTaskConfigInfoDO> pointTaskConfigInfoDOS = pointTaskConfigInfoService.getAllEffectiveTasks(new Date(), uid, productVersion, publishPlatFormType);
            return Result.buildSuccessResult(JsonUtil.copyList(pointTaskConfigInfoDOS, PointTaskConfigVO.class));
        } catch (Exception e) {
            log.error("queryPointTaskConfigs error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<List<PointTaskConfigVO>> queryPointTaskConfig(@NotNull(message = "任务id不能为空") Long taskId, String subId) {
        try {
            return Result.buildSuccessResult(JsonUtil.toBeanList(JSON.toJSONString(pointTaskConfigInfoService.getByTaskIdAndSubId(taskId, subId)), PointTaskConfigVO.class));
        } catch (Exception e) {
            log.error("queryPointTaskConfig error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<List<PointTaskConfigVO>> queryPointTaskConfigsByTaskType(@NotNull(message = "用户id不能为空") Long uid, @NotNull(message = "产品版本不能为空") String productVersion, @NotNull(message = "发布平台不能为空") String publishPlatFormType, @NotNull(message = "任务类型不能为空") Integer taskType) {
        try {
            List<PointTaskConfigInfoDO> pointTaskConfigInfoDOS = pointTaskConfigInfoService.getTasksByTaskType(taskType, uid, productVersion, publishPlatFormType);
            return Result.buildSuccessResult(JsonUtil.copyList(pointTaskConfigInfoDOS, PointTaskConfigVO.class));
        } catch (Exception e) {
            log.error("queryPointTaskConfigsByTaskType error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<List<PointTaskConfigVO>> queryPointTaskConfigsByTaskIds(@NotNull(message = "taskids不能为空") String taskIds) {
        try {
            List<Long> listTaskIds = new ArrayList<>();
            for (String taskId : taskIds.split(",")
            ) {
                listTaskIds.add(Long.parseLong(taskId));
            }
            List<PointTaskConfigInfoDO> pointTaskConfigInfoDOS = pointTaskConfigInfoService.getByTaskIds(listTaskIds);
            return Result.buildSuccessResult(JsonUtil.copyList(pointTaskConfigInfoDOS, PointTaskConfigVO.class));
        } catch (Exception e) {
            log.error("queryPointTaskConfigsByTaskIds error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }
}
