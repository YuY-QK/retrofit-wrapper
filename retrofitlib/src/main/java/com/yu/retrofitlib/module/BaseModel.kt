package com.yu.retrofitlib.module

import retrofit2.Call

/**
 * Created by yu on 2020/6/25.
 * 基础模型
 */
open class BaseModel: IModel {

    private var isCanceled = false
    private var callList: MutableList<Call<*>> = arrayListOf()

    override fun addCall(call: Call<*>) {
        callList.add(call)
    }

    override fun cancelRequest() {
        for (call in callList) {
            if (!call.isCanceled) {
                call.cancel()
            }
        }
        callList.clear()
        isCanceled = true
    }

    override fun isAllCanceled(): Boolean = isCanceled
}