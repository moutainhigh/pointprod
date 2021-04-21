package com.emoney.pointweb.service.biz;

import com.emoeny.pointcommon.result.userinfo.TicketInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserLoginService {
    TicketInfo getLoginAdminUser(HttpServletRequest request, HttpServletResponse response);
    boolean validateUserInfo(HttpServletResponse response,String ticket);
    boolean isLogin(HttpServletRequest request, HttpServletResponse response);
    void removeAdminUserInfo(HttpServletRequest request, HttpServletResponse response);
}
