package com.emoney.pointweb.repository.dao.mapper;

import com.emoney.pointweb.repository.dao.entity.PointMessageDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/4/12 13:58
 */
@Mapper
public interface PointMessageMapper {
    /**
     * 创建消息
     * @param pointMessageDO
     * @return
     */
    Integer insert(PointMessageDO pointMessageDO);

    Integer update(PointMessageDO pointMessageDO);

    /**
     * 根据uid获取所有消息记录
     * @param uid
     * @return
     */
    List<PointMessageDO> getByUid(Long uid, Date endDate);

    /**
     *
     * @param uid
     * @param msgExt
     * @return
     */
    List<PointMessageDO> getByUidAndExt(Long uid,String msgExt);
}
