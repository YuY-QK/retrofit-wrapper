package com.yu.retrofitlib.gson.strategy

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes

/**
 * Created by yu on 2020-06-28.
 *
 */
class GsonInclusionStrategy : ExclusionStrategy {

    /**
     * 是否跳过属性 不序列化
     * 返回 false 代表 属性要进行序列化
     * @param f
     * @return
     */
    override fun shouldSkipField(f: FieldAttributes): Boolean {
        //判断当前实体是否包含GsonInclude注解，不包含则过滤掉
        return f.getAnnotation(GsonInclude::class.java) == null
    }

    override fun shouldSkipClass(clazz: Class<*>?): Boolean = false
}