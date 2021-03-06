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

import javax.xml.crypto.Data;
import java.text.MessageFormat;
import java.util.Date;
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
        int ret=pointMessageMapper.insert(pointMessageDO);
        if(ret>0){
            redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointMessage_GETBYUID, pointMessageDO.getUid()));
        }
       return ret;
    }

    @Override
    public Integer update(PointMessageDO pointMessageDO) {
        int ret= pointMessageMapper.update(pointMessageDO);
        if(ret>0){
            redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointMessage_GETBYUID, pointMessageDO.getUid()));
        }
        return ret;
    }


    @Override
    public List<PointMessageDO> getByUid(Long uid, Date endDate) {
        List<PointMessageDO> pointMessageDOS = redisCache1.getList(MessageFormat.format(RedisConstants.REDISKEY_PointMessage_GETBYUID, uid), PointMessageDO.class);
        if (pointMessageDOS == null) {
            pointMessageDOS = pointMessageMapper.getByUid(uid,endDate);
            if (pointMessageDOS != null&&pointMessageDOS.size()>0) {
                redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointMessage_GETBYUID, uid), pointMessageDOS, ToolUtils.GetExpireTime(60));
            }
        }
        return pointMessageDOS;
    }

    @Override
    public List<PointMessageDO> getByUidAndExt(Long uid, String msgExt) {
        return pointMessageMapper.getByUidAndExt(uid,msgExt);
    }
}
