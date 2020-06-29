package com.yu.retrofitlib.callback.down

import com.yu.retrofitlib.callback.OnResponseCallback
import java.io.File

/**
 * Created by yu on 2020/6/25.
 * 响应回调<br/>
 * 请求下载数据等回调
 */
interface DownloadResponseCallback: OnResponseCallback<File> {

    /**
     * 检查已经下载<br/>
     * 1、是否文件已经下载<br/>
     * 2、是否有文件读写权限
     *
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @return 是否已经下载过了：<br/>
     * true：下载过了，
     * false：未下载过
     */
    fun onHasDownloaded(filePath: String, fileName: String): Boolean

    /**
     * 下载成功<br/>
     * UI（主）线程中<br/>
     *
     * @param httpCode 响应码，和响应体无关系
     * @param file 下载文件
     * @param httpresponseMsg 响应消息，和响应体无关系
     */
    fun onResponseByUI(httpCode: String, file: File?, httpResponseMsg: String)

    /**
     * 下载文件的进度
     * @param progress 进度，百分比
     * @param total 文件总大小，字节
     */
    fun onResponseProgress(progress: Int, total: Long)

}
