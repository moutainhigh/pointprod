package com.emoney.pointweb.interceptor;

import ch.qos.logback.classic.Logger;
import com.emoeny.pointcommon.result.userinfo.TicketInfo;
import com.emoney.pointweb.service.biz.UserLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserLoginService userLoginService;

    @Value("${loginurl}")
    private String loginurl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        try {
            if(userLoginService.isLogin(request,response)){
                request.setAttribute("username", userLoginService.getLoginAdminUser(request,response).UserName);
                return true;
            }
        }
        catch (Exception e){
            log.error("后台用户登录错误:", e);
        }
        String url=request.getServerName();
        Integer port = request.getServerPort();
        response.sendRedirect(MessageFormat.format(loginurl,url,port.toString()));
        return false;
    }
}
