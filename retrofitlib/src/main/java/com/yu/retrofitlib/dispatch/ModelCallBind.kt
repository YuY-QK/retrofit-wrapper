package com.yu.retrofitlib.dispatch

import com.yu.retrofitlib.module.IModel
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by yu on 2020/6/25.
 * 模型绑定
 */
class ModelCallBind<T>(private var call: Call<T>) : ICallBind<T> {

    private var isBind: Boolean = false

    override fun bind(model: IModel): ICallBind<T> {
        model.addCall(this)
        isBind = true
        return this
    }

    private fun checkBind() {
        if (!isBind) {
            throw IllegalArgumentException("u must call bind  before call execute or enqueue")
        }
    }

    override fun enqueue(callback: Callback<T>) {
        checkBind()
        call.enqueue(callback)
    }

    override fun isExecuted(): Boolean {
        return call.isExecuted
    }

    override fun timeout(): Timeout {
        return call.timeout()
    }

    override fun clone(): Call<T> {
        return ModelCallBind(call.clone())
    }

    override fun isCanceled(): Boolean {
        return call.isCanceled
    }

    override fun cancel() {
        call.cancel()
    }

    override fun execute(): Response<T> {
        return call.execute()
    }

    override fun request(): Request {
        return call.request()
    }


}