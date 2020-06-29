package com.yu.retrofitlib.config

/**
 * Created by yu on 2020/6/25.
 * 参数
 */
class RequestParams private constructor() {

    /**
     * 请求体参数
     */
    private var mBodyParamMap: MutableMap<String, @JvmSuppressWildcards Any> = hashMapOf()

    /**
     * 请求头参数
     */
    private var mHeaderParamMap: MutableMap<String, @JvmSuppressWildcards Any> = hashMapOf()

    /**
     * 请求体参数
     */
    private var mBodyBean: Any? = null

    companion object {

        @JvmStatic
        fun build(): RequestParams {
            return RequestParams()
        }
    }

    /**
     * 添加参数
     * @param isBody 是否添加请求体参数：true：请求体参数；false：请求头参数
     * @param key 参数键
     * @param value 参数值
     */
    public fun add(isBody: Boolean,
                   key: String, value: Any)
            : RequestParams {
        if (isBody) {
            mBodyParamMap[key] = value
        } else {
            mHeaderParamMap[key] = value
        }
        return this
    }

    /**
     * 添加请求体参数
     * @param key 参数键
     * @param value 参数值
     */
    public fun addBody(key: String, value: Any): RequestParams {
        return add(true, key, value)
    }

    /**
     * 添加请求体参数
     * @param key 参数键
     * @param value 参数值
     */
    public fun addBeanBody(bean: Any): RequestParams {
        this.mBodyBean = bean
        return this
    }

    /**
     * 添加请求头参数
     * @param key 参数键
     * @param value 参数值
     */
    public fun addHeader(key: String, value: Any): RequestParams {
        return add(false, key, value)
    }

    /**
     * 获取请求体参数
     */
    fun getBodyParams(): MutableMap<String, @JvmSuppressWildcards Any> {
        return mBodyParamMap
    }

    /**
     * 获取请求头参数
     */
    fun getHeaderParams(): MutableMap<String, @JvmSuppressWildcards Any> {
        return mHeaderParamMap
    }

    /**
     * 获取请求体对象参数
     */
    fun getBodyBean(): Any? {
        return mBodyBean
    }
}