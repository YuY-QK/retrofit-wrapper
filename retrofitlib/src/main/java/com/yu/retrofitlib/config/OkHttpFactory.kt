package com.yu.retrofitlib.config

import com.yu.retrofitlib.NetManager
import com.yu.retrofitlib.interceptor.InterceptorManager
import com.yu.retrofitlib.util.HttpUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * Created by yu on 2020/6/25.
 *  okhttp client
 */
object OkHttpFactory {

    private const val TIMEOUT_READ = 30
    private const val TIMEOUT_WRITE = 30
    private const val TIMEOUT_CONNECTION = 30

    var okHttpClient: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(InterceptorManager.requestInterceptor)
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        //测试环境的输出完整日志，非测试环境过滤代理，不输出日志
        if (NetManager.Config.DEBUG) {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            builder.proxy(Proxy.NO_PROXY)
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        builder.addInterceptor(httpLoggingInterceptor)
        builder.readTimeout(TIMEOUT_READ.toLong(), TimeUnit.SECONDS)
        builder.writeTimeout(TIMEOUT_WRITE.toLong(), TimeUnit.SECONDS)
        builder.connectTimeout(TIMEOUT_CONNECTION.toLong(), TimeUnit.SECONDS)
        //https的证书创建，这里是信任所有的证书  如果要使用文件证书的话是不是要放到主项目里了呢？
        // assets目录，Application.getAssets().open(证书名)
        val sslParams: HttpUtil.SSLParams = HttpUtil.getSslSocketFactory()
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
        okHttpClient = builder.build()
    }

}