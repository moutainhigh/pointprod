package com.emoney.pointweb.service.biz;

import com.emoney.pointweb.repository.dao.entity.vo.PointSendRecordVO;

import java.util.List;
import java.util.Map;

public interface PointSendRecordService {
    Map<String, Object> DataStatistics(int pointtype);

    Map<String, Object> getPointSendRecordByBatchId(String batchId, String status);

    Map<String, Object> PointUserSend(List<Map<String, Object>> userList, long taskId, String remark);

    void sendPointRecord(long taskId,String account);

    List<PointSendRecordVO> queryPointSendRecordBybatchIdAndstatus(String batchId,String status);
}
