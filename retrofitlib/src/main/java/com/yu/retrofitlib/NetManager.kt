package com.yu.retrofitlib

import android.text.TextUtils
import com.yu.retrofitlib.callback.OnResponseCallback
import com.yu.retrofitlib.callback.down.DownloadResponseCallback
import com.yu.retrofitlib.config.RequestParams
import com.yu.retrofitlib.module.RequestModel
import com.yu.retrofitlib.util.Utils
import java.io.File
import java.util.*

/**
 * Created by yu on 2019-09-09.
 * Updated by yu on 2020/6/19.
 * 请求管理器
 */
class NetManager private constructor(callClazz: Class<*>, tag: String = "") {

    /**
     * 配置
     */
    object Config {

        /**
         * 是否debug
         */
        @JvmField
        var DEBUG = true

        /**
         * 请求地址
         */
        @JvmField
        var BASE_URL = ""

        @JvmStatic
        fun responseBodyLabel(
                codeLabel: String = "code",
                msgLabel: String = "msg",
                dataLabel: String = "data",
                dataCodeOK: String = "200") {
            Label.DATA_NAME_CODE = codeLabel
            Label.DATA_NAME_MESSAGE = msgLabel
            Label.DATA_NAME_BODY = dataLabel
            Label.DATA_CODE_OK = dataCodeOK
        }

        //和服务器约定的上传文件的名称
        @JvmField
        var KEY_UPFILE = "file"

        @JvmField
        var noNetworkTips = "您的网络不太给力"
    }

    /**
     * 请求结果的标签配置
     */
    internal object Label {
        var DATA_NAME_CODE = "_code"

        var DATA_NAME_MESSAGE = "_msg"

        var DATA_NAME_BODY = "_data"

        var DATA_CODE_OK = "200"

    }

    companion object {

        private val modelMap: MutableMap<String, RequestModel> = mutableMapOf()

        /**
         * 构建请求管理器
         * @param callClazz 当前调用者的类
         * @return 构建请求管理器
         */
        @JvmStatic
        fun build(callClazz: Class<*>): NetManager {
            return NetManager(callClazz)
        }

        /**
         * 构建请求管理器
         * @param callClazz 当前调用者的类
         * @return 构建请求管理器
         */
        @JvmStatic
        fun build(callClazz: Class<@JvmSuppressWildcards Any>, tag: String): NetManager {
            return NetManager(callClazz, tag)
        }


        /**
         * 取消请求
         * @param callClazz 当前调用者的类
         */
        @JvmStatic
        fun cancelRequest(callClazz: Class<@JvmSuppressWildcards Any>) {
            cancelRequest(callClazz, "")
        }

        /**
         * 取消请求
         * @param callClazz 当前调用者的类
         */
        @JvmStatic
        fun cancelRequest(callClazz: Class<@JvmSuppressWildcards Any>?, tag: String) {
            val requestModel: RequestModel? = modelMap.remove(getTagKey(callClazz, tag))
            requestModel?.cancelRequest()
        }

        /**
         * 获取tagkey
         * @param callClazz 类
         * @param tag 标记
         * @return
         */
        private fun getTagKey(callClazz: Class<*>?, tag: String): String {
            return if (callClazz != null && !Utils.isEmpty(tag)) {
                callClazz.name + "_" + tag
            } else if (callClazz != null) {
                callClazz.name
            } else if (!Utils.isEmpty(tag)) {
                tag
            } else {
                ""
            }
        }
    }


    private var mModel: RequestModel? = null


    init {
        val tagKey: String = getTagKey(callClazz, tag)
        mModel = modelMap[tagKey]
        if (mModel == null) {
            mModel = RequestModel()
            modelMap[tagKey] = mModel!!
        }
    }

    /**
     * 设置返回响应码名称
     * @param codeKey 返回响应码key
     * @return 构建请求管理器
     */
    fun codeKey(codeKey: String): NetManager {
        Label.DATA_NAME_CODE = (if (!TextUtils.isEmpty(codeKey)) codeKey else Label.DATA_NAME_CODE)
        return this
    }

    /**
     * 设置返回消息名称
     * @param messageKey 返回消息key
     * @return 构建请求管理器
     */
    fun messageKey(messageKey: String): NetManager {
        Label.DATA_NAME_MESSAGE = (if (!TextUtils.isEmpty(messageKey)) messageKey else Label.DATA_NAME_MESSAGE)
        return this
    }

    /**
     * 设置返回数据名称
     * @param dataKey 返回数据key
     * @return 构建请求管理器
     */
    fun dataKey(dataKey: String): NetManager {
        Label.DATA_NAME_BODY = (if (!TextUtils.isEmpty(dataKey)) dataKey else Label.DATA_NAME_BODY)
        return this
    }


    private var beanClass: Class<*>? = null
    private var url: String = ""
    private var params: RequestParams? = null
    private var filePath: String? = null
    private var fileName: String? = null
    private var file: File? = null
    private var files: MutableList<File>? = null

    /**
     * 设置解析类
     * 是否为list都只设置单独的类
     */
    fun beanClass(beanClass: Class<*>?): NetManager {
        this.beanClass = beanClass
        return this
    }

    /**
     * 设置接口地址
     */
    fun url(url: String): NetManager {
        this.url = url
        return this
    }

    /**
     * 设置参数
     */
    fun params(params: RequestParams?): NetManager {
        this.params = params
        return this
    }

    /**
     * 设置文件上传相关
     */
    fun file(fileName: String?, filePath: String?, vararg file: File): NetManager {
        this.fileName = fileName
        this.filePath = filePath
        if (file.isNotEmpty() && file.size==1) {
            this.file = file[0]
        } else if (file.isNotEmpty()) {
            this.files = mutableListOf()
            file.forEach { files?.add(it) }
        }
        return this
    }

    /**
     * Get请求
     * @param url 请求地址
     * @param callback 回调
     */
    fun <T> onGet(callback: OnResponseCallback<T>) {
        mModel?.onGet(url, beanClass, params, callback)
    }

    /**
     * Post请求
     * @param url 请求地址
     * @param params 请求参数，可null
     * @param callback 回调
     */
    fun <T> onPost(callback: OnResponseCallback<T>) {
        mModel?.onPost(url, beanClass, params, callback)
    }

    /**
     * 上传文件
     * @param url 上传地址
     * @param file 上传文件
     * @param callback 回调
     */
    fun <T> onUpload(callback: OnResponseCallback<T>) {
        mModel?.onUpload(url, beanClass, params, files, callback)
    }

    /**
     * 上传文件
     * @param url 上传地址
     * @param files 上传文件
     * @param callback 回调
     */
    fun <T> onUploadList(callback: OnResponseCallback<List<T>?>?) {
        mModel?.onUpload(url, beanClass, params, files, callback)
    }

    /**
     * 下载文件
     * @param url 下载地址
     * @param callback 回调
     */
    fun onDownload(callback: DownloadResponseCallback?) {
        mModel?.onDownload(url, filePath?:"", fileName?:"", callback)
    }

}