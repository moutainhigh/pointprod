package com.emoney.pointweb.service.biz.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.enums.BaseResultCodeEnum;
import com.emoeny.pointcommon.enums.PointLimitToEnum;
import com.emoeny.pointcommon.enums.PointLimitTypeEnum;
import com.emoeny.pointcommon.enums.PointRecordStatusEnum;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.utils.ToolUtils;
import com.emoeny.pointfacade.model.dto.PointRecordCreateDTO;
import com.emoeny.pointfacade.model.vo.PointRecordCreateVO;
import com.emoney.pointweb.repository.PointLimitRepository;
import com.emoney.pointweb.repository.PointRecordESRepository;
import com.emoney.pointweb.repository.PointRecordRepository;
import com.emoney.pointweb.repository.PointTaskConfigInfoRepository;
import com.emoney.pointweb.repository.dao.entity.PointLimitDO;
import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.repository.dao.entity.PointRecordSummaryDO;
import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;
import com.emoney.pointweb.service.biz.PointRecordService;
import com.emoney.pointweb.service.biz.kafka.KafkaProducerService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.date.DateUtil.date;
import static com.emoeny.pointcommon.result.Result.buildErrorResult;

@Service
public class PointRecordServiceImpl implements PointRecordService {
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private PointTaskConfigInfoRepository pointTaskConfigInfoRepository;

    @Autowired
    private PointRecordRepository pointRecordRepository;

    @Autowired
    private PointRecordESRepository pointRecordESRepository;

    @Autowired
    private PointLimitRepository pointLimitRepository;

    @Autowired
    private RedisService redisCache1;

