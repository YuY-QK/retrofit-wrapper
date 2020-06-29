package com.yu.retrofitlib.gson.strategy

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by yu on 2020-06-28.
 *
 * GSON保留字段解析的注解
 */
@Target(AnnotationTarget.FIELD)
@Retention(RetentionPolicy.RUNTIME)
annotation class GsonInclude