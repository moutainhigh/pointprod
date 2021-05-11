package com.emoney.pointweb.repository.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.utils.ToolUtils;
import com.emoney.pointweb.repository.PointRecordRepository;
import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.repository.dao.entity.PointRecordSummaryDO;
import com.emoney.pointweb.repository.dao.mapper.PointRecordMapper;
import com.emoney.pointweb.service.biz.redis.RedisService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.date.DateUtil.date;

@Repository
public class PointRecordRepositoryImpl implements PointRecordRepository {

    @Autowired
    private PointRecordMapper pointRecordMapper;

    @Autowired
    private RedisService redisCache1;

    @Override
    public PointRecordDO getById(Long uid, Long id) {
        return pointRecordMapper.getById(uid, id);
    }

    @Override
    public Integer insert(PointRecordDO pointRecordDO) {
        PointRecordDO pointRecordDODb = getById(pointRecordDO.getUid(), pointRecordDO.getId());
        if (pointRecordDODb == null) {
            return pointRecordMapper.insert(pointRecordDO);
        }
        return 0;
    }

    @Override
    public List<PointRecordDO> getByPager(Long uid, Integer pointStatus, Date startDate, Date endDate, int pageIndex, int pageSize) {
        PageHelper.startPage(pageIndex, pageSize);
        return pointRecordMapper.getByPager(uid, pointStatus, startDate, endDate);
    }

    @Override
    public List<PointRecordDO> getByUid(Long uid) {
        List<PointRecordDO> pointRecordDOS = redisCache1.getList(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETBYUID, uid), PointRecordDO.class);
        if (pointRecordDOS == null) {
            pointRecordDOS = new ArrayList<>();
            List<PointRecordDO> pointRecordDOS1 = pointRecordMapper.getByUid1(uid);
            List<PointRecordDO> pointRecordDOS2 = pointRecordMapper.getByUid2(uid, DateUtil.beginOfDay(date()), DateUtil.offsetSecond(DateUtil.endOfDay(date()), 1));
            if (pointRecordDOS1 != null && pointRecordDOS1.size() > 0) {
                pointRecordDOS.addAll(pointRecordDOS1);
            }
            if (pointRecordDOS2 != null && pointRecordDOS2.size() > 0) {
                pointRecordDOS.addAll(pointRecordDOS2);
            }
            if (pointRecordDOS != null && pointRecordDOS.size() > 0) {
                redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETBYUID, uid), pointRecordDOS, ToolUtils.GetExpireTime(60));
            }
        }
        return pointRecordDOS;
    }

    @Override
    public List<PointRecordDO> getUnClaimRecordsByUid(Long uid) {
        List<PointRecordDO> pointRecordDOS = redisCache1.getList(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETUNCLAIMRECORDSBYUID, uid), PointRecordDO.class);
        if (pointRecordDOS == null) {
            pointRecordDOS = pointRecordMapper.getUnClaimRecordsByUid(uid);
            if (pointRecordDOS != null && pointRecordDOS.size() > 0) {
                redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETUNCLAIMRECORDSBYUID, uid), pointRecordDOS, ToolUtils.GetExpireTime(60));
            }
        }
        return pointRecordDOS;
    }

    @Override
    public Integer update(PointRecordDO pointRecordDO) {
        return pointRecordMapper.update(pointRecordDO);
    }

    @Override
    public List<PointRecordSummaryDO> getPointRecordSummaryByUid(Long uid) {
        List<PointRecordSummaryDO> pointRecordSummaryDOS = redisCache1.getList(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETSUMMARYBYUID, uid), PointRecordSummaryDO.class);
        if (pointRecordSummaryDOS == null) {
            pointRecordSummaryDOS = pointRecordMapper.getPointRecordSummaryByUid(uid);
            if (pointRecordSummaryDOS != null && pointRecordSummaryDOS.size() > 0) {
                redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETSUMMARYBYUID, uid), pointRecordSummaryDOS, ToolUtils.GetExpireTime(60));
            }
        }
        return pointRecordSummaryDOS;
    }

    @Override
    public List<PointRecordSummaryDO> getPointRecordSummaryByUidAndCreateTime(Long uid, Date dtStart, Date dtEnd) {
//        List<PointRecordSummaryDO> pointRecordSummaryDOS = redisCache1.getList(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETSUMMARYBYUIDANDCREATETIME, uid, DateUtil.format(dtStart, "yyyyMMdd"), DateUtil.format(dtEnd, "yyyyMMdd")), PointRecordSummaryDO.class);
//        if (pointRecordSummaryDOS == null) {
//            pointRecordSummaryDOS = pointRecordMapper.getPointRecordSummaryByUidAndCreateTime(uid, dtStart, dtEnd);
//            if (pointRecordSummaryDOS != null && pointRecordSummaryDOS.size() > 0) {
//                redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETSUMMARYBYUIDANDCREATETIME, uid, DateUtil.format(dtStart, "yyyyMMdd"), DateUtil.format(dtEnd, "yyyyMMdd")), pointRecordSummaryDOS, ToolUtils.GetExpireTime(60));
//            }
//        }
        return pointRecordMapper.getPointRecordSummaryByUidAndCreateTime(uid, dtStart, dtEnd);
    }

    @Override
    public List<PointRecordDO> getPointRecordByTaskIds(Long uid, List<Long> taskIds) {
        return pointRecordMapper.getPointRecordByTaskIds(uid, taskIds);
    }

    @Override
    public List<PointRecordDO> getByUidAndCreateTime(Long uid, Date endDate) {
        return pointRecordMapper.getByUidAndCreateTime(uid,endDate);
    }

    @Override
    public List<PointRecordDO> getLockPointRecordsByUid() {
        return pointRecordMapper.getLockPointRecordsByUid();
    }
}
