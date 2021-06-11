package com.emoney.pointweb.repository.impl;

import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.utils.ToolUtils;
import com.emoney.pointweb.repository.PointUserMessageMappingRepository;
import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.repository.dao.entity.UserMessageMappingDO;
import com.emoney.pointweb.repository.dao.mapper.PointUserMessageMappingMapper;
import com.emoney.pointweb.service.biz.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author lipengcheng
 * @date 2021-06-11
 */
@Repository
public class PointUserMessageMappingRepositoryImpl implements PointUserMessageMappingRepository {

    @Autowired
    private PointUserMessageMappingMapper pointUserMessageMappingMapper;

    @Autowired
    private RedisService redisCache1;

    @Override
    public Integer insert(UserMessageMappingDO userMessageMappingDO) {
        Integer result = pointUserMessageMappingMapper.insert(userMessageMappingDO);
        if (result > 0) {
            redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_UserMessageMapping_GETBYUID, userMessageMappingDO.getUid()));
        }
        return result;
    }

    @Override
    public List<UserMessageMappingDO> getByuid(String uid) {
        List<UserMessageMappingDO> userMessageMappingDOS = redisCache1.getList(MessageFormat.format(RedisConstants.REDISKEY_UserMessageMapping_GETBYUID, uid), UserMessageMappingDO.class);
        if (userMessageMappingDOS == null) {
            userMessageMappingDOS = pointUserMessageMappingMapper.getByuid(uid);
            if (userMessageMappingDOS != null) {
                redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_UserMessageMapping_GETBYUID, uid), userMessageMappingDOS, ToolUtils.GetExpireTime(60));
            }
        }
        return userMessageMappingDOS;
    }
}
