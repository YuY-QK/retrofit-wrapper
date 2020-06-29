package com.yu.retrofitlib.interceptor

import com.orhanobut.logger.Logger
import okhttp3.*
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

/**
 * Created by yu on 2020/6/26.
 * okhttp 基础请求拦截器
 */
abstract class BaseHttpRequestInterceptor: Interceptor {

    private val TAG = "BaseHttpRequestInterceptor"

    private val UTF8 = Charset.forName("UTF-8")

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest: Request = chain.request()
        return originRequest.body()?.let {
            var jsonObject = JSONObject()
            if (it is JSONObject) {
                val oldBody: FormBody = it as FormBody
                try {
                    for (i in 0 until oldBody.size()) {
                        jsonObject.put(oldBody.name(i), oldBody.value(i))
                    }
                    addCommonParams(originRequest, jsonObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    throw IllegalArgumentException(TAG + "-JSONException:" + e.message)
                }
                chain.proceed(getRequest(originRequest, jsonObject))
            } else if (it.contentType() == null) {
                try {
                    addCommonParams(originRequest, jsonObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                chain.proceed(getRequest(originRequest, jsonObject))
            } else if (originRequest.body()?.contentType() == MediaType.parse("application/json; charset=utf-8")
                    || originRequest.body()?.contentType() == MediaType.parse("application/json; charset=UTF-8")) {
                val buffer = Buffer()
                it.writeTo(buffer)
                var charset: Charset = UTF8
                val contentType = it.contentType()
                contentType?.charset(UTF8)?.let { charset = it  }
                val content = buffer.readString(charset)
                try {
                    jsonObject = JSONObject(content)
                    addCommonParams(originRequest, jsonObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Logger.e("$TAG：$content")
                chain.proceed(getRequest(originRequest, jsonObject))
            } else {
                chain.proceed(originRequest)
            }
        }?: chain.proceed(originRequest)
    }


    /**
     * 处理公共参数
     *
     * @param originRequest
     * @param jsonObject
     */
    @Throws(JSONException::class)
    abstract fun addCommonParams(originRequest: Request, jsonObject: JSONObject)

    /**
     * 构建新的请求
     *
     * @param originRequest
     * @param jsonObject
     * @return
     */
    abstract fun getRequest(originRequest: Request, jsonObject: JSONObject): Request

}