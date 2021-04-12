package com.emoney.pointweb.service.biz;

import com.emoney.pointweb.repository.dao.entity.PointQuotationDO;

import java.util.List;

public interface PointQuotationService {
    List<PointQuotationDO> getAll();
    int insert(PointQuotationDO pointQuotationDO);
    int update(PointQuotationDO pointQuotationDO);
}
