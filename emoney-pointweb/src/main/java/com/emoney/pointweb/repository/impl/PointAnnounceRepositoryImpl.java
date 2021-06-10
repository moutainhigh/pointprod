package com.emoney.pointweb.repository.impl;

import cn.hutool.core.date.DateUtil;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.utils.ToolUtils;
import com.emoney.pointweb.repository.PointAnnounceRepository;
import com.emoney.pointweb.repository.dao.entity.PointAnnounceDO;
import com.emoney.pointweb.repository.dao.entity.PointLimitDO;
import com.emoney.pointweb.repository.dao.entity.PointMessageDO;
import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;
import com.emoney.pointweb.repository.dao.mapper.PointAnnounceMapper;
import com.emoney.pointweb.service.biz.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/4/14 11:34
 */
@Repository
public class PointAnnounceRepositoryImpl implements PointAnnounceRepository {

    @Autowired
    private PointAnnounceMapper pointAnnounceMapper;

    @Autowired
    private RedisService redisCache1;

    @Override
    public List<PointAnnounceDO> getPointAnnouncesByType(List<Integer> msgTypes, Date endDate) {
        List<PointAnnounceDO> pointAnnounceDOS = redisCache1.getList(RedisConstants.REDISKEY_PointAnnounce_GETBYTYPE, PointAnnounceDO.class);
        if (pointAnnounceDOS == null) {
            pointAnnounceDOS = pointAnnounceMapper.getPointAnnouncesByType(new Date(), endDate, msgTypes);
            if (pointAnnounceDOS != null && pointAnnounceDOS.size() > 0) {
                redisCache1.set(RedisConstants.REDISKEY_PointAnnounce_GETBYTYPE, pointAnnounceDOS, ToolUtils.GetExpireTime(60));
            }
        }
        return pointAnnounceDOS;
    }

    @Override
    public Integer insert(PointAnnounceDO pointAnnounceDO) {

        //清除缓存
        redisCache1.remove(RedisConstants.REDISKEY_PointAnnounce_GETBYTYPE);
        return pointAnnounceMapper.insert(pointAnnounceDO);
    }

    @Override
    public Integer update(PointAnnounceDO pointAnnounceDO) {
        //清除缓存
        redisCache1.remove(RedisConstants.REDISKEY_PointAnnounce_GETBYTYPE);
        return pointAnnounceMapper.update(pointAnnounceDO);
    }
}
