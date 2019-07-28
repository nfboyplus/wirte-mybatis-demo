package com.mybatis.demo.aop;

import com.mybatis.demo.annotation.ExtInsert;
import com.mybatis.demo.annotation.ExtParam;
import com.mybatis.demo.annotation.ExtSelect;
import com.mybatis.demo.utils.JDBCUtils;
import com.mybatis.demo.utils.SQLUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代理模式
 */
public class MyInvocationHandlerMybatis implements InvocationHandler {

    /**
     * 这个就是我们要代理的真实对象
     */
    private Object subject;

    /**
     * 构造方法，给我们要代理的真实对象赋初值
     *
     * @param subject
     */
    public MyInvocationHandlerMybatis(Object subject) {
        this.subject = subject;
    }


    /**
     * 该方法负责集中处理动态代理类上的所有方法调用。
     * 调用处理器根据这三个参数进行预处理或分派到委托类实例上反射执行
     *
     * @param proxy  代理类实例
     * @param method 被调用的方法对象
     * @param args   调用参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("使用动态代理拦截接口方法开始");
        //判断方法上是否有ExtInsert注解
        ExtInsert extInsert = method.getAnnotation(ExtInsert.class);
        if (extInsert != null) {
            return insertSQL(extInsert, method, args);
        }
        //判断方法上注解类型
        ExtSelect extSelect = method.getAnnotation(ExtSelect.class);
        if (extSelect != null) {
            return selectMybatis(extSelect, method, args);
        }
        return null;
    }

    private Object selectMybatis(ExtSelect extSelect, Method method, Object[] args) {
        try {
            // 获取查询SQL语句
            String selectSQL = extSelect.value();
            // 将方法上的参数存放在Map集合中
            Parameter[] parameters = method.getParameters();
            // 获取方法上参数集合
            ConcurrentHashMap<Object, Object> parameterMap = getExtParams(parameters, args);
            // 获取SQL传递参数
            List<String> sqlSelectParameter = SQLUtils.sqlSelectParameter(selectSQL);
            // 排序参数
            List<Object> paramValues = new ArrayList<>();
            for (int i = 0; i < sqlSelectParameter.size(); i++) {
                String parameterName = sqlSelectParameter.get(i);
                Object object = parameterMap.get(parameterName);
                paramValues.add(object.toString());
            }
            // 变为?号
            String newSql = SQLUtils.paramQuestion(selectSQL, sqlSelectParameter);
            System.out.println("执行SQL:" + newSql + "参数信息:" + paramValues.toString());
            // 调用JDBC代码查询
            ResultSet rs = JDBCUtils.query(newSql, paramValues);
            // 获取返回类型
            Class<?> returnType = method.getReturnType();
            if (!rs.next()) {
                // 没有查找数据
                return null;
            }
            // 向上移动
            rs.previous();
            // 实例化对象
            Object newInstance = returnType.newInstance();
            while (rs.next()) {
                for (String parameterName : sqlSelectParameter) {
                    // 获取集合中数据
                    Object value = rs.getObject(parameterName);
                    // 查找对应属性
                    Field field = returnType.getDeclaredField(parameterName);
                    // 设置允许私有访问
                    field.setAccessible(true);
                    // 赋值参数
                    field.set(newInstance, value);
                }
            }
            return newInstance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private Object insertSQL(ExtInsert extInsert, Method method, Object[] args) {
        //获取注解上的SQL
        String insertSql = extInsert.value();
        System.out.println("插入数据，sql:" + insertSql);
        //获取方法上的参数
        Parameter[] parameters = method.getParameters();
        //将方法上的参数放在Map集合中
        ConcurrentHashMap<Object,Object> parameterMap = getExtParams(parameters, args);
        //获取SQL语句上需要传递的参数
        String[] sqlParameter = SQLUtils.sqlInsertParameter(insertSql);
        List<Object> paramValues = new ArrayList<>();
        for (int i = 0; i < sqlParameter.length; i++) {
            String str = sqlParameter[i];
            Object object = parameterMap.get(str);
            paramValues.add(object);
        }
        //将SQL语句替换为？号
        String newSql = SQLUtils.paramQuestion(insertSql, sqlParameter);
        System.out.println("替换后的语句，newSql:" + newSql);
        //调用jdbc代码执行
        int insertResult = JDBCUtils.insert(newSql, true, paramValues);
        return insertResult;
    }

    /**
     * 获取方法上参数集合
     */
    private ConcurrentHashMap<Object, Object> getExtParams(Parameter[] parameters, Object[] args) {
        // 获取方法上参数集合
        ConcurrentHashMap<Object, Object> parameterMap = new ConcurrentHashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            // 参数信息
            Parameter parameter = parameters[i];
            ExtParam extParam = parameter.getDeclaredAnnotation(ExtParam.class);
            // 参数名称
            String paramValue = extParam.value();
            // 参数值
            Object oj = args[i];
            parameterMap.put(paramValue, oj);
        }
        return parameterMap;
    }

}
