package com.emoney.pointweb.repository.dao.mapper;

import com.emoney.pointweb.repository.dao.entity.PointFeedBackDO;
import com.emoney.pointweb.repository.dao.entity.PointMessageDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface PointFeedBackMapper {
    Integer insert(PointFeedBackDO pointFeedBackDO);
    Integer update(PointFeedBackDO pointFeedBackDO);
    List<PointFeedBackDO> getAll();
    PointFeedBackDO getById(Integer id);
    List<PointFeedBackDO> queryAllByRemarkAndStatus(Integer status,Integer isReply);
    /**
     * 根据uid获取所有反馈记录
     * @param uid
     * @return
     */
    List<PointFeedBackDO> getByUid(String accountName, Date endDate);
}
