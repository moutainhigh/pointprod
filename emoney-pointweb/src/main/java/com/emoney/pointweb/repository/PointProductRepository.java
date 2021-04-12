package com.emoney.pointweb.repository;

import com.emoney.pointweb.repository.dao.entity.PointProductDO;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/19 16:26
 */
public interface PointProductRepository {
    List<PointProductDO> getAllEffectiveProducts(Date curDate);

    PointProductDO getById(int id);
}
