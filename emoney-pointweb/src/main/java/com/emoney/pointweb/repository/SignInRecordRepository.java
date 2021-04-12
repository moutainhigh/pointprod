package com.emoney.pointweb.repository;

import com.emoney.pointweb.repository.dao.entity.SignInRecordDO;

import java.util.Date;
import java.util.List;

public interface SignInRecordRepository {
    SignInRecordDO getById(Long uid, Long id);

    Integer insert(SignInRecordDO signInRecordDO);

    List<SignInRecordDO> getByUid(Long uid, Date firstDay);
}
