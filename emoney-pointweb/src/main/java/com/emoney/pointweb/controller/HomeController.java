package com.emoney.pointweb.controller;

import com.emoney.pointweb.service.biz.UserLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;

@Controller
@Slf4j
public class HomeController {

    @Resource
    private UserLoginService userLoginService;

    @Value("${loginurl}")
    private String loginurl;

    @RequestMapping("/index")
    public String index(HttpServletRequest request, HttpServletResponse response){
        return "index";
    }

    @RequestMapping("/login")
    public void login(HttpServletResponse response, HttpServletRequest request) throws IOException {
        if (userLoginService.IsLogin(request,response))
        {
            response.sendRedirect("/index");
        }

        String ticket= URLEncoder.encode(request.getParameter("ticket"),"UTF-8");
        if(!ticket.isEmpty()){
            if(userLoginService.ValidateUserInfo(response,ticket)){
                response.sendRedirect("/index");
                return;
            }
        }
        String url=request.getServerName();
        int port = request.getServerPort();
        response.sendRedirect(MessageFormat.format(loginurl,url,port));
    }

    @RequestMapping("/logout")
    public void logout(HttpServletResponse response, HttpServletRequest request) throws IOException {
        userLoginService.RemoveAdminUserInfo(request,response);
        String url=request.getServerName();
        Integer port = request.getServerPort();
        response.sendRedirect(MessageFormat.format(loginurl,url,port.toString()));
    }
}
