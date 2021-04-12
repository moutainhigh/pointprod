package com.emoney.pointweb.service.biz.impl;

import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoney.pointweb.repository.dao.entity.PointLimitDO;
import com.emoney.pointweb.repository.dao.entity.vo.PointLimitVO;
import com.emoney.pointweb.repository.dao.entity.vo.PointTaskConfigInfoVO;
import com.emoney.pointweb.repository.dao.mapper.PointLimitMapper;
import com.emoney.pointweb.service.biz.PointLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PointLimitServiceImpl implements PointLimitService {
    @Autowired
    private PointLimitMapper pointLimitMapper;

    @Override
    public Map<String, Object> pageList() {
        List<PointLimitDO> list = pointLimitMapper.pageList();
        List<PointLimitVO> data = JsonUtil.copyList(list, PointLimitVO.class);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", data.size());        // 总记录数
        maps.put("recordsFiltered", data.size());    // 过滤后的总记录数
        maps.put("data", data);
        return maps;
    }
}
