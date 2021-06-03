package com.emoney.pointweb.service.biz;

import com.emoeny.pointcommon.result.userperiod.UserPeriodResult;
import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;
import com.emoney.pointweb.repository.dao.entity.dto.CheckUserGroupDTO;
import com.emoney.pointweb.repository.dao.entity.dto.CheckUserGroupData;
import com.emoney.pointweb.repository.dao.entity.vo.CheckUserGroupVO;
import com.emoney.pointweb.repository.dao.entity.vo.PointTaskConfigInfoVO;
import com.emoney.pointweb.repository.dao.entity.vo.UserGroupVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PointTaskConfigInfoService {
    Map<String, Object> pageList(Integer start, Integer length, Integer task_type, Integer task_status, String ver, String plat);

    int insert(PointTaskConfigInfoDO pointTaskConfigInfoDO);

    int update(PointTaskConfigInfoDO pointTaskConfigInfoDO);

    List<PointTaskConfigInfoDO> getPointTaskConfigInfoByOrderAndType(int tasktype, int taskorder);

    List<PointTaskConfigInfoDO> getPointTaskConfigInfoByIsDirectional();

    List<PointTaskConfigInfoDO> getAllEffectiveTasks(Date curDate, Long uid, String productVersion, String publishPlatFormType);

    List<PointTaskConfigInfoDO> getByTaskIdAndSubId(Long taskId, String subId);

    List<PointTaskConfigInfoDO> getTasksByTaskType(int taskType, Long uid, String productVersion, String publishPlatFormType);

    List<PointTaskConfigInfoDO> getByTaskIds(List<Long> listTaskIds);
}
