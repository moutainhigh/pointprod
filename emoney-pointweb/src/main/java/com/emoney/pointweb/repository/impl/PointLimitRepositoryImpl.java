package com.emoney.pointweb.repository.impl;

import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoney.pointweb.repository.PointLimitRepository;
import com.emoney.pointweb.repository.dao.entity.PointLimitDO;
import com.emoney.pointweb.repository.dao.mapper.PointLimitMapper;
import com.emoney.pointweb.service.biz.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.MessageFormat;

@Repository
public class PointLimitRepositoryImpl implements PointLimitRepository {

    @Autowired
    private PointLimitMapper pointLimitMapper;

    @Autowired
    private RedisService redisCache1;

    @Override
    public PointLimitDO getByType(int pointLimittype, int pointListto) {
        PointLimitDO pointLimitDO = redisCache1.get(MessageFormat.format(RedisConstants.REDISKEY_PointLimitType_GETBYTYPE, pointLimittype, pointListto), PointLimitDO.class);
        if (pointLimitDO == null) {
            pointLimitDO = pointLimitMapper.getByType(pointLimittype, pointListto);
            if (pointLimitDO != null) {
                redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointLimitType_GETBYTYPE, pointLimittype, pointListto), pointLimitDO, (long) 8 * 60 * 60);
            }
        }
        return pointLimitDO;
    }
}
