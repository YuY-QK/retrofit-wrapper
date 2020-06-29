package com.yu.retrofitlib.module

import com.yu.retrofitlib.callback.OnNetCallback
import com.yu.retrofitlib.callback.OnResponseCallback
import com.yu.retrofitlib.callback.down.DownloadResponseCallback
import com.yu.retrofitlib.callback.down.OnDownloadCallback
import com.yu.retrofitlib.config.RequestParams
import com.yu.retrofitlib.config.RetrofitClient
import com.yu.retrofitlib.config.ServerApi
import com.yu.retrofitlib.dispatch.ICallBind
import com.yu.retrofitlib.util.Utils
import okhttp3.ResponseBody
import java.io.File

/**
 * Created by yu on 2020/6/25.
 * 请求模型
 */
class RequestModel : BaseModel() {

    /**
     * Get方式请求数据
     * @param url      请求地址
     * @param callback 回调
     * @return
     */
    fun <T> onGet(url: String,
                  beanClazz: Class<*>?,
                  params: RequestParams?,
                  callback: OnResponseCallback<T>?) {
        val httpCall: ICallBind<ResponseBody> = if (params == null) {
            RetrofitClient.INSTANCE.retrofit
                    .create(ServerApi::class.java)
                    .onGet(Utils.handleRequestUrl(url))
        } else {
            RetrofitClient.INSTANCE.retrofit
                    .create(ServerApi::class.java)
                    .onGet(Utils.handleRequestUrl(url), params.getHeaderParams())
        }

        httpCall.bind(this).enqueue(object : OnNetCallback<T>(beanClazz) {
            override fun onSuccess(data: T?) {
                callback?.onSuccess(data)
            }

            override fun onError(errCode: String, errMsg: String, throwable: Throwable?) {
                callback?.onFailure(errCode, errMsg, throwable)
            }
        })
    }

    /**
     * Post请求
     *
     * @param url      请求地址
     * @param params   请求参数，可null
     * @param callback 回调
     * @param <T> 类型
     */
    fun <T> onPost(url: String,
                   beanClazz: Class<*>?,
                   params: RequestParams?,
                   callback: OnResponseCallback<T>?) {
        val httpCall: ICallBind<ResponseBody> = if (params != null) {
            val api = RetrofitClient.INSTANCE.retrofit.create(ServerApi::class.java)

            params.getBodyBean()?.let {
                api.onPost(Utils.handleRequestUrl(url), it)
            } ?:
            api.onPost(Utils.handleRequestUrl(url), params.getBodyParams())
        } else {
            RetrofitClient.INSTANCE.retrofit
                    .create(ServerApi::class.java)
                    .onPost(Utils.handleRequestUrl(url))
        }
        httpCall.bind(this).enqueue(object : OnNetCallback<T>(beanClazz) {
            override fun onSuccess(data: T?) {
                callback?.onSuccess(data)
            }

            override fun onError(errCode: String, errMsg: String, throwable: Throwable?) {
                callback?.onFailure(errCode, errMsg, throwable)
            }
        })
    }

    /**
     * 上传文件
     *
     * @param url      上传地址
     * @param files    上传文件
     * @param callback 回调
     */
    fun <T> onUpload(url: String,
                     beanClazz: Class<*>?,
                     params: RequestParams?,
                     files: List<File>?,
                     callback: OnResponseCallback<T>?) {
        if (files == null || files.isEmpty()) {
            callback?.onFailure("", "no files", Exception("no files"))
            return
        }
        RetrofitClient.INSTANCE
                .create(ServerApi::class.java)
                .onUpload(Utils.handleRequestUrl(url),
                        Utils.handleUploadParams(params),
                        Utils.handleUploadParts(files))
                .bind(this)
                .enqueue(object : OnNetCallback<T>(beanClazz) {

                    override fun onSuccess(data: T?) {
                        callback?.onSuccess(data)
                    }

                    override fun onError(errCode: String, errMsg: String, throwable: Throwable?) {
                        callback?.onFailure(errCode, errMsg, throwable)
                    }
                })
    }

    /**
     * 下载文件
     *
     * @param url      下载地址
     * @param callback 回调
     */
    fun onDownload(url: String,
                   filePath: String, fileName: String,
                   callback: DownloadResponseCallback?) {
        if (callback != null && callback.onHasDownloaded(filePath, fileName)) {
            return
        }
        RetrofitClient.INSTANCE.create(ServerApi::class.java)
                .onDownload(Utils.handleRequestUrl(url))
                .bind(this)
                .enqueue(object : OnDownloadCallback(filePath, fileName) {
                    override fun onResponseByUI(httpCode: String, file: File?, httpResponseMsg: String) {
                        callback?.onResponseByUI(httpCode, file, httpResponseMsg)
                    }

                    override fun onResponseProgress(progress: Int, total: Long) {
                        callback?.onResponseProgress(progress, total)
                    }

                    override fun onSuccess(data: File?) {
                        callback?.onSuccess(data)
                    }

                    override fun onError(errCode: String, errMsg: String, throwable: Throwable?) {
                        callback?.onFailure(errCode, errMsg, throwable)
                    }
                })
    }
}