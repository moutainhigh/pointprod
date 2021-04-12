package com.emoney.pointweb.service.biz.impl;

import com.emoney.pointweb.repository.PointMessageRepository;
import com.emoney.pointweb.repository.dao.entity.PointMessageDO;
import com.emoney.pointweb.service.biz.PointMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/4/12 15:01
 */
@Service
@Slf4j
public class PointMessageServiceImpl implements PointMessageService {

    @Autowired
    private PointMessageRepository pointMessageRepository;

    @Override
    public Integer insert(PointMessageDO pointMessageDO) {
       return  pointMessageRepository.insert(pointMessageDO);
    }

    @Override
    public List<PointMessageDO> getByUid(Long uid) {
        return pointMessageRepository.getByUid(uid);
    }
}
