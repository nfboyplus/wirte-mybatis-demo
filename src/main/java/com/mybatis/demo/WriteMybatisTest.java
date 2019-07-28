package com.mybatis.demo;

import com.mybatis.demo.entity.User;
import com.mybatis.demo.mapper.UserMapper;
import com.mybatis.demo.utils.SqlSession;

/**
 * 纯手写mybatis注解版本框架
 * 使用动态代理方法虚拟调用
 */
public class WriteMybatisTest {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        //使用动态代理方法虚拟调用
        UserMapper userMapper = SqlSession.getMapper(UserMapper.class);
        //先走拦截 invoke
        int insertUserResult = userMapper.insertUser("松子",1);
        System.out.println(insertUserResult);
        //查询
        User user = userMapper.selectUser("松松", 11);
        System.out.println(user.toString());
    }

    /**
     * 打印结果：
     * 使用动态代理拦截接口方法开始
     * 插入数据，sql:insert into test_user(name,age) values(#{name},#{age})
     * 替换后的语句，newSql:insert into test_user(name,age) values(?,?)
     * 282
     */

    /**
     * 打印结果：
     * 使用动态代理拦截接口方法开始
     * 执行SQL:select * from test_user where name=? and age=?参数信息:[松松, 11]
     * User(name=松松, age=11)
     */


}
