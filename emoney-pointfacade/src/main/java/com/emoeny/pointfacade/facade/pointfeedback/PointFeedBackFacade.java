package com.emoeny.pointfacade.facade.pointfeedback;

import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.dto.PointFeedBackCreateDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping("/api/pointfeedback")
@Validated
public interface PointFeedBackFacade {

    /*
     * 创建意见反馈
     * @author lipengcheng
     * @date 2021-4-19 17:47
     * @param pointFeedBackCreateDTO
     * @return com.emoeny.pointcommon.result.Result<java.lang.Object>
     */
    @PostMapping("/add")
    Result<Object> createFeedBack(@RequestBody @Valid PointFeedBackCreateDTO pointFeedBackCreateDTO);
}
