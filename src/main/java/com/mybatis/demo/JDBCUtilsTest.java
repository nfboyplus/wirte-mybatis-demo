package com.mybatis.demo;

import com.mybatis.demo.utils.JDBCUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 测试 JDBCUtils 工具类
 */
public class JDBCUtilsTest {

    public static void main(String[] args) throws SQLException {
        //插入
        String insertSql = "insert into test_user (name,age) values(?,?)";
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add("刘松");
        arrayList.add(28);
        int insert = JDBCUtils.insert(insertSql, true, arrayList);
        System.out.println(insert);
        //查询
        ArrayList<Object> objects = new ArrayList<>();
        objects.add("刘松");
        objects.add(28);
        ResultSet resultSet = JDBCUtils.query("select * from test_user where name=? and age=?",objects);
        while (resultSet.next()){
            String name = resultSet.getString("name");
            System.out.println(name);
        }
    }
}
