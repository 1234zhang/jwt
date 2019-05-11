package com.example.jwtdemo.helper;

import com.example.jwtdemo.util.Base64UrlCodingUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Brandon.
 * @date 2019/4/25.
 * @time 9:03.
 */

@Component
public class JwtHelper {
    @Autowired
    Base64UrlCodingUtil base64UrlCodingUtil;
    Gson gson = new Gson();
    public Claims parseJwt(String jsonWebToken, String base64Security) {
        SecretKey key = generalKey(base64Security);
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jsonWebToken).getBody();
        return claims;
    }
    public String createJwt(String username, String appid, String role, String audience, String issuer,
                                   long TTLMillis, String base64Security){
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Map<String,Object> claims = new HashMap<String,Object>();
        claims.put("uid", appid);
        claims.put("user_name", username);
        claims.put("role",role);
        claims.put("audience",audience);
        SecretKey key = generalKey(base64Security);

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setIssuer(issuer)
                .setSubject(gson.toJson(claims))
                .signWith(signatureAlgorithm, key);
        if (TTLMillis >= 0) {
            long expMillis = nowMillis + TTLMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }
    public SecretKey generalKey(String base64Security){
        byte[] encodedKey = base64UrlCodingUtil.encrypt(base64Security).getBytes();
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }
}
