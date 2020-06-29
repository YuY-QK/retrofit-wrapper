package com.yu.retrofitlib.gson

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.yu.retrofitlib.gson.adapter.*
import com.yu.retrofitlib.gson.strategy.GsonInclusionStrategy
import java.lang.reflect.Type
import java.util.regex.Pattern

/**
 * Created by yu on 2020/6/28.
 * Gson工具类
 *
 */
object GsonUtil {

    /**
     * 正则表达式：验证json对象是否为对象
     */
    const val REGEX_JSON_OBJECT = "^\\{.*\\}$"

    /**
     * 正则表达式：验证json对象是否为数组
     */
    const val REGEX_JSON_ARRAY = "^\\[.*\\]$"

    /**
     * 正则表达式：验证json对象是否为数组对象
     */
    const val REGEX_JSON_ARRAY_OBJ = "^\\[\\{.*\\}\\]$"


    /**
     * 是否为json相关类型
     *
     * @param jsonStr     验证数据
     * @param regex，验证表达式
     * @return
     */
    @JvmStatic
    fun isJson(jsonStr: String?, regex: String): Boolean {
        return jsonStr != null && Pattern.matches(regex, jsonStr.trim { it <= ' ' })
    }

    /**
     * 获取构建的Gson
     *
     * @return
     */
    @JvmStatic
    fun factory(): Gson {
        return buildGsonTypeAdapter().create()
    }

    /**
     * 构建Gson的注册类型
     *
     * @return
     */
    @JvmStatic
    fun buildGsonTypeAdapter(): GsonBuilder {
        return GsonBuilder()
                .registerTypeAdapter(Int::class.java, IntegerTypeAdapter())
                .registerTypeAdapter(Int::class.javaPrimitiveType, IntegerTypeAdapter())
                .registerTypeAdapter(Float::class.java, FloatTypeAdapter())
                .registerTypeAdapter(Float::class.javaPrimitiveType, FloatTypeAdapter())
                .registerTypeAdapter(Double::class.java, DoubleTypeAdapter())
                .registerTypeAdapter(Double::class.javaPrimitiveType, DoubleTypeAdapter())
                .registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter())
                .registerTypeAdapter(Boolean::class.javaPrimitiveType, BooleanTypeAdapter())
                .registerTypeAdapter(String::class.java, StringTypeAdapter())
                //TODO Map解析器，暂时不用，需测试
                //.registerTypeAdapter(new TypeToken<Object>() {}.getType(), new MapTypeAdapter())
    }


    /**
     * 将json字符串转换为指定类
     *
     * @param json JSON串
     * @param type 类型
     * @param <T>  泛型
     * @return 转换对象 </T>
     */
    @JvmStatic
    fun <T> trans2Bean(json: String?, type: Type?): T? {
        return if (TextUtils.isEmpty(json)) {
            null
        } else {
            factory().fromJson(json, type)
        }
    }

    /**
     * 将实体bean转换为字符串
     *
     * @param obj         转换对象
     * @param includeOnly 是否只转换包含 [com.yu.retrofitlib.gson.strategy.GsonInclude] 注解的字段
     * @return
     */
    @JvmStatic
    fun trans2Str(obj: Any?, includeOnly: Boolean = false): String {
        if (obj == null) {
            return ""
        }
        val gson: Gson
        if (includeOnly) {
            gson = buildGsonTypeAdapter()
                    .setExclusionStrategies(GsonInclusionStrategy())
                    .create()
        } else {
            gson = factory()
        }
        return gson.toJson(obj) ?: ""
    }

}