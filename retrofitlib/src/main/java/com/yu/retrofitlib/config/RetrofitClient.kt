package com.yu.retrofitlib.config

import com.yu.retrofitlib.NetManager
import com.yu.retrofitlib.dispatch.ExecutorHttpCallAdapterFactory
import com.yu.retrofitlib.gson.GsonUtil
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by yu on 2020/6/25.
 * Retrofit
 */
class RetrofitClient {

    companion object {
        var INSTANCE: RetrofitClient = RetrofitClient()
    }

    var retrofit: Retrofit
        private set
    init {
        retrofit = Retrofit.Builder()
                .client(OkHttpFactory.okHttpClient)
                .baseUrl(NetManager.Config.BASE_URL)
                .addCallAdapterFactory(ExecutorHttpCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create(GsonUtil.factory()))
                .build()
    }

    public fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

}