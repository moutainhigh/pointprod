package com.emoney.pointweb.service.biz.impl;

import com.alibaba.fastjson.JSON;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.result.ApiResult;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.utils.OkHttpUtil;
import com.emoney.pointweb.repository.PointProductRepository;
import com.emoney.pointweb.repository.dao.entity.PointProductDO;
import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;
import com.emoney.pointweb.repository.dao.entity.dto.CheckUserGroupDTO;
import com.emoney.pointweb.repository.dao.entity.dto.CheckUserGroupData;
import com.emoney.pointweb.repository.dao.entity.vo.ActivityInfoVO;
import com.emoney.pointweb.repository.dao.entity.vo.CheckUserGroupVO;
import com.emoney.pointweb.repository.dao.mapper.PointProductMapper;
import com.emoney.pointweb.service.biz.PointProductService;
import com.emoney.pointweb.service.biz.PointTaskConfigInfoService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PointProductServiceImpl implements PointProductService {
    @Autowired
    private PointProductMapper pointProductMapper;

    @Autowired
    private PointProductRepository pointProductRepository;

    @Autowired
    private PointTaskConfigInfoService pointTaskConfigInfoService;

    @Autowired
    private RedisService redisCache1;

    @Value("${getactivityurl}")
    private String getactivityurl;

    @Override
    public int updatePointProduct(PointProductDO pointProductDO) {
        //商品编辑或者新增将缓存删除
        redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointProduct_GETBYID, pointProductDO.getId()));
        redisCache1.remove(RedisConstants.REDISKEY_PointProduct_GETALLEFFECTIVEPRODUCTS);
        return pointProductMapper.updatePointProduct(pointProductDO);
    }

    @Override
    public int insertPointProduct(PointProductDO pointProductDO) {
        //商品编辑或者新增将缓存删除
        redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointProduct_GETBYID, pointProductDO.getId()));
        redisCache1.remove(RedisConstants.REDISKEY_PointProduct_GETALLEFFECTIVEPRODUCTS);
        return pointProductMapper.insertPointProduct(pointProductDO);
    }

    @Override
    public List<PointProductDO> getPointProductListByProductType(int productType) {
        return pointProductMapper.getPointProductListByProductType(productType);
    }

    @Override
    public Map<String, Object> checkActivityCode(String acCode) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, String> query = new HashMap<>();
            query.put("gate_appid", "10109");
            query.put("jsonStr", "{'ActivityCode':'" + acCode + "'}");
            String res = OkHttpUtil.get(getactivityurl, query);
            ApiResult<String> apiResult = JSON.parseObject(res, ApiResult.class);
            Result<List<ActivityInfoVO>> data = JSON.parseObject(apiResult.Message, Result.class);

            result.put("code", 0);
            result.put("data", data.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<PointProductDO> getAllEffectiveProducts(Date curDate, String productVersion, Long uid) {
        List<PointProductDO> retPointProducts = new ArrayList<>();
        List<PointProductDO> pointProductDOS = pointProductRepository.getAllEffectiveProducts(new Date());
        if (pointProductDOS != null) {
            pointProductDOS = pointProductDOS.stream().filter(h -> h.getProductVersion().contains(productVersion)).collect(Collectors.toList());
        }

        //接入用户画像
        if (pointProductDOS != null) {
            CheckUserGroupDTO checkUserGroupDTO = new CheckUserGroupDTO();
            List<CheckUserGroupData> checkUserGroupDataList = new ArrayList<>();
            CheckUserGroupData checkUserGroupData = null;
            for (PointProductDO pointProductDO : pointProductDOS
            ) {
                if (!StringUtils.isEmpty(pointProductDO.getUserGroup())) {
                    for (String groupId : pointProductDO.getUserGroup().split(",")
                    ) {
                        checkUserGroupData = new CheckUserGroupData();
                        checkUserGroupData.setGroupId(Integer.valueOf(groupId));
                        checkUserGroupData.setCheckResult(false);
                        checkUserGroupDataList.add(checkUserGroupData);
                    }
                } else {
                    retPointProducts.add(pointProductDO);
                }
            }
            checkUserGroupDTO.setUid(String.valueOf(uid));
            checkUserGroupDTO.setUserGroupList(checkUserGroupDataList);
            CheckUserGroupVO checkUserGroupVO = pointTaskConfigInfoService.getUserGroupCheckUser(checkUserGroupDTO);
            if (checkUserGroupVO != null && checkUserGroupVO.getUserGroupList() != null && checkUserGroupVO.getUserGroupList().size() > 0) {
                for (PointProductDO pointProductDO : pointProductDOS
                ) {
                    if (!StringUtils.isEmpty(pointProductDO.getUserGroup())) {
                        for (String groupId : pointProductDO.getUserGroup().split(",")) {
                            if (checkUserGroupVO.getUserGroupList().stream().filter(h -> h.getGroupId().equals(Integer.valueOf(groupId)) && h.getCheckResult()).count() > 0) {
                                retPointProducts.add(pointProductDO);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return retPointProducts;
    }

    @Override
    public PointProductDO getById(int id) {
        return pointProductRepository.getById(id);
    }
}
