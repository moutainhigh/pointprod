package com.emoney.pointweb.service.biz.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.enums.BaseResultCodeEnum;
import com.emoeny.pointcommon.enums.PointLimitToEnum;
import com.emoeny.pointcommon.enums.PointLimitTypeEnum;
import com.emoeny.pointcommon.enums.PointRecordStatusEnum;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.result.ResultInfo;
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
import com.emoney.pointweb.repository.dao.entity.vo.UserInfoVO;
import com.emoney.pointweb.service.biz.MailerService;
import com.emoney.pointweb.service.biz.PointRecordService;
import com.emoney.pointweb.service.biz.UserInfoService;
import com.emoney.pointweb.service.biz.UserLoginService;
import com.emoney.pointweb.service.biz.kafka.KafkaProducerService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static cn.hutool.core.date.DateUtil.date;
import static com.emoeny.pointcommon.result.Result.buildErrorResult;

@Service
@Slf4j
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

    @Autowired
    private MailerService mailerService;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private UserInfoService userInfoService;

    @Value("${mail.toMail.addr}")
    private String toMailAddress;

    @Value("${pointrecord.topic}")
    private String pointrecordTopic;

    @Override
    public Result<Object> createPointRecord(PointRecordCreateDTO pointRecordCreateDTO) {

        log.info("????????????,??????:" + JSON.toJSONString(pointRecordCreateDTO));
        //??????????????????
        boolean canSendMessage = false;
        PointRecordDO pointRecordDO = new PointRecordDO();
        //??????????????????
        PointTaskConfigInfoDO pointTaskConfigInfoDO = null;
        List<PointTaskConfigInfoDO> pointTaskConfigInfoDOS = pointTaskConfigInfoRepository.getByTaskIdAndSubId(pointRecordCreateDTO.getTaskId(), pointRecordCreateDTO.getSubId(),new Date());
        if (pointTaskConfigInfoDOS != null) {
            if (!StringUtils.isEmpty(pointRecordCreateDTO.getSubId())) {
                pointTaskConfigInfoDO = pointTaskConfigInfoDOS.stream().filter(h -> h.getSubId().equals(pointRecordCreateDTO.getSubId())).findFirst().orElse(null);
            } else {
                if (pointTaskConfigInfoDOS.size() > 1) {
                    return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "??????????????????");
                }
                pointTaskConfigInfoDO = pointTaskConfigInfoDOS.stream().findFirst().orElse(null);
            }
        }
        if (pointTaskConfigInfoDO != null && pointTaskConfigInfoDO.getTaskStartTime().before(new Date()) && pointTaskConfigInfoDO.getTaskEndTime().after(new Date())) {
            //??????sub_id
            if (!StringUtils.isEmpty(pointTaskConfigInfoDO.getSubId()) && !pointTaskConfigInfoDO.getSubId().equals(pointRecordCreateDTO.getSubId())) {
                return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "??????????????????????????????");
            }
            //??????????????????
            List<PointRecordDO> pointRecordDOS = pointRecordRepository.getByUid(pointRecordCreateDTO.getUid());
            List<PointRecordDO> tmpPointRecords = new ArrayList<>();
            if (pointRecordDOS != null && pointRecordDOS.size() > 0) {
                if (pointRecordCreateDTO.getSubId() != null) {
                    tmpPointRecords = pointRecordDOS.stream().filter(h -> h.getTaskId().equals(pointRecordCreateDTO.getTaskId()) && h.getSubId().equals(pointRecordCreateDTO.getSubId())).collect(Collectors.toList());
                } else {
                    tmpPointRecords = pointRecordDOS.stream().filter(h -> h.getTaskId().equals(pointRecordCreateDTO.getTaskId())).collect(Collectors.toList());
                }
                long dailyTaskCount = tmpPointRecords.stream().filter(h -> DateUtil.formatDate(h.getCreateTime()).equals(DateUtil.formatDate(date()))).count() + 1;
                long nDailyTaskCount = tmpPointRecords.stream().count() + 1;
                if (
                        (pointTaskConfigInfoDO.getIsDailyTask() && dailyTaskCount <= pointTaskConfigInfoDO.getDailyJoinTimes())
                                || (!pointTaskConfigInfoDO.getIsDailyTask() && nDailyTaskCount <= pointTaskConfigInfoDO.getDailyJoinTimes())
                ) {
                    if (tmpPointRecords.stream().filter(h -> h.getPointStatus().equals(Integer.valueOf(PointRecordStatusEnum.UNCLAIMED.getCode()))).count() > 0) {
                        return buildErrorResult(BaseResultCodeEnum.LOGIC_ERROR.getCode(), "??????????????????????????????,???????????????????????????");
                    } else {
                        pointRecordDO = setPointRecordDO(pointRecordCreateDTO, pointTaskConfigInfoDO);
                        pointRecordDOS.add(pointRecordDO);
                        canSendMessage = true;
                    }
                } else {
                    return buildErrorResult(BaseResultCodeEnum.LOGIC_ERROR.getCode(), "?????????????????????????????????????????????????????????");
                }
            } else {
                pointRecordDO = setPointRecordDO(pointRecordCreateDTO, pointTaskConfigInfoDO);
                pointRecordDOS.add(pointRecordDO);
                canSendMessage = true;
            }
            if (canSendMessage) {
                //????????????????????????????????????????????????
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
                        String cTaskId = String.valueOf(pointTaskConfigInfoDO.getTaskId());
                        String cTaskName = pointTaskConfigInfoDO.getTaskName();
                        //????????????
                        CompletableFuture.runAsync(() -> {
                            try {
                                //????????????
                                String subject = "????????????????????????";
                                String userName = "";
                                List<UserInfoVO> userInfoVOS = userInfoService.getUserInfoByUid(pointRecordCreateDTO.getUid());
                                if (userInfoVOS != null) {
                                    UserInfoVO userInfoVO = userInfoVOS.stream().filter(h -> h.getAccountType() == 0).findFirst().orElse(null);
                                    if (userInfoVO != null) {
                                        userName = userInfoVO.getAccountName();
                                    }
                                }
                                String content = MessageFormat.format("???????????????????????????ID???{0},????????????{1},??????ID:{2},????????????:{3},????????????:{4}", pointRecordCreateDTO.getUid(), userName, cTaskId, cTaskName, new Date());
                                mailerService.sendSimpleTextMailActual(subject, content, toMailAddress.split(","), null, null, null);
                            } catch (Exception e) {
                                log.error("????????????????????????,sendSimpleTextMailActual error", e);
                            }
                        }, executor);
                        return buildErrorResult(BaseResultCodeEnum.LOGIC_ERROR.getCode(), "????????????????????????????????????????????????????????? ");
                    } else {
                        redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETBYUID, pointRecordCreateDTO.getUid()), pointRecordDOS, ToolUtils.GetExpireTime(60));

                        //????????????kafka
                        kafkaProducerService.sendMessageSync(pointrecordTopic, JSONObject.toJSONString(pointRecordDO));
                        //?????????????????????
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
                    return buildErrorResult(BaseResultCodeEnum.LOGIC_ERROR.getCode(), "??????????????????");
                }

            } else {
                return buildErrorResult(BaseResultCodeEnum.LOGIC_ERROR.getCode(), "????????????????????????????????????");
            }
        } else {
            return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "????????????????????????");
        }
    }

    @Override
    public List<PointRecordDO> getByPager(Long uid, Integer pointStatus, Date startDate, Date endDate, int pageIndex, int pageSize) {
        List<PointRecordDO> ret = pointRecordRepository.getByPager(uid, pointStatus, startDate, endDate, pageIndex, pageSize);
        //????????????
        if (startDate != null && startDate.equals(DateUtil.parseDate((DateUtil.year(DateUtil.date()) - 1) + "-01-01 00:00:00"))) {
            ret = ret.stream().filter(h -> !h.getLeftPoint().equals(0)).collect(Collectors.toList());
        }

        if (ret != null) {
            for (PointRecordDO pointRecord : ret
            ) {
                if (pointRecord.getTaskName().contains("??????") && !StringUtils.isEmpty(pointRecord.getRemark())) {
                    pointRecord.setTaskName("????????????" + pointRecord.getRemark() + "???");
                }
            }
        }


        return ret;
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

    @Override
    public Integer update(PointRecordDO pointRecordDO) {
        return pointRecordRepository.update(pointRecordDO);
    }

    @Override
    public List<PointRecordDO> getByUid(Long uid) {
        return pointRecordRepository.getByUid(uid);
    }

    private PointRecordDO setPointRecordDO(PointRecordCreateDTO pointRecordCreateDTO, PointTaskConfigInfoDO pointTaskConfigInfoDO) {
        PointRecordDO pointRecordDO = new PointRecordDO();
        pointRecordDO.setId(IdUtil.getSnowflake(1, 1).nextId());
        pointRecordDO.setUid(pointRecordCreateDTO.getUid());
        pointRecordDO.setTaskId(pointTaskConfigInfoDO.getTaskId());
        pointRecordDO.setTaskName(pointTaskConfigInfoDO.getTaskName());
        pointRecordDO.setTaskPoint(pointTaskConfigInfoDO.getTaskPoints());

        if (pointRecordCreateDTO.getManualPoint() != null) {
            pointRecordDO.setTaskPoint(pointRecordCreateDTO.getManualPoint());
        }

        if (!StringUtils.isEmpty(pointRecordCreateDTO.getTaskName())) {
            pointRecordDO.setTaskName(pointRecordCreateDTO.getTaskName());
        }
        pointRecordDO.setIsDailytask(pointTaskConfigInfoDO.getIsDailyTask());
        pointRecordDO.setSubId(pointTaskConfigInfoDO.getSubId());
        pointRecordDO.setCreateTime(new Date());
        pointRecordDO.setCreateBy("system");
        pointRecordDO.setPid(pointRecordCreateDTO.getPid());
        pointRecordDO.setEmNo(pointRecordCreateDTO.getEmNo());
        pointRecordDO.setPlatform(pointRecordCreateDTO.getPlatform());
        pointRecordDO.setRemark(pointRecordCreateDTO.getRemark());

        //????????????
        if (pointTaskConfigInfoDO.getIsDirectional()) {
            pointRecordDO.setIsDirectional(true);
            pointRecordDO.setPointStatus(Integer.valueOf(PointRecordStatusEnum.FINISHED.getCode()));
        } else {
            pointRecordDO.setIsDirectional(false);
            //???????????? ??????????????????????????????
            if (pointTaskConfigInfoDO.getTaskType() == 2) {
                pointRecordDO.setPointStatus(Integer.valueOf(PointRecordStatusEnum.UNCLAIMED.getCode()));
            } else {
                pointRecordDO.setPointStatus(Integer.valueOf(PointRecordStatusEnum.FINISHED.getCode()));
            }
        }
        //???????????????
        if (pointTaskConfigInfoDO.getIsDirectional()) {
            pointRecordDO.setExpirationTime(DateUtil.parseDateTime("2099-12-31 23:59:59"));
        } else {
            pointRecordDO.setExpirationTime(DateUtil.parseDateTime((DateUtil.year(DateUtil.date()) + 2) + "-03-31 23:59:59"));
        }
        pointRecordDO.setLeftPoint(pointRecordDO.getTaskPoint());
        pointRecordDO.setIsValid(true);
        pointRecordDO.setLockDays(pointRecordCreateDTO.getLockDays());
        return pointRecordDO;
    }
}
