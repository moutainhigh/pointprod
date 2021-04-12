package com.emoney.pointweb.service.biz;

import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.dto.SignInRecordCreateDTO;
import com.emoney.pointweb.repository.dao.entity.SignInRecordDO;

import java.util.Date;
import java.util.List;

public interface SignInRecordService {
    SignInRecordDO getById(Long uid, Long id);

    Result<Object> createSignInRecord(SignInRecordCreateDTO signInRecordCreateDTO);

    List<SignInRecordDO> getByUid(Long uid, Date firstDay);
}
