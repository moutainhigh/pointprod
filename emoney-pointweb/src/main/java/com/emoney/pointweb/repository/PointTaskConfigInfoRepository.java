package com.emoney.pointweb.repository;

import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;

import java.util.Date;
import java.util.List;

public interface PointTaskConfigInfoRepository {
    List<PointTaskConfigInfoDO> getByTaskIdAndSubId(Long taskId,String subId,Date curDate);

    List<PointTaskConfigInfoDO> getAllEffectiveTasks(Date curDate);

    List<PointTaskConfigInfoDO> getTasksByTaskType(int taskType);
}
