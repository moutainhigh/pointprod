package com.emoney.pointweb.repository.impl;

import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.utils.ToolUtils;
import com.emoney.pointweb.repository.PointMessageRepository;
import com.emoney.pointweb.repository.dao.entity.PointMessageDO;
import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;
import com.emoney.pointweb.repository.dao.mapper.PointMessageMapper;
import com.emoney.pointweb.service.biz.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/4/12 14:28
 */
@Repository
public class PointMessageRepositoryImpl implements PointMessageRepository {

    @Autowired
    private PointMessageMapper pointMessageMapper;

    @Autowired
    private RedisService redisCache1;


    @Override
    public Integer insert(PointMessageDO pointMessageDO) {
       return pointMessageMapper.insert(pointMessageDO);
    }

    @Override
    public List<PointMessageDO> getByUid(Long uid) {
        List<PointMessageDO> pointMessageDOS = redisCache1.getList(MessageFormat.format(RedisConstants.REDISKEY_PointMessage_GETBYUID, uid), PointMessageDO.class);
        if (pointMessageDOS == null) {
            pointMessageDOS = pointMessageMapper.getByUid(uid);
            if (pointMessageDOS != null&&pointMessageDOS.size()>0) {
                redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointMessage_GETBYUID, uid), pointMessageDOS, ToolUtils.GetExpireTime(60));
            }
        }
        return pointMessageDOS;
    }

    @Override
    public Integer getByUidAndExt(Long uid, String msgExt) {
        return pointMessageMapper.getByUidAndExt(uid,msgExt);
    }
}
