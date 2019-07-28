package com.mybatis.demo.mapper;

import com.mybatis.demo.annotation.ExtInsert;
import com.mybatis.demo.annotation.ExtMapper;
import com.mybatis.demo.annotation.ExtParam;
import com.mybatis.demo.annotation.ExtSelect;
import com.mybatis.demo.entity.User;

/**
 * 接口既然不能被实例化？那么我们是怎么实现能够调用的？
 * 动态代理
 */
@ExtMapper
public interface UserMapper {

    @ExtSelect("select * from test_user where name=#{name} and age=#{age}")
    User selectUser(@ExtParam("name") String name, @ExtParam("age") Integer age);

    @ExtInsert("insert into test_user(name,age) values(#{name},#{age})")
    int insertUser(@ExtParam("name") String name, @ExtParam("age") Integer age);

}
