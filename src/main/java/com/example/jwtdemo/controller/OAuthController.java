package com.example.jwtdemo.controller;

import com.example.jwtdemo.entity.UserInfoEntity;
import com.example.jwtdemo.serivce.OAuthService;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Brandon.
 * @date 2019/4/22.
 * @time 21:24.
 */

@RestController
@RequestMapping("/oauth")
public class OAuthController {
    @Autowired
    OAuthService oAuthService;
    @GetMapping(value = "/authorize")
    public String getCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String appid = request.getParameter("appid");
        String secret = request.getParameter("secret");
        String redirectUrl = request.getParameter("redirect_url");
        HttpSession session = request.getSession();
        if(oAuthService.checkLogin(appid,secret)) {
            session.setAttribute("redirect_url", redirectUrl);
            session.setAttribute("appid",appid);
            response.sendRedirect("/index.html");
        }
        return "{\"msg\":\"error\"}";
    }
    @GetMapping(value = "/login")
    public String userLogin(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        HttpSession session = request.getSession();
        String code = oAuthService.getCode(username,password,(String)session.getAttribute("appid"));
        if(code != null) {
            String redirectUrl = (String) session.getAttribute("redirect_url");
            response.sendRedirect(redirectUrl + "?code = " + code);
        }
        return "{\"msg\":\"用户不存在\"}";
    }
    @GetMapping("/access_token")
    public String getAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        String redirectUrl = request.getParameter("redirect_url");
        String appid = request.getParameter("appid");
        String accessToken = oAuthService.getAccessToken(code,appid,request);
        if(accessToken != null){
            response.sendRedirect(redirectUrl + "?access_token=" + oAuthService.getAccessToken(code,appid,request));
        }
        return "{\"msg\":\"获取access_token失败\"}";
    }
    @GetMapping("/user_info")
    public String getUserInfo(HttpServletRequest request,HttpServletResponse response){
        Gson gson = new Gson();
        String accessToken = request.getParameter("access_token");
        UserInfoEntity userInfoEntity = oAuthService.getUserInfo(accessToken,request);
        if(userInfoEntity != null){
            return gson.toJson(userInfoEntity);
        }
        return "{\"msg\":\"用户信息获取失败\"}";
    }
}
