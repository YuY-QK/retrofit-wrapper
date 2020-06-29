package com.yu.retrofitlib.util

import android.os.Handler
import java.util.concurrent.*

/**
 * Created by yu on 2020/6/28.
 * 任务
 */
class Tasks private constructor(){

    companion object {
        val INSTANCE: Tasks = Tasks()
    }

    private var scheduledExecutorService: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private var mHandler: Handler = Handler()
    private var mScheduleMap: MutableMap<String, ScheduledFuture<*>> = mutableMapOf()

    /**
     * 开启任务
     * @param taskId 任务id，标记一个任务，方便定位
     * @param taskListener 任务监听
     * @param <T>
     */
    fun <T> start(taskId: String, taskListener: OnTaskListener<T>?) {
        taskListener?.let {
            it.onPre(taskId)
            val callable = Callable {
                val data: T = it.onDoing(taskId)
                mHandler.post { it.onComplete(data) }
                data
            }
            mScheduleMap[taskId] = scheduledExecutorService.schedule(callable, 0, TimeUnit.SECONDS)
        }
    }

    /**
     * 取消指定任务
     * @param taskId 任务id
     */
    fun cancel(taskId: String?) {
        val scheduledFuture = mScheduleMap[taskId] ?: return
        scheduledFuture.cancel(true)
    }

    interface OnTaskListener<T> {
        /**
         * 任务准备
         * @param taskId 任务id
         */
        fun onPre(taskId: String)

        /**
         * 任务执行中
         * @param taskId 任务id
         * @return 任务结果
         * @throws => Exception
         */
        @Throws(Exception::class)
        fun onDoing(taskId: String): T

        /**
         * 任务完成
         * @param data 结果
         */
        fun onComplete(data: T)
    }

}