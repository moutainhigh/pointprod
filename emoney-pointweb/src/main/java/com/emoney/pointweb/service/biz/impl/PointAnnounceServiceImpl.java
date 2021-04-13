package com.emoney.pointweb.service.biz.impl;

import com.emoney.pointweb.repository.dao.entity.PointAnnounceDO;
import com.emoney.pointweb.repository.dao.mapper.PointAnnounceMapper;
import com.emoney.pointweb.service.biz.PointAnnounceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lipengcheng
 * @date 2021-04-13
 */
@Service
@Slf4j
public class PointAnnounceServiceImpl implements PointAnnounceService {

    @Autowired
    private PointAnnounceMapper pointAnnounceMapper;

    @Override
    public Integer insert(PointAnnounceDO pointAnnounceDO) {
        return pointAnnounceMapper.insert(pointAnnounceDO);
    }

    @Override
    public Integer update(PointAnnounceDO pointAnnounceDO) {
        return pointAnnounceMapper.update(pointAnnounceDO);
    }

    @Override
    public List<PointAnnounceDO> getAll() {
        return pointAnnounceMapper.getAll();
    }
}
