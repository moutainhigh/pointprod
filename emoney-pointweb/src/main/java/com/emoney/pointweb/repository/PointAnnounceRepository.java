package com.emoney.pointweb.repository;

import com.emoney.pointweb.repository.dao.entity.PointAnnounceDO;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/4/14 11:33
 */
public interface PointAnnounceRepository {

    List<PointAnnounceDO> getPointAnnouncesByType(List<Integer> msgTypes, Date endDate);

    Integer insert(PointAnnounceDO pointAnnounceDO);

    Integer update(PointAnnounceDO pointAnnounceDO);
}
