package com.example.jwtdemo.mapper;

import com.example.jwtdemo.entity.UserInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author Brandon.
 * @date 2019/4/23.
 * @time 15:35.
 */

@Mapper
@Repository
public interface UserInfoMapper {
    @Select("select * from user_info where(username = #{username})")
    UserInfoEntity getUser(String username);
}
