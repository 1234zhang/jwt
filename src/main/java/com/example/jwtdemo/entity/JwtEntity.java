package com.example.jwtdemo.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author Brandon.
 * @date 2019/4/25.
 * @time 8:43.
 */

@Data
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "audience")
@Component
public class JwtEntity {
    private String clientId;
    private String base64Secret;
    private String name;
    private int expiresSecond;
}
