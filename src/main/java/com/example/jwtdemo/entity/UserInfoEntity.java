package com.example.jwtdemo.entity;

import lombok.Data;

/**
 * @author Brandon.
 * @date 2019/4/23.
 * @time 15:25.
 */

@Data
public class UserInfoEntity {
    public String username;
    public String password;
    public int sex;
    public int age;
    public String headImgUrl;
}
