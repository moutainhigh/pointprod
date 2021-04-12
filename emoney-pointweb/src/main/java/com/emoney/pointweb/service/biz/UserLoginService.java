package com.emoney.pointweb.service.biz;

import com.emoeny.pointcommon.result.userinfo.TicketInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserLoginService {
    TicketInfo GetLoginAdminUser(HttpServletRequest request, HttpServletResponse response);
    boolean ValidateUserInfo(HttpServletResponse response,String ticket);
    boolean IsLogin(HttpServletRequest request, HttpServletResponse response);
    void RemoveAdminUserInfo(HttpServletRequest request, HttpServletResponse response);
}
