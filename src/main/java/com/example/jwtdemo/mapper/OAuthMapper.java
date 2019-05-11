package com.example.jwtdemo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author Brandon.
 * @date 2019/4/22.
 * @time 22:58.
 */

@Mapper
@Repository
public interface OAuthMapper {
    @Select("select secret from OAuth_info where appid = #{appid}")
    String getSecret(String appid);
}
