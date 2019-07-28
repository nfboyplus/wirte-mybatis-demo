package com.mybatis.demo.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtParam {

    String value();

}
