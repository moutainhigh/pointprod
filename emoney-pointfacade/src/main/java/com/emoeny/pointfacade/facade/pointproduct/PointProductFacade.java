package com.emoeny.pointfacade.facade.pointproduct;

import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.vo.PointProductVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/22 11:53
 */

@RequestMapping("/api/pointproduct")
@Validated
public interface PointProductFacade {

    /**
     * 查询有效商品列表
     *
     * @param productVersion
     * @return
     */
    @GetMapping("/query")
    Result<List<PointProductVO>> queryPointProducts(@NotNull(message = "用户id不能为空") Long uid,@NotNull(message = "产品版本不能为空") String productVersion);

    /**
     * 根据用户id查询单个商品信息
     *
     * @param id
     * @return
     */
    @GetMapping("/querypointproductinfo")
    Result<PointProductVO> queryPointProduct(@NotNull(message = "商品id不能为空") Integer id);
}
