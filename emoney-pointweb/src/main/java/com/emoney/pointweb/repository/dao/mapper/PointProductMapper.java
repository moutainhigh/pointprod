package com.emoney.pointweb.repository.dao.mapper;

import com.emoney.pointweb.repository.dao.entity.PointProductDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.redis.connection.ReactiveListCommands;

import java.util.Date;
import java.util.List;

@Mapper
public interface PointProductMapper {
    int insertPointProduct(PointProductDO pointProductDO);

    int updatePointProduct(PointProductDO pointProductDO);

    List<PointProductDO> getPointProductListByProductType(int productType);

    List<PointProductDO> getAllEffectiveProducts(Date curDate);

    PointProductDO getById(int id);
}
