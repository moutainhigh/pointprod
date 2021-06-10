package com.emoney.pointweb.facade.impl.pointrecord;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.enums.BaseResultCodeEnum;
import com.emoeny.pointcommon.enums.PointRecordStatusEnum;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.utils.CollectionBeanUtils;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointcommon.utils.ToolUtils;
import com.emoeny.pointfacade.facade.pointrecord.PointRecordFacade;
import com.emoeny.pointfacade.model.dto.PointRecordCreateDTO;
import com.emoeny.pointfacade.model.dto.PointRecordQueryByTaskIdsDTO;
import com.emoeny.pointfacade.model.dto.PointRecordRecevieDTO;
import com.emoeny.pointfacade.model.vo.PointRecordSummaryVO;
import com.emoeny.pointfacade.model.vo.PointRecordVO;
import com.emoney.pointweb.repository.PointRecordRepository;
import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.repository.dao.entity.PointRecordSummaryDO;
import com.emoney.pointweb.service.biz.PointRecordService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import com.emoney.pointweb.service.biz.redis.RedissonDistributionLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.hutool.core.date.DateUtil.date;
import static com.emoeny.pointcommon.result.Result.buildErrorResult;

@RestController
@Validated
@Slf4j
public class PointRecordFacadeImpl implements PointRecordFacade {


    @Autowired
    private PointRecordRepository pointRecordRepository;

    @Autowired
    private PointRecordService pointRecordService;

    @Autowired
    private RedisService redisCache1;

    /**
     * 分布式锁
     */
    @Resource
    private RedissonDistributionLock redissonDistributionLock;

    @Override
    public Result<Object> createPointRecord(@RequestBody @Valid PointRecordCreateDTO pointRecordCreateDTO) {
        String lockKey = MessageFormat.format(RedisConstants.REDISKEY_PointRecord_CREATE_LOCKKEY, pointRecordCreateDTO.getUid(), pointRecordCreateDTO.getTaskId());
        try {
            if (redissonDistributionLock.tryLock(lockKey, TimeUnit.SECONDS, 10, 10)) {
                return pointRecordService.createPointRecord(pointRecordCreateDTO);
            }
            return Result.buildErrorResult(BaseResultCodeEnum.REPETITIVE_OPERATION.code(), BaseResultCodeEnum.REPETITIVE_OPERATION.getMsg());
        } catch (Exception e) {
            log.error("createPointRecord error:", e);
            return buildErrorResult(e.getMessage());
        } finally {
            redissonDistributionLock.unlock(lockKey);
        }
    }

