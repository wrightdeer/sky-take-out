package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     * @param openid 用户的openid
     * @return 查询到的用户对象，如果未找到则返回null
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入用户数据
     * @param user 要插入的用户对象
     */
    void insert(User user);

    /**
     * 根据id查询用户
     * @param id 用户的id
     * @return 查询到的用户对象，如果未找到则返回null
     */
    @Select("select * from user where id = #{id}")
    User getById(Long id);
}