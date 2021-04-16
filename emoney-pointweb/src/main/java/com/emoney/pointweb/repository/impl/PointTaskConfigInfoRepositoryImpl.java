package com.emoney.pointweb.repository.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.utils.ToolUtils;
import com.emoney.pointweb.repository.PointTaskConfigInfoRepository;
import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;
import com.emoney.pointweb.repository.dao.mapper.PointTaskConfigInfoMapper;
import com.emoney.pointweb.service.biz.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.date.DateUtil.date;

@Repository
public class PointTaskConfigInfoRepositoryImpl implements PointTaskConfigInfoRepository {

    @Autowired
    private PointTaskConfigInfoMapper pointTaskConfigInfoMapper;

    @Autowired
    private RedisService redisCache1;

    @Override
    public List<PointTaskConfigInfoDO> getByTaskIdAndSubId(Long taskId, String subId) {
        List<PointTaskConfigInfoDO> pointTaskConfigInfoDOs = redisCache1.getList(MessageFormat.format(RedisConstants.REDISKEY_PointTaskConfigInfo_GETBYTASKID, taskId, subId), PointTaskConfigInfoDO.class);
        if (pointTaskConfigInfoDOs == null) {
            pointTaskConfigInfoDOs = pointTaskConfigInfoMapper.getByTaskIdAndSubId(taskId, subId);
            if (pointTaskConfigInfoDOs != null && pointTaskConfigInfoDOs.size() > 0) {
                redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointTaskConfigInfo_GETBYTASKID, taskId, subId), pointTaskConfigInfoDOs, (long) 60 * 60 * 8);
            }
        }
        return pointTaskConfigInfoDOs;
    }

    @Override
    public List<PointTaskConfigInfoDO> getAllEffectiveTasks(Date curDate) {
        List<PointTaskConfigInfoDO> pointTaskConfigInfoDOS = redisCache1.getList(RedisConstants.REDISKEY_PointTaskConfigInfo_GETALLEFFECTIVETASKS, PointTaskConfigInfoDO.class);
        if (pointTaskConfigInfoDOS == null) {
            pointTaskConfigInfoDOS = pointTaskConfigInfoMapper.getAllEffectiveTasks(curDate);
            if (pointTaskConfigInfoDOS != null&&pointTaskConfigInfoDOS.size()>0) {
                redisCache1.set(RedisConstants.REDISKEY_PointTaskConfigInfo_GETALLEFFECTIVETASKS, pointTaskConfigInfoDOS, ToolUtils.GetExpireTime(60));
            }
        }
        return pointTaskConfigInfoDOS;
    }

    @Override
    public List<PointTaskConfigInfoDO> getTasksByTaskType(int taskType) {
        List<PointTaskConfigInfoDO> pointTaskConfigInfoDOS = redisCache1.getList(MessageFormat.format(RedisConstants.REDISKEY_PointTaskConfigInfo_GETTASKSBYTASKTYPE, taskType), PointTaskConfigInfoDO.class);
        if (pointTaskConfigInfoDOS == null) {
            pointTaskConfigInfoDOS = pointTaskConfigInfoMapper.getTasksByTaskType(taskType);
            if (pointTaskConfigInfoDOS != null&&pointTaskConfigInfoDOS.size()>0) {
                redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointTaskConfigInfo_GETTASKSBYTASKTYPE, taskType), pointTaskConfigInfoDOS, ToolUtils.GetExpireTime(60));
            }
        }
        return pointTaskConfigInfoDOS;
    }
}
