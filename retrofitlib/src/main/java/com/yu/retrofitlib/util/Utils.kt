package com.yu.retrofitlib.util

import android.text.TextUtils
import com.yu.retrofitlib.NetManager
import com.yu.retrofitlib.config.RequestParams
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.lang.reflect.GenericDeclaration
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.util.ArrayList
import java.util.HashMap
import java.util.regex.Pattern

/**
 * Created by yu on 2020/6/26.
 * 工具类
 */
object Utils {

    /**
     * 判断是否为空
     *
     * @param str 需判断的字符串
     * @return true：为空，false：不为空
     */
    @JvmStatic
    fun isEmpty(str: String?): Boolean {
        if (str == null || str.trim { it <= ' ' }.isEmpty() || "null" == str) {
            return true
        }
        for (element in str) {
            if (element != ' ' && element != '\t' && element != '\r' && element != '\n') {
                return false
            }
        }
        return true
    }

    /**
     * 判断正浮点数
     *
     * @param str 需判断的字符串
     * @return true：正浮点数，false：非正浮点数
     */
    @JvmStatic
    fun isNumericFloatingPoint(str: String?): Boolean {
        return !isEmpty(str) && Pattern.matches("^[1-9]\\d*\\.\\d*|0\\.\\d*[0-9]\\d*$", str)
    }

    /**
     * 获取泛型参数上限实参
     * Extract the upper bound of the generic parameter at `index` from `type`. For
     * example, index 1 of `Map<String, ? extends Runnable>` returns `Runnable`.
     * @param index 索引
     * @param type 参数类型
     * @return 类型
     */
    @JvmStatic
    fun getParameterUpperBound(index: Int, type: ParameterizedType): Type {
        val types = type.actualTypeArguments
        require(!(index < 0 || index >= types.size)) { "Index " + index + " not in range [0," + types.size + ") for " + type }

        val paramType = types[index]

        return if (paramType is WildcardType) {
            paramType.upperBounds[0]
        } else {
            paramType
        }
    }

    /**
     * 处理请求地址
     * @param url
     * @return
     */
    @JvmStatic
    fun handleRequestUrl(url: String): String {
        require(!TextUtils.isEmpty(url)) { "必须设置请求地址" }
        return if (url.startsWith("http") || url.startsWith("https")) {
            url
        } else {
            NetManager.Config.BASE_URL + url
        }
    }

    /**
     * 处理上传请求参数
     * @param requestParams 参数map
     * @return 参数体
     */
    fun handleUploadParams(requestParams: RequestParams?): Map<String, RequestBody> {
        val map: MutableMap<String, RequestBody> = HashMap()
        if (requestParams == null) {
            return map
        }
        val params: Map<String, @JvmSuppressWildcards Any> = requestParams.getBodyParams()
        if (params.isEmpty()) {
            return map
        }
        for ((key, value) in params) {
            map[key] = RequestBody.create(null, value.toString())
        }
        return map
    }

    /**
     * 处理上传请求文件参数
     * @param files 上传文件
     * @return 请求文件体
     */
    fun handleUploadParts(files: List<File>): List<MultipartBody.Part> {
        val uploadList = ArrayList<MultipartBody.Part>()
        for (i in files.indices) {
            uploadList.add(getUploadParts(files[i], i.toString()))
        }
        return uploadList
    }

    /**
     * 处理上传请求文件参数
     * @param file 上传文件
     * @return 请求文件体
     */
    private fun getUploadParts(file: File, position: String): MultipartBody.Part {
        // 根据类型及File对象创建RequestBody
        val requestBody = RequestBody.create(
                MediaType.parse("application/json;charset=UTF-8"), file)
        // 将RequestBody封装成MultipartBody.Part类型
        return MultipartBody.Part.createFormData(NetManager.Config.KEY_UPFILE, file.name, requestBody)
    }
}