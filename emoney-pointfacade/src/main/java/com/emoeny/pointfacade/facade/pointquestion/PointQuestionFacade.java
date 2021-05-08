package com.emoeny.pointfacade.facade.pointquestion;

import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.vo.PointQuestionVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/pointquestion")
public interface PointQuestionFacade {
    /*
     * 获取每日一答
     * @author lipengcheng
     * @date 2021-5-7 13:32
     * @return com.emoeny.pointcommon.result.Result<com.emoeny.pointfacade.model.vo.PointQuestionVO>
     */
    @GetMapping("/querypointquestion")
    Result<PointQuestionVO> queryPointQuestion();
}
