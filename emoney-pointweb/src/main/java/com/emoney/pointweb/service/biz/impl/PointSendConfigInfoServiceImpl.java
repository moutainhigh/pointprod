package com.emoney.pointweb.service.biz.impl;

import com.emoney.pointweb.repository.dao.entity.PointSendConfigInfoDO;
import com.emoney.pointweb.repository.dao.mapper.PointSendConfigInfoMapper;
import com.emoney.pointweb.service.biz.PointSendConfigInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lipengcheng
 * @date 2021-05-13
 */
@Service
public class PointSendConfigInfoServiceImpl implements PointSendConfigInfoService {

    @Autowired
    private PointSendConfigInfoMapper pointSendConfigInfoMapper;

    @Override
    public List<PointSendConfigInfoDO> queryAll() {
        return pointSendConfigInfoMapper.queryAll();
    }

    @Override
    public Integer insert(PointSendConfigInfoDO pointSendConfigInfoDO) {
        return pointSendConfigInfoMapper.insert(pointSendConfigInfoDO);
    }

    @Override
    public Integer update(PointSendConfigInfoDO pointSendConfigInfoDO) {
        return pointSendConfigInfoMapper.update(pointSendConfigInfoDO);
    }
}
