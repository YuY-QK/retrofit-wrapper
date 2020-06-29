package com.yu.retrofitlib.callback

import android.accounts.NetworkErrorException
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.yu.retrofitlib.NetManager
import com.yu.retrofitlib.config.HttpCode
import com.yu.retrofitlib.gson.GsonUtil
import com.yu.retrofitlib.util.Utils
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.TypeVariable
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Created by yu on 2020/6/25.
 * 请求回调
 */
abstract class OnNetCallback<T>(private var beanClass: Class<*>?) : Callback<ResponseBody> {

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        if (call.isCanceled) {
            Logger.d("HttpCallback is canceled")
            return
        }
        Logger.e(t, "HttpCallback onFailure")
        when (t) {
            is JsonSyntaxException -> {
                onError(HttpCode.JSON_SYNTAX_EXCEPTION, NetManager.Config.noNetworkTips, t)
            }
            is ConnectException -> {
                onError(HttpCode.NETWORK_CONNECT_ERROR, NetManager.Config.noNetworkTips, t)
            }
            is SocketTimeoutException -> {
                onError(HttpCode.NETWORK_CONNECT_ERROR, NetManager.Config.noNetworkTips, t)
            }
            else -> {
                onError(HttpCode.NETWORK_CONNECT_ERROR, NetManager.Config.noNetworkTips, t)
            }
        }
    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        if (call.isCanceled) {
            Logger.d("HttpCallback is canceled")
            return
        }
        if (response.code() == 200) {
            try {
                onResponseSuccess(response)
            } catch (e: Exception) {
                e.printStackTrace()
                onError(
                        "",
                        "加载失败",
                        e)
            }
        } else {
            onError(
                    HttpCode.HTTP_SERVER_ERROR,
                    NetManager.Config.noNetworkTips,
                    NetworkErrorException("服务器异常：code=" + response.code() + ",msg=" + response.message()))
        }
    }

    @Throws(Exception::class)
    protected open fun onResponseSuccess(response: Response<ResponseBody>) {
        var dataCode = ""
        var message = ""
        var dataJson = ""
        val body = response.body()
        val bodyJsonObj = JSONObject(body!!.string())
        if (bodyJsonObj.has(NetManager.Label.DATA_NAME_CODE)) {
            dataCode = bodyJsonObj.optString(NetManager.Label.DATA_NAME_CODE)
        }
        if (bodyJsonObj.has(NetManager.Label.DATA_NAME_MESSAGE)) {
            message = bodyJsonObj.optString(NetManager.Label.DATA_NAME_MESSAGE)
        }
        if (bodyJsonObj.has(NetManager.Label.DATA_NAME_BODY)) {
            dataJson = bodyJsonObj.optString(NetManager.Label.DATA_NAME_BODY)
            /*if (!TextUtils.isEmpty(dataJson)){
                if("[]".equals(dataJson.trim()) || "{}".equals(dataJson.trim())) {
                    dataJson = "";
                }
            }*/
        }

        when {
            HttpCode.OK == dataCode -> {
                //onSuccess(handleResultData(dataJson))
                onSuccess(getResultData(dataJson))
            }
            handleHttpDataCode(dataCode) -> {
                onError(dataCode, message, null)
            }
            else -> {
                onError(dataCode, message, null)
            }
        }

    }

    private fun getResultData(dataJson: String): T? {
        if (beanClass==null || (beanClass == String.javaClass)) {
            return dataJson as T
        }
        return when {
            GsonUtil.isJson(dataJson, GsonUtil.REGEX_JSON_ARRAY) -> {
                val list: MutableList<T?> = mutableListOf()
                val listArrs = GsonUtil.trans2Bean<List<Any?>>(dataJson,
                        object : TypeToken<List<Any?>>() {}.type)
                listArrs?.forEach {
                    list.add(GsonUtil.trans2Bean(GsonUtil.trans2Str(it), beanClass))
                }
                list as T
            }
            GsonUtil.isJson(dataJson, GsonUtil.REGEX_JSON_ARRAY_OBJ) -> {
                GsonUtil.trans2Bean(dataJson, beanClass)
            }
            else -> dataJson as T
        }
    }

    private fun handleResultData(dataJson: String): T? {
        if (Utils.isEmpty(dataJson)
                || "[]" == dataJson.trim()
                || "{}" == dataJson.trim()) {
            return null
        }
        /*val returnType = this.javaClass.genericSuperclass as ParameterizedType
        val dateType = Utils.getParameterUpperBound(0, returnType)
        return GsonUtil.trans2Bean(dataJson, dateType)*/

        val method = this.javaClass.methods.first { it.name.contains("onSuccess") }
        val tv = method.genericReturnType as TypeVariable<*>
        val genericDeclaration = tv.genericDeclaration
        //val dateType = Utils.getParameterUpperBound(0, genericDeclaration)
        //GsonUtil.trans2Bean(dataJson, dateType)
        return null

    }


    /**
     * 成功回调
     * @param data 数据结果
     */
    abstract fun onSuccess(data: T?)

    /**
     * 失败回调
     * @param errCode 错误码
     * @param errMsg 错误信息
     * @param throwable 错误
     */
    abstract fun onError(errCode: String, errMsg: String, throwable: Throwable?)

    /**
     * 处理返回的code
     * @param datacode 响应体中定义的编码，非Http的code
     */
    protected fun handleHttpDataCode(datacode: String): Boolean {
        return true
    }
}