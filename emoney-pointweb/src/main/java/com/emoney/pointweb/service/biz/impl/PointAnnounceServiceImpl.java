package com.emoney.pointweb.service.biz.impl;

import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoney.pointweb.repository.PointAnnounceRepository;
import com.emoney.pointweb.repository.dao.entity.PointAnnounceDO;
import com.emoney.pointweb.repository.dao.mapper.PointAnnounceMapper;
import com.emoney.pointweb.service.biz.PointAnnounceService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author lipengcheng
 * @date 2021-04-13
 */
@Service
@Slf4j
public class PointAnnounceServiceImpl implements PointAnnounceService {

    @Autowired
    private PointAnnounceMapper pointAnnounceMapper;

    @Autowired
    private PointAnnounceRepository pointAnnounceRepository;

    @Autowired
    private RedisService redisCache1;

    @Override
    public Integer insert(PointAnnounceDO pointAnnounceDO) {
        //清除缓存
        redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointAnnounce_GETBYTYPE, pointAnnounceDO.getMsgType()));
        return pointAnnounceMapper.insert(pointAnnounceDO);
    }

    @Override
    public Integer update(PointAnnounceDO pointAnnounceDO) {
        //清除缓存
        redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointAnnounce_GETBYTYPE, pointAnnounceDO.getMsgType()));
        return pointAnnounceMapper.update(pointAnnounceDO);
    }

    @Override
    public List<PointAnnounceDO> getAll() {
        return pointAnnounceMapper.getAll();
    }

    @Override
    public List<PointAnnounceDO> getPointAnnouncesByType(int msgType) {
        return pointAnnounceRepository.getPointAnnouncesByType(msgType);
    }
}
