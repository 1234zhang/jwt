package com.example.jwtdemo.util;

import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * @author Brandon.
 * @date 2019/4/23.
 * @time 8:50.
 */

@Component
public class Base64UrlCodingUtil {
    public String encrypt(String plainText){
        String cipherText = new String(Base64.getEncoder().encode(plainText.getBytes())).split("=")[0];
        cipherText.replaceAll("\\+","-").replaceAll("/","_");
        return cipherText;
    }
    public String decrypt(String cipherText){
        cipherText = cipherText.replaceAll("_","/").replaceAll("-","+");
        int flag = cipherText.length()%4;
        if(flag == 2){
            cipherText = cipherText + "==";
        }else if(flag == 3){
            cipherText = cipherText + "=";
        }
        return new String(Base64.getDecoder().decode(cipherText.getBytes()));
    }
}
