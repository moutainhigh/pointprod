package com.emoney.pointweb.service.biz;

import com.emoney.pointweb.repository.dao.entity.PointMessageDO;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/4/12 15:01
 */
public interface PointMessageService {
    /**
     * 创建消息
     * @param pointMessageDO
     * @return
     */
    Integer insert(PointMessageDO pointMessageDO);

    /**
     * 根据uid获取所有消息记录
     * @param uid
     * @return
     */
    List<PointMessageDO> getByUid(Long uid, Date endDate);
}
