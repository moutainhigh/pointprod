package com.emoney.pointweb.repository.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.utils.ToolUtils;
import com.emoney.pointweb.repository.SignInRecordRepository;
import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.repository.dao.entity.SignInRecordDO;
import com.emoney.pointweb.repository.dao.mapper.SignInRecordMapper;
import com.emoney.pointweb.service.biz.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cn.hutool.core.date.DateUtil.date;

@Repository
public class SignInRecordRepositoryImpl implements SignInRecordRepository {

    @Autowired
    private SignInRecordMapper signInRecordMapper;

    @Autowired
    private RedisService redisCache1;

    @Override
    public SignInRecordDO getById(Long uid, Long id) {
        return signInRecordMapper.getById(uid, id);
    }

    @Override
    public Integer insert(SignInRecordDO signInRecordDO) {
        SignInRecordDO signInRecordDODb = getById(signInRecordDO.getUid(), signInRecordDO.getId());
        if (signInRecordDODb == null) {
            return signInRecordMapper.insert(signInRecordDO);
        }
        return 0;
    }

    @Override
    public List<SignInRecordDO> getByUid(Long uid, Date firstDay) {
        List<SignInRecordDO> signInRecordDOS = redisCache1.getList(MessageFormat.format(RedisConstants.REDISKEY_SignInRecord_GETBYUID, uid), SignInRecordDO.class);
        if (signInRecordDOS == null) {
            signInRecordDOS = signInRecordMapper.getByUid(uid, firstDay);
            if (signInRecordDOS != null && signInRecordDOS.size() > 0) {
                redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_SignInRecord_GETBYUID, uid), signInRecordDOS, ToolUtils.GetExpireTime(60));
            }
        }
        return signInRecordDOS;
    }
}
