package com.gwh.axb.config;

import java.lang.annotation.*;

/**
 import java.lang.annotation.*;
 */
@Target({ ElementType.PARAMETER,ElementType.METHOD }) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
@Documented//生成文档
public @interface SystemControllerLog {
    /** 操作事件     */
    String operation() default "";
    /** 日志类型 */
    String type();
}
