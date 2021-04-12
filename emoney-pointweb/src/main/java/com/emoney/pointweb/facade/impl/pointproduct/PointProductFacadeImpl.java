package com.emoney.pointweb.facade.impl.pointproduct;

import com.alibaba.fastjson.JSON;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointfacade.facade.pointproduct.PointProductFacade;
import com.emoeny.pointfacade.model.vo.PointProductVO;
import com.emoney.pointweb.repository.dao.entity.PointProductDO;
import com.emoney.pointweb.service.biz.PointProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/22 11:57
 */
@RestController
@Validated
@Slf4j
public class PointProductFacadeImpl implements PointProductFacade {

    @Autowired
    private PointProductService pointProductService;

    @Override
    public Result<List<PointProductVO>> queryPointProducts(@NotNull(message = "产品版本不能为空") String productVersion) {
        try {
            List<PointProductDO> pointProductDOS = pointProductService.getAllEffectiveProducts(new Date(),productVersion);
            return Result.buildSuccessResult(JsonUtil.copyList(pointProductDOS, PointProductVO.class));
        } catch (Exception e) {
            log.error("queryPointProducts error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<PointProductVO> queryPointProduct(@NotNull(message = "商品id不能为空") Integer id) {
        try {
            return Result.buildSuccessResult(JsonUtil.toBean(JSON.toJSONString(pointProductService.getById(id)), PointProductVO.class));
        } catch (Exception e) {
            log.error("queryPointProduct error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }
}
