package com.emoney.pointweb.repository;

import com.emoney.pointweb.repository.dao.entity.PointLimitDO;

public interface PointLimitRepository {
    PointLimitDO getByType(int pointLimittype, int pointListto);
}
