package com.emoney.pointweb.service.biz;

import com.emoeny.pointcommon.result.userperiod.UserPeriodResult;
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

    /**
     * 查询用户生命周期
     * @param uid
     * @return
     */
    UserPeriodResult getUserPeriod(long uid);

    /**
     * 根据uid获取pid
     * @param uid
     * @return
     */
    String getPidByUserId(Long uid);
}
