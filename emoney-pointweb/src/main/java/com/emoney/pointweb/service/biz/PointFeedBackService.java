package com.emoney.pointweb.service.biz;

import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.dto.PointFeedBackCreateDTO;
import com.emoney.pointweb.repository.dao.entity.PointFeedBackDO;

import java.util.List;

public interface PointFeedBackService {
    Integer insert(PointFeedBackDO pointFeedBackDO);
    Integer update(PointFeedBackDO pointFeedBackDO);
    List<PointFeedBackDO> getAll();
    PointFeedBackDO getById(Integer id);
    Result<Object> createFeedBack(PointFeedBackCreateDTO pointFeedBackCreateDTO);
    List<PointFeedBackDO> queryAllByRemarkAndStatus(Integer status,Integer isReply);
}
