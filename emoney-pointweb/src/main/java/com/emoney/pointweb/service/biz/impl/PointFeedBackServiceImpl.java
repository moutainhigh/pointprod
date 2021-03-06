package com.emoney.pointweb.service.biz.impl;

import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.dto.PointFeedBackCreateDTO;
import com.emoney.pointweb.repository.dao.entity.PointFeedBackDO;
import com.emoney.pointweb.repository.dao.mapper.PointFeedBackMapper;
import com.emoney.pointweb.service.biz.PointFeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.java2d.pipe.SolidTextRenderer;

import java.util.Date;
import java.util.List;

import static com.emoeny.pointcommon.result.Result.buildErrorResult;

/**
 * @author lipengcheng
 * @date 2021-04-15
 */
@Service
public class PointFeedBackServiceImpl implements PointFeedBackService {

    @Autowired
    private PointFeedBackMapper pointFeedBackMapper;

    @Override
    public Integer insert(PointFeedBackDO pointFeedBackDO) {
        return pointFeedBackMapper.insert(pointFeedBackDO);
    }

    @Override
    public Integer update(PointFeedBackDO pointFeedBackDO) {
        return pointFeedBackMapper.update(pointFeedBackDO);
    }

    @Override
    public List<PointFeedBackDO> getAll() {
        return pointFeedBackMapper.getAll();
    }

    public List<PointFeedBackDO> queryAllByRemarkAndStatus(Integer status, Integer isReply, Integer isAdopt, String content) {
        return pointFeedBackMapper.queryAllByRemarkAndStatus(status, isReply, isAdopt, content);
    }

    @Override
    public List<PointFeedBackDO> getByUid(String accountName, Date endDate) {
        return pointFeedBackMapper.getByUid(accountName, endDate);
    }

    @Override
    public PointFeedBackDO getById(Integer id) {
        return pointFeedBackMapper.getById(id);
    }

    @Override
    public Result<Object> createFeedBack(PointFeedBackCreateDTO pointFeedBackCreateDTO) {
        if (pointFeedBackCreateDTO != null) {
            PointFeedBackDO pointFeedBackDO = new PointFeedBackDO();
            pointFeedBackDO.setFeedType(pointFeedBackCreateDTO.getFeedType());
            pointFeedBackDO.setPid(pointFeedBackCreateDTO.getPid());
            pointFeedBackDO.setAccount(pointFeedBackCreateDTO.getAccount());
            pointFeedBackDO.setMobileX(pointFeedBackCreateDTO.getMobileX());
            pointFeedBackDO.setEmail(pointFeedBackCreateDTO.getEmail());
            pointFeedBackDO.setSuggest(pointFeedBackCreateDTO.getSuggest());
            pointFeedBackDO.setImgUrl(pointFeedBackCreateDTO.getImgUrl());
            pointFeedBackDO.setStatus(pointFeedBackCreateDTO.getStatus());
            pointFeedBackDO.setCreateTime(new Date());
            pointFeedBackDO.setUpdateTime(new Date());
            pointFeedBackDO.setCreateBy(pointFeedBackCreateDTO.getUid().toString());
            pointFeedBackDO.setUpdateBy(pointFeedBackCreateDTO.getUid().toString());
            if (pointFeedBackMapper.insert(pointFeedBackDO) > 0) {
                return Result.buildSuccessResult("????????????");
            }
        }
        return buildErrorResult("????????????");
    }
}
