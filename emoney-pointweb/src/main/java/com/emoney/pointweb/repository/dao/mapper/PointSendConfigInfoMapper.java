package com.emoney.pointweb.repository.dao.mapper;

import com.emoney.pointweb.repository.dao.entity.PointSendConfigInfoDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PointSendConfigInfoMapper{
    List<PointSendConfigInfoDO> queryAll();
    Integer insert(PointSendConfigInfoDO pointSendConfigInfoDO);
    Integer update(PointSendConfigInfoDO pointSendConfigInfoDO);
}




