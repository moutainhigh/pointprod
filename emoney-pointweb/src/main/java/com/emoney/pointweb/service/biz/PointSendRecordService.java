package com.emoney.pointweb.service.biz;

import java.util.List;
import java.util.Map;

public interface PointSendRecordService {
    Map<String, Object> DataStatistics(int pointtype);

    Map<String, Object> getPointSendRecordByBatchId(String batchId, String status);

    Map<String, Object> PointUserSend(List<Map<String, Object>> userList, long taskId, String remark);
}
