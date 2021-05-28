package com.emoeny.pointfacade.facade.signinrecord;

import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.dto.SignInRecordCreateDTO;
import com.emoeny.pointfacade.model.vo.SignInRecordVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequestMapping("/api/signinrecord")
@Validated
public interface SignInRecordFacade {

    /**
     *
     * 对外提供服务，增加签到记录
     *
     */
    @PostMapping("/add")
    Result<Object> createSignInRecord(@RequestBody @Valid SignInRecordCreateDTO signInRecordCreateDTO);

    /**
     *
     * 根据用户id查询用户签到记录，只返回最近一年
     *
     */
    @GetMapping("/query")
    Result<List<SignInRecordVO>> querySignInRecord(@NotNull(message = "用户id不能为空") Long uid);

    /**
     *
     * 获取签到提示语
     *
     */
    @GetMapping("/querytips")
    Result<String> querySignInRecordTips(@NotNull(message = "用户id不能为空") Long uid,@NotNull(message = "产品版本不能为空") String productVersion,@NotNull(message = "发布平台不能为空") String publishPlatFormType);
}
