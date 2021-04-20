package com.emoney.pointweb.facade.impl.pointfeedback;

import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.enums.BaseResultCodeEnum;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.facade.pointfeedback.PointFeedBackFacade;
import com.emoeny.pointfacade.model.dto.PointFeedBackCreateDTO;
import com.emoney.pointweb.service.biz.PointFeedBackService;
import com.emoney.pointweb.service.biz.redis.RedissonDistributionLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.awt.*;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import static com.emoeny.pointcommon.result.Result.buildErrorResult;

/**
 * @author lipengcheng
 * @date 2021-04-19
 */
@RestController
@Validated
@Slf4j
public class PointFeedBackFacadeImpl implements PointFeedBackFacade {

    @Autowired
    private PointFeedBackService pointFeedBackService;

    /**
     * 分布式锁
     */
    @Resource
    private RedissonDistributionLock redissonDistributionLock;

    @Override
    public Result<Object> createFeedBack(PointFeedBackCreateDTO pointFeedBackCreateDTO) {
        String lockKey = MessageFormat.format(RedisConstants.REDISKEY_SignInRecord_CREATE_LOCKKEY, pointFeedBackCreateDTO.getUid());
        try {
            if (redissonDistributionLock.tryLock(lockKey, TimeUnit.SECONDS, 10, 10)) {
                return pointFeedBackService.createFeedBack(pointFeedBackCreateDTO);
            }
            return Result.buildErrorResult(BaseResultCodeEnum.REPETITIVE_OPERATION.code(), BaseResultCodeEnum.REPETITIVE_OPERATION.getMsg());
        } catch (Exception e) {
            log.error("createSignInRecord error:", e);
            return Result.buildErrorResult(e.getMessage());
        }finally {
            redissonDistributionLock.unlock(lockKey);
        }
    }
}
