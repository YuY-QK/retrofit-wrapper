package com.yu.retrofitlib.interceptor

import okhttp3.Interceptor

/**
 * Created by yu on 2020/6/25.
 * okhttp拦截器管理
 */
object InterceptorManager {

    @JvmStatic
    public var requestInterceptor: BaseHttpRequestInterceptor = DefaultHttpRequestInterceptor()

    @JvmStatic
    public var responseInterceptor: Interceptor? = null

}