package com.yu.retrofitlib.callback.down

import android.accounts.NetworkErrorException
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.orhanobut.logger.Logger
import com.yu.retrofitlib.NetManager
import com.yu.retrofitlib.callback.OnNetCallback
import com.yu.retrofitlib.config.HttpCode
import com.yu.retrofitlib.util.Tasks
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * Created by yu on 2020-02-16.
 */
abstract class OnDownloadCallback(private val filePath: String, private val fileName: String) : OnNetCallback<File>(null) {

    private val WHAT_FINISH = 10 //文件下载完成
    private val WHAT_PROGRESS = 20 //文件下载进度
    private val mHandler: Handler

    /**
     * 下载成功<br/>
     * UI（主）线程中<br></br>
     *
     * @param httpCode 响应码，和响应体无关系
     * @param file 下载文件
     * @param httpresponseMsg 响应消息，和响应体无关系
     */
    abstract fun onResponseByUI(httpCode: String, file: File?, httpResponseMsg: String)

    /**
     * 下载文件的进度
     * @param progress 进度，百分比
     * @param total 文件总大小，字节
     */
    abstract fun onResponseProgress(progress: Int, total: Long)

    @Throws(Exception::class)
    override fun onResponseSuccess(response: Response<ResponseBody>) {
        if (!onDownWithSelf(response, filePath, fileName)) {   //文件下载处理
            handleDownResponse(response)
        }
    }

    /**
     * 是否需要自己处理[工作线程中]<br></br>
     * 若需要自己处理，有UI方面的操作，需要将结果发送到UI线程。<br></br>
     * 此方法执行在okhttp#onResonse()的方法中，需要自己实现onFinish()方法和onProgress()方法
     * @param response 最原始的响应体
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @return 默认为false<br></br>
     * true：自己处理<br></br>
     * false：默认处理
     */
    protected fun onDownWithSelf(
            response: Response<*>,
            filePath: String,
            fileName: String): Boolean {
        return false
    }

    /**
     * 处理下载文件的响应体
     * @param response 响应消息体
     * @throws IOException IO异常错误
     */
    @Throws(Exception::class)
    private fun handleDownResponse(response: Response<ResponseBody>) {
        Logger.e("down===>start")
        val body = response.body()
        val code = response.code().toString()
        val message = response.message()
        if (body?.byteStream() == null) {
            onError(HttpCode.HTTP_SERVER_ERROR, NetManager.Config.noNetworkTips,
                    NetworkErrorException("服务器异常：code=$code, msg=$message"))
            return
        }
        Tasks.INSTANCE.start("download", object : Tasks.OnTaskListener<Void?> {
            override fun onPre(taskId: String) {}

            @Throws(Exception::class)
            override fun onDoing(taskId: String): Void? {
                handleDownloadFile(response, code, message)
                return null
            }

            override fun onComplete(data: Void?) {

            }
        })
    }

    @Throws(Exception::class)
    private fun handleDownloadFile(response: Response<ResponseBody>,
                                   code: String, message: String) {
        val iStream = response.body()!!.byteStream()
        val buf = ByteArray(2048)
        var len: Int
        var fos: FileOutputStream? = null
        // 下载文件的目录
        try {
            val savePath = getSaveDir(filePath)
            val total = response.body()?.contentLength()?:0
            val file = File(savePath, fileName)
            fos = FileOutputStream(file)
            var totalSize = 0
            while (iStream.read(buf).also { len = it } != -1) {
                fos.write(buf, 0, len)
                totalSize += len
                sendMessage(WHAT_PROGRESS,
                        total,
                        (totalSize * 1.0f / total * 100).toInt())
            }
            fos.flush()
            val sMap: MutableMap<String, Any> = HashMap()
            sMap["code"] = code
            sMap["message"] = message
            sMap["file"] = file
            sendMessage(WHAT_FINISH, sMap, 0)
            onSuccess(file)
            Logger.e("down===>onSuccess")
        } catch (e: Exception) {
            Logger.e("error===>" + e.message)
            e.printStackTrace()
            onError(
                    HttpCode.HTTP_SERVER_ERROR,
                    NetManager.Config.noNetworkTips,
                    Exception("文件读写异常：${e.message}"))
        } finally {
            iStream.close()
            fos?.close()
        }
    }

    /**
     * 判断下载目录是否存在
     * @param saveDir 存储目录
     * @return 存储目录
     * @throws IOException IO异常错误
     */
    @Throws(IOException::class)
    private fun getSaveDir(saveDir: String): String {
        val downloadFile = File(saveDir)
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile()
        }
        return downloadFile.absolutePath
    }

    /**
     * 发送消息
     * @param what 消息类型
     * @param obj 数据
     * @param arg1 参数1
     */
    private fun sendMessage(what: Int, obj: Any, arg1: Int) {
        val msg = Message.obtain()
        msg.what = what
        msg.obj = obj
        msg.arg1 = arg1
        mHandler.sendMessage(msg)
    }

    /**
     * 处理消息
     * @param msg 消息
     */
    private fun handleResponseMessage(msg: Message) {
        when (msg.what) {
            WHAT_FINISH -> {
                mHandler.removeCallbacks(null)
                val fileMap = msg.obj as Map<String, Any>
                val code = fileMap["code"].toString()
                val message = fileMap["message"].toString()
                val file = fileMap["file"] as File?
                onResponseByUI(code, file, message)
            }
            WHAT_PROGRESS -> onResponseProgress(msg.arg1, msg.obj as Long) // 下载中
        }
    }

    init {
        mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                handleResponseMessage(msg)
            }
        }
    }
}