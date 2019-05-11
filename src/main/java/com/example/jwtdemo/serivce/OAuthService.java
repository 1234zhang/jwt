package com.example.jwtdemo.serivce;

import com.example.jwtdemo.entity.JwtEntity;
import com.example.jwtdemo.entity.UserInfoEntity;
import com.example.jwtdemo.helper.JwtHelper;
import com.example.jwtdemo.mapper.OAuthMapper;
import com.example.jwtdemo.mapper.UserInfoMapper;
import com.example.jwtdemo.util.Base64UrlCodingUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Brandon.
 * @date 2019/4/22.
 * @time 21:39.
 */

@Service
public class OAuthService<T> {
    @Autowired
    OAuthMapper oAuthMapper;
    @Autowired
    Base64UrlCodingUtil base64UrlCodingUtil;
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    JwtHelper jwtHelper;

    private Gson gson = new Gson();
    public boolean checkLogin(String appid,String secret){
        String checkSecret = oAuthMapper.getSecret(appid);
        if(checkSecret != null){
            if(secret.equals(checkSecret)){
                return true;
            }
        }
        return false;
    }
    public String getCode(String username, String password,String appid) {
        Map<String,T> codeMap = new HashMap<>();
        UserInfoEntity userInfoEntity = userInfoMapper.getUser(username);
        if(userInfoEntity != null){
            if(userInfoEntity.password.equals(password)){
                codeMap.put("appid",(T)appid);
                codeMap.put("username",(T)userInfoEntity.username);
                codeMap.put("life",(T)new Integer(1));
                stringRedisTemplate.opsForValue().set("userinfo",gson.toJson(userInfoEntity),120, TimeUnit.MINUTES);
                return base64UrlCodingUtil.encrypt(gson.toJson(codeMap));
            }
        }
        return null;
    }
    public String getAccessToken(String code, String appid, HttpServletRequest request){
        Map<String,T> codeMap = gson.fromJson(base64UrlCodingUtil.decrypt(code),HashMap.class);
        Map<String,T> accessTokenMap = new HashMap<>();
        BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        JwtEntity jwtEntity = (JwtEntity) factory.getBean("jwtEntity");
        double life = (double)codeMap.get("life");
        if(life-- == 1){
            UserInfoEntity userInfoEntity = gson.fromJson(stringRedisTemplate.opsForValue().get("userinfo"),UserInfoEntity.class);
            String jwt = jwtHelper.createJwt(userInfoEntity.username,appid,"admission","localhost",
                    "localhost",7200000,jwtEntity.getBase64Secret());
            accessTokenMap.put("access_token", (T) jwt);
            accessTokenMap.put("expiration",(T) new Double(7200));
            return gson.toJson(accessTokenMap);
        }
        return null;
    }
    public UserInfoEntity getUserInfo(String accessToken,HttpServletRequest request){
        BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        JwtEntity jwtEntity = (JwtEntity) factory.getBean("jwtEntity");

        Claims claims = jwtHelper.parseJwt(accessToken, jwtEntity.getBase64Secret());
       if("admission".equals(claims.get("role"))){
           return gson.fromJson(stringRedisTemplate.opsForValue().get("userinfo"),UserInfoEntity.class);
       }
       return null;
    }
}
