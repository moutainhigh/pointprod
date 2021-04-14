package com.emoney.pointweb.repository;

import com.emoney.pointweb.repository.dao.entity.PointAnnounceDO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/4/14 11:33
 */
public interface PointAnnounceRepository {

    List<PointAnnounceDO> getPointAnnouncesByType(List<Integer> msgTypes);
}
