package com.emoney.pointweb.repository;

import com.emoeny.pointfacade.model.vo.PointQuestionVO;
import com.emoney.pointweb.repository.dao.entity.PointQuestionDO;

import java.util.List;

public interface PointQuestionRepository {
    List<PointQuestionDO> queryAll();
    PointQuestionDO queryAllById(Integer id);
}
