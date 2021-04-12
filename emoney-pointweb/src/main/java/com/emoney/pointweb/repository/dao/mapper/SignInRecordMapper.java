package com.emoney.pointweb.repository.dao.mapper;

import com.emoney.pointweb.repository.dao.entity.SignInRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface SignInRecordMapper {
    SignInRecordDO getById(Long uid, Long id);

    Integer insert(SignInRecordDO signInRecordDO);

    List<SignInRecordDO> getByUid(Long uid, Date firstDay);
}
