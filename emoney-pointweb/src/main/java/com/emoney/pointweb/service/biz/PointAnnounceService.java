package com.emoney.pointweb.service.biz;

import com.emoney.pointweb.repository.dao.entity.PointAnnounceDO;

import java.util.Date;
import java.util.List;

public interface PointAnnounceService {
    Integer insert(PointAnnounceDO pointAnnounceDO);

    Integer update(PointAnnounceDO pointAnnounceDO);

    List<PointAnnounceDO> getAll();

    List<PointAnnounceDO> getPointAnnouncesByType(List<Integer> msgTypes, Date endDate);

    List<PointAnnounceDO> getPointAnnouncesByMapping(Date endDate, String uid);
}