    @Override
    public Result<List<PointRecordVO>> queryPointRecords(@NotNull(message = "用户id不能为空") Long uid) {
        try {
            List<PointRecordVO> pointRecordVOS = new ArrayList<>();
            List<PointRecordDO> pointRecordDOS = pointRecordRepository.getByUid(uid);
            if (pointRecordDOS != null) {
                pointRecordVOS = CollectionBeanUtils.copyListProperties(pointRecordDOS, PointRecordVO::new);
            }
            return Result.buildSuccessResult(pointRecordVOS);
        } catch (Exception e) {
            log.error("queryPointRecords error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<Object> receviePointRecord(@RequestBody @Valid PointRecordRecevieDTO pointRecordRecevieDTO) {
        try {
            List<PointRecordDO> pointRecordDOS = pointRecordRepository.getByUid(pointRecordRecevieDTO.getUid());
            if (pointRecordDOS == null) {
                return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "积分记录不存在");
            } else {
                Optional<PointRecordDO> pointRecordDO = pointRecordDOS.stream().filter(h -> h.getId().equals(pointRecordRecevieDTO.getId())).findFirst();
                if (pointRecordDO.isPresent()) {
                    if (pointRecordDO.get().getPointStatus().equals(Integer.valueOf(PointRecordStatusEnum.UNCLAIMED.getCode()))) {
                        pointRecordDO.get().setPointStatus(Integer.valueOf(PointRecordStatusEnum.FINISHED.getCode()));
                        pointRecordDO.get().setUpdateBy("system");
                        pointRecordDO.get().setUpdateTime(new Date());
                        int ret = pointRecordRepository.update(pointRecordDO.get());
                        if (ret > 0) {
                            redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETBYUID, pointRecordRecevieDTO.getUid()), pointRecordDOS, ToolUtils.GetExpireTime(60));
                            //去掉积分统计
                            redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETSUMMARYBYUID, pointRecordRecevieDTO.getUid()));
                            redisCache1.removePattern(MessageFormat.format("pointprod:pointrecord_getsummarybyuidandcreatetime_{0}_*", pointRecordRecevieDTO.getUid()));
                            //去掉待领取任务记录
                            redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_SETPOINTRECORDID, pointRecordDO.get().getUid(), pointRecordDO.get().getId()));
                            redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETUNCLAIMRECORDSBYUID, pointRecordDO.get().getUid()));
                            return Result.buildSuccessResult();
                        }
                        return buildErrorResult("领取失败");
                    } else {
                        return buildErrorResult(BaseResultCodeEnum.ILLEGAL_STATE.getCode(), "只有待领取任务才可以领取");
                    }
                } else {
                    return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "积分记录不存在");
                }
            }
        } catch (Exception e) {
            log.error("receviePointRecord error:", e);
            return buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<List<PointRecordSummaryVO>> queryPointRecordSummary(@NotNull(message = "用户id不能为空") Long uid) {
        try {
            return Result.buildSuccessResult(JsonUtil.copyList(pointRecordService.getPointRecordSummaryByUid(uid), PointRecordSummaryVO.class));
        } catch (Exception e) {
            log.error("queryPointRecordSummary error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<List<PointRecordSummaryVO>> queryPointRecordSummaryByCreateTime(@NotNull(message = "用户id不能为空") Long uid, @NotNull(message = "开始时间不能为空") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dtStart, @NotNull(message = "结束时间不能为空") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dtEnd) {
        try {
            return Result.buildSuccessResult(JsonUtil.copyList(pointRecordService.getPointRecordSummaryByUidAndCreateTime(uid, dtStart, dtEnd), PointRecordSummaryVO.class));
        } catch (Exception e) {
            log.error("queryPointRecordSummaryByCreateTime error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<List<PointRecordVO>> queryPointRecords(@NotNull(message = "用户id不能为空") Long uid, @NotNull(message = "查询类型不能为空") Integer queryType, @NotNull(message = "pageIndex不能为空") Integer pageIndex, @NotNull(message = "pageSize不能为空") Integer pageSize) {
        try {
            Date dtStart = null;
            Date dtEnd = null;
            //积分明细全部
            if (queryType == 0) {
                queryType = -1;
            }
            //积分收入
            else if (queryType == 1) {
                queryType = Integer.valueOf(PointRecordStatusEnum.FINISHED.getCode());
            }
            //兑换积分
            else if (queryType == 2) {
                queryType = Integer.valueOf(PointRecordStatusEnum.CONVERTED.getCode());
            }
            //过期积分
            else if (queryType == 3) {
                queryType = Integer.valueOf(PointRecordStatusEnum.FINISHED.getCode());
                dtStart = DateUtil.parseDate((DateUtil.year(DateUtil.date()) - 1) + "-01-01 00:00:00");
                dtEnd = DateUtil.beginOfDay(DateUtil.offsetMonth(dtStart, 3));
            }
            //冻结积分
            else if (queryType == 4) {
                queryType = -2;
            }
            return Result.buildSuccessResult(CollectionBeanUtils.copyListProperties(pointRecordService.getByPager(uid, queryType, dtStart, dtEnd, pageIndex, pageSize), PointRecordVO::new));
        } catch (Exception e) {
            log.error("queryPointRecords error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<Float> queryUnclaimedRecordPoints(@NotNull(message = "用户id不能为空") Long uid) {
        try {
            Float points = 0f;
            List<PointRecordDO> pointRecordDOS = pointRecordService.getUnClaimRecordsByUid(uid);
            if (pointRecordDOS != null) {
                for (PointRecordDO p : pointRecordDOS
                ) {
                    points += p.getTaskPoint();
                }
            }
            return Result.buildSuccessResult(points);
        } catch (Exception e) {
            log.error("queryUnclaimedRecordPoints error:", e);
            return buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<List<PointRecordVO>> queryUnclaimedRecords(@NotNull(message = "用户id不能为空") Long uid) {
        try {
            return Result.buildSuccessResult(JsonUtil.copyList(pointRecordService.getUnClaimRecordsByUid(uid), PointRecordVO.class));
        } catch (Exception e) {
            log.error("queryUnclaimedRecords error:", e);
            return buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<List<PointRecordVO>> queryPointRecordsByTaskids(@Valid PointRecordQueryByTaskIdsDTO pointRecordQueryByTaskIdsDTO) {
        try {
            return Result.buildSuccessResult(JsonUtil.copyList(pointRecordService.getPointRecordByTaskIds(pointRecordQueryByTaskIdsDTO.getUid(), pointRecordQueryByTaskIdsDTO.getTaskIds()), PointRecordVO.class));
        } catch (Exception e) {
            log.error("queryPointRecordsByTaskids error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<Object> clearRedis(){
        redisCache1.removePattern("pointprod:pointrecord_getbyuid_*");
        redisCache1.removePattern("pointprod:pointrecord_getsummarybyuid_*");
        redisCache1.removePattern("pointprod:pointrecord_getsummarybyuidandcreatetime_*");
        redisCache1.removePattern("pointprod:pointrecord_getunclaimrecordsbyuid_*");
        redisCache1.removePattern("pointprod:signinrecord_getbyuid_*");
        return Result.buildSuccessResult();
    }
}
