package com.emoney.pointweb.repository.dao.mapper;

import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.repository.dao.entity.PointRecordSummaryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface PointRecordMapper {
    /**
     * 根据uid和主键id查询积分记录
     * @param uid
     * @param id
     * @return
     */
    PointRecordDO getById(Long uid, Long id);

    /**
     *保存积分记录
     * @param pointRecordDO
     * @return
     */
    Integer insert(PointRecordDO pointRecordDO);

    /**
     * 查询积分明细记录
     * @param uid
     * @param pointStatus
     * @param startDate
     * @param endDate
     * @return
     */
    List<PointRecordDO> getByPager(Long uid,Integer pointStatus,Date startDate, Date endDate);

    /**
     * 根据uid查询非每日任务积分记录
     * @param uid
     * @return
     */
    List<PointRecordDO> getByUid1(Long uid);

    /**
     * 根据uid查询每日任务当日积分记录
     * @param uid
     * @param startDate
     * @param endDate
     * @return
     */
    List<PointRecordDO> getByUid2(Long uid, Date startDate, Date endDate);

    /**
     * 根据uid查询带领取任务记录
     * @param uid
     * @return
     */
    List<PointRecordDO> getUnClaimRecordsByUid(Long uid);

    /**
     * 修改积分记录
     * @param pointRecordDO
     * @return
     */
    Integer update(PointRecordDO pointRecordDO);

    /**
     * 根据uid查询积分记录按照状态分组统计
     * @param uid
     * @return
     */
    List<PointRecordSummaryDO> getPointRecordSummaryByUid(Long uid);

    /**
     * 根据uid,时间范围 查询积分记录按照状态分组统计
     * @param uid
     * @param dtStart
     * @param dtEnd
     * @return
     */
    List<PointRecordSummaryDO> getPointRecordSummaryByUidAndCreateTime(Long uid, Date dtStart, Date dtEnd);

    /**
     * 根据uid，taskIds批量查询任务记录
     * @param uid
     * @param taskIds
     * @return
     */
    List<PointRecordDO> getPointRecordByTaskIds(Long uid, @Param("list") List<Long> taskIds);

    /**
     * 根据uid查询当前获得的未使用积分
     * @param uid
     * @param endDate
     * @return
     */
    List<PointRecordDO> getByUidAndCreateTime(Long uid, Date endDate);
}
