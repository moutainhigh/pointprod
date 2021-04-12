package com.emoney.pointweb.repository.dao.mapper;

import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface PointTaskConfigInfoMapper {
    List<PointTaskConfigInfoDO> pageList(int start, int length, int task_type);

    List<PointTaskConfigInfoDO> getPointTaskConfigInfoByOrderAndType(int task_type, int task_order);

    List<PointTaskConfigInfoDO> getPointTaskConfigInfoByIsDirectional(int is_directional);

    int insert(PointTaskConfigInfoDO pointTaskConfigInfoDO);

    int update(PointTaskConfigInfoDO pointTaskConfigInfoDO);

    List<PointTaskConfigInfoDO> getByTaskIdAndSubId(Long taskId,String subId);

    List<PointTaskConfigInfoDO> getAllEffectiveTasks(Date curDate);

    List<PointTaskConfigInfoDO> getTasksByTaskType(int taskType);
}