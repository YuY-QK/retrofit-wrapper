package com.yu.retrofitlib.interceptor

import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

/**
 * Created by yu on 2020/6/26.
 * okhttp默认请求拦截器
 */
class DefaultHttpRequestInterceptor: BaseHttpRequestInterceptor() {

    override fun getRequest(originRequest: Request, jsonObject: JSONObject): Request {
        val newBody: RequestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString())
        return originRequest.newBuilder()
                .method(originRequest.method(), newBody)
                .build()
    }

    override fun addCommonParams(originRequest: Request, jsonObject: JSONObject) {

    }
}