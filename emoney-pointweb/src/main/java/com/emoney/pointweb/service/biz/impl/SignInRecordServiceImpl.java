package com.emoney.pointweb.service.biz.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.enums.BaseResultCodeEnum;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.utils.ToolUtils;
import com.emoeny.pointfacade.model.dto.SignInRecordCreateDTO;
import com.emoney.pointweb.repository.SignInRecordRepository;
import com.emoney.pointweb.repository.dao.entity.*;
import com.emoney.pointweb.service.biz.SignInRecordService;
import com.emoney.pointweb.service.biz.kafka.KafkaProducerService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import static cn.hutool.core.date.DateUtil.date;
import static com.emoeny.pointcommon.result.Result.buildErrorResult;

@Service
@Slf4j
public class SignInRecordServiceImpl implements SignInRecordService {

    @Autowired
    private SignInRecordRepository signInRecordRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private RedisService redisCache1;

    @Value("${signinrecord.topic}")
    private String signinrecordTopic;

    @Override
    public SignInRecordDO getById(Long uid, Long id) {
        return signInRecordRepository.getById(uid, id);
    }

    @Override
    public Result<Object> createSignInRecord(SignInRecordCreateDTO signInRecordCreateDTO) {
        //是否发送消息
        boolean canSendMessage = false;
        SignInRecordDO signInRecordDO = new SignInRecordDO();
        List<SignInRecordDO> signInRecordDOS = signInRecordRepository.getByUid(signInRecordCreateDTO.getUid(), DateUtil.parseDate(DateUtil.year(date()) + "-01-01"));
        if (signInRecordDOS != null && signInRecordDOS.size() > 0) {
            if (signInRecordDOS.stream().filter(h -> h.getUid().equals(signInRecordCreateDTO.getUid()) && DateUtil.formatDate(h.getSignInTime()).equals(DateUtil.formatDate(date()))).count() > 0) {
                return buildErrorResult(BaseResultCodeEnum.LOGIC_ERROR.getCode(), "不允许重复签到");
            } else {
                signInRecordDO = setSignInRecord(signInRecordCreateDTO);
                signInRecordDOS.add(signInRecordDO);
                canSendMessage = true;
            }
        } else {
            signInRecordDO = setSignInRecord(signInRecordCreateDTO);
            signInRecordDOS.add(signInRecordDO);
            canSendMessage = true;
        }
        if (canSendMessage) {
            redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_SignInRecord_GETBYUID, signInRecordCreateDTO.getUid()), signInRecordDOS, ToolUtils.GetExpireTime(60));
            //发消息到kafka
            kafkaProducerService.sendMessageSync(signinrecordTopic, JSONObject.toJSONString(signInRecordDO));
            return Result.buildSuccessResult();
        }
        return buildErrorResult(BaseResultCodeEnum.LOGIC_ERROR.getCode(), "已经签到或者其它异常");
    }

    @Override
    public List<SignInRecordDO> getByUid(Long uid, Date firstDay) {
        return signInRecordRepository.getByUid(uid, firstDay);
    }

    private SignInRecordDO setSignInRecord(SignInRecordCreateDTO signInRecordCreateDTO) {
        SignInRecordDO signInRecordDO = new SignInRecordDO();
        signInRecordDO.setId(IdUtil.getSnowflake(1, 1).nextId());
        signInRecordDO.setUid(signInRecordCreateDTO.getUid());
        signInRecordDO.setPlatform(signInRecordCreateDTO.getPlatform());
        signInRecordDO.setSignInTime(new Date());
        signInRecordDO.setCreateTime(new Date());
        return signInRecordDO;
    }
}
