package com.emoney.pointweb.service.biz;

import com.emoney.pointweb.repository.dao.entity.PointProductDO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PointProductService {
    int insertPointProduct(PointProductDO pointProductDO);

    int updatePointProduct(PointProductDO pointProductDO);

    List<PointProductDO> getPointProductListByProductType(int productType);

    Map<String, Object> checkActivityCode(String acCode);

    List<PointProductDO> getAllEffectiveProducts(Date curDate,String productVersion);

    PointProductDO getById(int id);
}
