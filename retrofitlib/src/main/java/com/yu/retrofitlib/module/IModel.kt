package com.yu.retrofitlib.module

import retrofit2.Call

/**
 * Created by yu on 2020/6/25.
 * Call模型
 */
interface IModel {

    /**
     * 添加调用
     */
    fun addCall(call: Call<*>)

    /**
     * 取消请求
     */
    fun cancelRequest()

    /**
     * 是否全部都取消了
     */
    fun isAllCanceled(): Boolean
}