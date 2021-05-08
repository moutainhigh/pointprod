package com.emoney.pointweb.service.biz.impl;

import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.dto.PointFeedBackCreateDTO;
import com.emoney.pointweb.repository.dao.entity.PointFeedBackDO;
import com.emoney.pointweb.repository.dao.mapper.PointFeedBackMapper;
import com.emoney.pointweb.service.biz.PointFeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public PointFeedBackDO getById(Integer id){
        return pointFeedBackMapper.getById(id);
    }

    @Override
    public Result<Object> createFeedBack(PointFeedBackCreateDTO pointFeedBackCreateDTO){
        if(pointFeedBackCreateDTO!=null){
            PointFeedBackDO pointFeedBackDO=new PointFeedBackDO();
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
            if(pointFeedBackMapper.insert(pointFeedBackDO)>0){
                return Result.buildSuccessResult("提交成功");
            }
        }
        return buildErrorResult("提交失败");
    }
}
