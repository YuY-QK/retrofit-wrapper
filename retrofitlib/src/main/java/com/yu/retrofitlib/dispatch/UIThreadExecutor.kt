package com.yu.retrofitlib.dispatch

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

/**
 * Created by yu on 2020/06/25.
 * 主线程调用器
 * 将请求的结果，转到主线程中
 */
class UIThreadExecutor : Executor {
    private val handler = Handler(Looper.getMainLooper())
    override fun execute(r: Runnable) {
        handler.post(r)
    }
}