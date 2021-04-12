package com.emoney.pointweb.repository.dao.mapper;

import com.emoney.pointweb.repository.dao.entity.PointLimitDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PointLimitMapper {
    Integer insert(PointLimitDO pointLimitDO);

    List<PointLimitDO> pageList();

    PointLimitDO getByType(int pointLimittype, int pointListto);
}
