package com.emoney.pointweb.facade.impl.signinrecord;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.enums.BaseResultCodeEnum;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.utils.CollectionBeanUtils;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointfacade.facade.signinrecord.SignInRecordFacade;
import com.emoeny.pointfacade.model.dto.SignInRecordCreateDTO;
import com.emoeny.pointfacade.model.vo.PointQuotationVO;
import com.emoeny.pointfacade.model.vo.SignInRecordVO;
import com.emoney.pointweb.repository.dao.entity.SignInRecordDO;
import com.emoney.pointweb.service.biz.PointQuotationService;
import com.emoney.pointweb.service.biz.SignInRecordService;
import com.emoney.pointweb.service.biz.redis.RedissonDistributionLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.date.DateUtil.date;

@RestController
@Validated
@Slf4j
public class SignInRecordFacadeImpl implements SignInRecordFacade {

    @Autowired
    private SignInRecordService signInRecordService;

    @Autowired
    private PointQuotationService pointQuotationService;

    /**
     * 分布式锁
     */
    @Resource
    private RedissonDistributionLock redissonDistributionLock;

    @Override
    public Result<Object> createSignInRecord(@RequestBody @Valid SignInRecordCreateDTO signInRecordCreateDTO) {
        String lockKey = MessageFormat.format(RedisConstants.REDISKEY_SignInRecord_CREATE_LOCKKEY, signInRecordCreateDTO.getUid());
        try {
            if (redissonDistributionLock.tryLock(lockKey, TimeUnit.SECONDS, 10, 10)) {
                return signInRecordService.createSignInRecord(signInRecordCreateDTO);
            }
            return Result.buildErrorResult(BaseResultCodeEnum.REPETITIVE_OPERATION.code(), BaseResultCodeEnum.REPETITIVE_OPERATION.getMsg());
        } catch (Exception e) {
            log.error("createSignInRecord error:", e);
            return Result.buildErrorResult(e.getMessage());
        }finally {
            redissonDistributionLock.unlock(lockKey);
        }
    }

    @Override
    public Result<List<SignInRecordVO>> querySignInRecord(@NotNull(message = "用户id不能为空") Long uid) {
        try {
            List<SignInRecordDO> signInRecordDOS = signInRecordService.getByUid(uid, DateUtil.parseDate("2021-01-01"));
            if (signInRecordDOS != null && signInRecordDOS.size() > 0) {
                signInRecordDOS.sort(Comparator.comparing(SignInRecordDO::getSignInTime).reversed());
                return Result.buildSuccessResult(CollectionBeanUtils.copyListProperties(signInRecordDOS, SignInRecordVO::new));
            }
            return Result.buildSuccessResult();

        } catch (Exception e) {
            log.error("querySignInRecord error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<String> querySignInRecordTips() {
        try {
            List<PointQuotationVO> pointQuotationVOS = JsonUtil.copyList(pointQuotationService.getAll(), PointQuotationVO.class);
            if (pointQuotationVOS != null && pointQuotationVOS.size() > 0) {
                PointQuotationVO pointQuotationVO = pointQuotationVOS.get(RandomUtil.randomInt(0, pointQuotationVOS.size() - 1));
                return Result.buildSuccessResult(pointQuotationVO.getContent());
            }
            return Result.buildErrorResult("");
        } catch (Exception e) {
            log.error("querySignInRecordTips error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }
}