    @Override
    public Result<Object> createPointRecord(PointRecordCreateDTO pointRecordCreateDTO) {
        //是否发送消息
        boolean canSendMessage = false;
        PointRecordDO pointRecordDO = new PointRecordDO();
        //获取任务信息
        PointTaskConfigInfoDO pointTaskConfigInfoDO = null;
        List<PointTaskConfigInfoDO> pointTaskConfigInfoDOS = pointTaskConfigInfoRepository.getByTaskIdAndSubId(pointRecordCreateDTO.getTaskId(), pointRecordCreateDTO.getSubId());
        if (pointTaskConfigInfoDOS != null) {
            if (!StringUtils.isEmpty(pointRecordCreateDTO.getSubId())) {
                pointTaskConfigInfoDO = pointTaskConfigInfoDOS.stream().filter(h -> h.getSubId().equals(pointRecordCreateDTO.getSubId())).findAny().orElse(null);
            } else {
                if (pointTaskConfigInfoDOS.size() > 1) {
                    return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "后台配置错误");
                }
                pointTaskConfigInfoDO = pointTaskConfigInfoDOS.stream().findFirst().orElse(null);
            }
        }
        if (pointTaskConfigInfoDO != null && pointTaskConfigInfoDO.getTaskStartTime().before(new Date()) && pointTaskConfigInfoDO.getTaskEndTime().after(new Date())) {
            //判断sub_id
            if (!StringUtils.isEmpty(pointTaskConfigInfoDO.getSubId()) && !pointTaskConfigInfoDO.getSubId().equals(pointRecordCreateDTO.getSubId())) {
                return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "当前场景不能发送积分");
            }
            //获取积分记录
            List<PointRecordDO> pointRecordDOS = pointRecordRepository.getByUid(pointRecordCreateDTO.getUid());
            if (pointRecordDOS != null && pointRecordDOS.size() > 0) {
                long dailyTaskCount = pointRecordDOS.stream().filter(h -> h.getTaskId().equals(pointRecordCreateDTO.getTaskId()) && DateUtil.formatDate(h.getCreateTime()).equals(DateUtil.formatDate(date()))).count() + 1;
                long nDailyTaskCount = pointRecordDOS.stream().filter(h -> h.getTaskId().equals(pointRecordCreateDTO.getTaskId())).count() + 1;
                if (
                        (pointTaskConfigInfoDO.getIsDailyTask() && dailyTaskCount <= pointTaskConfigInfoDO.getDailyJoinTimes())
                                || (!pointTaskConfigInfoDO.getIsDailyTask() && nDailyTaskCount <= pointTaskConfigInfoDO.getDailyJoinTimes())
                ) {
                    if (pointRecordDOS.stream().filter(h -> h.getTaskId().equals(pointRecordCreateDTO.getTaskId()) && h.getPointStatus().equals(Integer.valueOf(PointRecordStatusEnum.UNCLAIMED.getCode()))).count() > 0) {
                        return buildErrorResult(BaseResultCodeEnum.LOGIC_ERROR.getCode(), "当前存在待领取的任务,请先去任务中心领取");
                    } else {
                        pointRecordDO = setPointRecordDO(pointRecordCreateDTO, pointTaskConfigInfoDO);
                        pointRecordDOS.add(pointRecordDO);
                        canSendMessage = true;
                    }
                } else {
                    return buildErrorResult(BaseResultCodeEnum.LOGIC_ERROR.getCode(), "参与次数超限");
                }
            } else {
                pointRecordDO = setPointRecordDO(pointRecordCreateDTO, pointTaskConfigInfoDO);
                pointRecordDOS.add(pointRecordDO);
                canSendMessage = true;
            }
            if (canSendMessage) {
                //判断限额，限额从后台读取，有缓存
                float curTotal = 0f;
                PointLimitDO pointLimitDO = pointLimitRepository.getByType(Integer.valueOf(PointLimitTypeEnum.SEND.code()), Integer.valueOf(PointLimitToEnum.PERSONAL.code()));
                if (pointLimitDO != null) {
                    for (PointRecordDO p : pointRecordDOS
                    ) {
                        if (DateUtil.formatDate(p.getCreateTime()).equals(DateUtil.formatDate(date()))) {
                            curTotal += p.getTaskPoint();
                        }
                    }
                    if (curTotal > pointLimitDO.getPointLimitvalue()) {
                        return buildErrorResult(BaseResultCodeEnum.LOGIC_ERROR.getCode(), "积分超过限额");
                    } else {
                        redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETBYUID, pointRecordCreateDTO.getUid()), pointRecordDOS, ToolUtils.GetExpireTime(60));
                        //发消息到kafka
                        kafkaProducerService.sendMessageSync("pointrecordadd", JSONObject.toJSONString(pointRecordDO));
                        //将积分记录返回
                        PointRecordCreateVO pointRecordCreateVO = new PointRecordCreateVO();
                        pointRecordCreateVO.setId(pointRecordDO.getId());
                        pointRecordCreateVO.setTaskId(pointRecordDO.getTaskId());
                        pointRecordCreateVO.setTaskName(pointRecordDO.getTaskName());
                        pointRecordCreateVO.setTaskPoint(pointRecordDO.getTaskPoint());
                        pointRecordCreateVO.setPointStatus(pointRecordDO.getPointStatus());
                        pointRecordCreateVO.setSubId(pointRecordDO.getSubId());
                        return Result.buildSuccessResult(pointRecordCreateVO);
                    }
                } else {
                    return buildErrorResult(BaseResultCodeEnum.LOGIC_ERROR.getCode(), "请先配置限额");
                }

            } else {
                return buildErrorResult(BaseResultCodeEnum.LOGIC_ERROR.getCode(), "任务已完成或参与次数超限");
            }
        } else {
            return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "任务无效或已过期");
        }
    }

    @Override
    public List<PointRecordDO> getByPager(Long uid, Integer pointStatus, Date startDate, Date endDate, int pageIndex, int pageSize) {
        return pointRecordRepository.getByPager(uid, pointStatus, startDate, endDate, pageIndex, pageSize);
    }

