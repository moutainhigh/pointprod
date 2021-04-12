package com.emoney.pointweb.repository.dao.mapper;

import com.emoney.pointweb.repository.dao.entity.PointQuotationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PointQuotationMapper {
    List<PointQuotationDO> getAll();
    int insert(PointQuotationDO pointQuotationDO);
    int update(PointQuotationDO pointQuotationDO);
}
