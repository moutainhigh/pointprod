package com.emoney.pointweb.service.biz;


import com.emoney.pointweb.repository.dao.entity.PointSendConfigInfoDO;

import java.util.List;

public interface PointSendConfigInfoService {
    List<PointSendConfigInfoDO> queryAll();
    Integer insert(PointSendConfigInfoDO pointSendConfigInfoDO);
    Integer update(PointSendConfigInfoDO pointSendConfigInfoDO);
}
