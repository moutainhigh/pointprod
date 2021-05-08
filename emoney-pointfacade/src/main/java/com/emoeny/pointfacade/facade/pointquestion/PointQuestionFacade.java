package com.emoeny.pointfacade.facade.pointquestion;

import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.vo.PointQuestionVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.NotNull;

@RequestMapping("/api/pointquestion")
@Validated
public interface PointQuestionFacade {
    /*
     * 获取每日一答
     * @author lipengcheng
     * @date 2021-5-7 13:32
     * @return com.emoeny.pointcommon.result.Result<com.emoeny.pointfacade.model.vo.PointQuestionVO>
     */
    @GetMapping("/querypointquestion")
    Result<PointQuestionVO> queryPointQuestion();

    /*
     * 根据id获取题目内容
     * @author lipengcheng
     * @date 2021-5-8 17:12
     * @param id 
     * @return com.emoeny.pointcommon.result.Result<com.emoeny.pointfacade.model.vo.PointQuestionVO>
     */
    @GetMapping("/querypointquestionbyid")
    Result<PointQuestionVO> queryPointQuestionById(@NotNull(message = "题目id不能为空") Integer id);
}
