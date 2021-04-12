package com.emoney.pointweb.service.biz.impl;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.fastjson.JSON;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.result.ResultInfo;
import com.emoeny.pointcommon.result.userinfo.TicketInfo;
import com.emoeny.pointcommon.utils.CookieUtils;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointcommon.utils.OkHttpUtil;
import com.emoeny.pointcommon.utils.ToolUtils;
import com.emoney.pointweb.service.biz.UserLoginService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import lombok.var;
import okhttp3.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.CookieHandler;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    private static String cookiekey="emoney.pointweb.userinfo";
    private static String rediskey="emoney.pointweb.userinfo.redis.userid:";
    //随机生成密钥
    byte[] key = "emoney.pointweb.userinfo".getBytes(StandardCharsets.UTF_8);
    //构建
    SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);

    @Value("${getuserinfourl}")
    private String getuserinfourl;

    @Value("${checkticketurl}")
    private String checkticketurl;

    @Value("${apiencryptkey}")
    private String apiencryptkey;

    @Autowired
    private RedisService redisCache1;

    public boolean IsLogin(HttpServletRequest request,HttpServletResponse response){
        TicketInfo ticketInfo = GetLoginAdminUser(request,response);
        if(ticketInfo!=null&&!ticketInfo.UserID.isEmpty()){
            return true;
        }
        return false;
    }

    //获取后台登录用户信息
    public TicketInfo GetLoginAdminUser(HttpServletRequest request,HttpServletResponse response){
        String userStr= CookieUtils.getValue(request,cookiekey);
        if(userStr==null){
            return null;
        }
        TicketInfo ticketInfo=JSON.parseObject(aes.decryptStr(userStr, CharsetUtil.CHARSET_UTF_8),TicketInfo.class);
        TicketInfo userInfo=new TicketInfo();
        if(ticketInfo!=null){
            String userrediskey = rediskey + ticketInfo.UserID;
            userInfo = redisCache1.get(userrediskey,TicketInfo.class);
            if(userInfo==null){
                CookieUtils.remove(request,response,cookiekey);
            }
        }
        return userInfo;
    }

    //加载后台登录用户信息
    public void LoadAdminUserInfo(TicketInfo ticketInfo,HttpServletResponse response){
        CookieUtils.set(response,cookiekey,aes.encryptHex(JSON.toJSONString(ticketInfo)),true);
        String userrediskey = rediskey + ticketInfo.UserID;
        redisCache1.set(userrediskey,ticketInfo, ToolUtils.GetExpireTime(60*60*24));
    }

    public void RemoveAdminUserInfo(HttpServletRequest request,HttpServletResponse response){
        TicketInfo ticketInfo = GetLoginAdminUser(request,response);
        if(ticketInfo!=null){
            CookieUtils.remove(request,response,cookiekey);
            String userrediskey = rediskey + ticketInfo.UserID;
            redisCache1.remove(userrediskey);
        }
    }

    public boolean ValidateUserInfo(HttpServletResponse response,String ticket){
        //验证票据信息
        ResultInfo<Object> res = ValidateClientTicket(ticket);
        if(res.getRetCode() == 0){
            var data= res.getMessage().toString();
            TicketInfo ticketInfo=JSON.parseObject(data,TicketInfo.class);
            //获取用户安全绑定信息
            ResultInfo<String> userres= GetUserInfo(ticketInfo.UserID);
            if(userres!=null&&userres.getRetCode()==0){
                LoadAdminUserInfo(ticketInfo,response);
                return true;
            }
        }
        return false;
    }

    public ResultInfo<String> GetUserInfo(String userId){
        String url=MessageFormat.format(getuserinfourl,userId);
        String res = OkHttpUtil.get(CreateUrl(url,apiencryptkey),null);
        ResultInfo<String> result= JSON.parseObject(res,ResultInfo.class);
        return result;
    }

    public ResultInfo<Object> ValidateClientTicket(String ticket){
        String url= MessageFormat.format(checkticketurl,ticket);
        String res = OkHttpUtil.get(CreateUrl(url,apiencryptkey),null);
        ResultInfo<Object> result= JSON.parseObject(res,ResultInfo.class);
        return result;
    }

    public String CreateUrl(String url,String apiKey){
        String[] querys=URI.create(url).getQuery().replace("?","").split("&");
        StringBuilder stringBuilder=new StringBuilder();
        if(querys!=null&&querys.length>0){
            for (var item: Arrays.stream(querys).toArray()) {
                stringBuilder.append(item);
            }
        }
        String md5key = SecureUtil.md5(stringBuilder.toString() + apiKey).toUpperCase();
        return url + "&encrypt=" + md5key;
    }
}
