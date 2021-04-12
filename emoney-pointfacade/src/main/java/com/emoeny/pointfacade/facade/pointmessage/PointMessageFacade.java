package com.emoeny.pointfacade.facade.pointmessage;

import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.vo.PointMessageVO;
import com.emoeny.pointfacade.model.vo.PointOrderVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/4/12 15:36
 */
@RequestMapping("/api/pointmessage")
@Validated
public interface PointMessageFacade {
    /**
     * 根据用户id查询用户订单列表
     */
    @GetMapping("/query")
    Result<List<PointMessageVO>> queryPointMessages(@NotNull(message = "用户id不能为空") Long uid);
}
