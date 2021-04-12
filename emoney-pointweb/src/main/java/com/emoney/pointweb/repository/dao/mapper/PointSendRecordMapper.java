package com.emoney.pointweb.repository.dao.mapper;

import com.emoney.pointweb.repository.dao.entity.PointSendRecordDO;
import com.emoney.pointweb.repository.dao.entity.vo.PointSendRecordVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PointSendRecordMapper {
    List<PointSendRecordVO> getDataStatistics(int pointtype);
    Integer insert(PointSendRecordDO pointSendRecordDO);
    List<PointSendRecordVO> getPointSendRecordByBatchId(String batchId,String status);
}