//    @Override
//    public List<PointRecordDO> getPointRecordDOs(long uid,  List<Integer> pointStatus,int pageSize,int pageIndex) {
//        Pageable pageable = PageRequest.of(pageSize,pageIndex);
//        return pointRecordESRepository.findByUidAndPointStatusInOrderByCreateTimeDesc(uid,pointStatus,pageable).getContent();
//    }
//
//    @Override
//    public List<PointRecordDO> getPointRecordDOs(long uid,  List<Integer> pointStatus,Date from, Date to,int pageSize,int pageIndex) {
//        Pageable pageable = PageRequest.of(pageSize,pageIndex);
//        return pointRecordESRepository.findByUidAndCreateTimeBetweenOrderByCreateTimeDesc(uid,pointStatus,from,to,pageable).getContent();
//    }

    @Override
    public Long calPointRecordByTaskId(long taskId, String subId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PointRecordDO> pointRecordDOPage = pointRecordESRepository.findByTaskIdAndSubId(taskId, subId, pageable);
        return pointRecordDOPage.getTotalElements();
    }

    @Override
    public List<PointRecordDO> getUnClaimRecordsByUid(Long uid) {
        return pointRecordRepository.getUnClaimRecordsByUid(uid);
    }

    @Override
    public List<PointRecordSummaryDO> getPointRecordSummaryByUid(Long uid) {
        return pointRecordRepository.getPointRecordSummaryByUid(uid);
    }

    @Override
    public List<PointRecordSummaryDO> getPointRecordSummaryByUidAndCreateTime(Long uid, Date dtStart, Date dtEnd) {
        return pointRecordRepository.getPointRecordSummaryByUidAndCreateTime(uid, dtStart, dtEnd);
    }

    @Override
    public List<PointRecordDO> getPointRecordByTaskIds(Long uid, List<Long> taskIds) {
        return pointRecordRepository.getPointRecordByTaskIds(uid, taskIds);
    }

    private PointRecordDO setPointRecordDO(PointRecordCreateDTO pointRecordCreateDTO, PointTaskConfigInfoDO pointTaskConfigInfoDO) {
        PointRecordDO pointRecordDO = new PointRecordDO();
        pointRecordDO.setId(IdUtil.getSnowflake(1, 1).nextId());
        pointRecordDO.setUid(pointRecordCreateDTO.getUid());
        pointRecordDO.setTaskId(pointTaskConfigInfoDO.getTaskId());
        pointRecordDO.setTaskName(pointTaskConfigInfoDO.getTaskName());
        pointRecordDO.setTaskPoint(pointTaskConfigInfoDO.getTaskPoints());
        pointRecordDO.setIsDailytask(pointTaskConfigInfoDO.getIsDailyTask());
        pointRecordDO.setSubId(pointTaskConfigInfoDO.getSubId());
        pointRecordDO.setCreateTime(new Date());
        pointRecordDO.setCreateBy("system");
        pointRecordDO.setPid(pointRecordCreateDTO.getPid());
        pointRecordDO.setEmNo(pointRecordCreateDTO.getEmNo());
        pointRecordDO.setPlatform(pointRecordCreateDTO.getPlatform());
        pointRecordDO.setRemark(pointRecordCreateDTO.getRemark());

        //定向积分
        if (pointTaskConfigInfoDO.getIsDirectional()) {
            pointRecordDO.setPointStatus(Integer.valueOf(PointRecordStatusEnum.FINISHED.getCode()));
        } else {
            //成长任务 只有成长任务需要领取
            if (pointTaskConfigInfoDO.getTaskType() == 2) {
                pointRecordDO.setPointStatus(Integer.valueOf(PointRecordStatusEnum.UNCLAIMED.getCode()));
            } else {
                pointRecordDO.setPointStatus(Integer.valueOf(PointRecordStatusEnum.FINISHED.getCode()));
            }
        }
        //积分有效期
        if (pointTaskConfigInfoDO.getIsDailyTask()) {
            pointRecordDO.setExpirationTime(DateUtil.parseDate("2099-12-31"));
        } else {
            pointRecordDO.setExpirationTime(DateUtil.parseDateTime((DateUtil.year(DateUtil.date()) + 1) + "-03-31 23:59:59"));
        }
        pointRecordDO.setLeftPoint(pointTaskConfigInfoDO.getTaskPoints());
        pointRecordDO.setIsValid(true);
        return pointRecordDO;
    }
}
