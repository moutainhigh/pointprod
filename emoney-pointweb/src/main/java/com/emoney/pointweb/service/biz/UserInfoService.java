package com.emoney.pointweb.service.biz;

import com.emoney.pointweb.repository.dao.entity.vo.UserInfoVO;

import java.util.List;

public interface UserInfoService {
    /**
     * 通过uid调用内部网关获取用户信息
     *
     * @param uid
     * @return
     */
    List<UserInfoVO> getUserInfoByUid(Long uid);
}
