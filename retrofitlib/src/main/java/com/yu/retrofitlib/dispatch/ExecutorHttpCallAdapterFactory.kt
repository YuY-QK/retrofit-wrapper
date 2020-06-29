package com.yu.retrofitlib.dispatch

import com.yu.retrofitlib.module.IModel
import okhttp3.Request
import okio.Timeout
import retrofit2.*
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.Executor

/**
 * Created by yu on 2020/6/26.
 * 请求响应转换器
 */
class ExecutorHttpCallAdapterFactory(
        val callbackExecutor: Executor = UIThreadExecutor())
    : CallAdapter.Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (getRawType(returnType) != ICallBind::class.java) {
            return null
        }
        val responseType = getCallResponseType(returnType)
        return object : CallAdapter<Any, Call<*>> {
            override fun responseType(): Type {
                return responseType
            }

            override fun adapt(call: Call<Any>): Call<Any> {
                return ExecutorCallbackCall(callbackExecutor, ModelCallBind(call))
            }
        }
    }

    private fun getCallResponseType(returnType: Type): Type{
        require(returnType is ParameterizedType) { "Call return type must be parameterized as Call<Foo> or Call<? extends Foo>" }
        return getParameterUpperBound(0, returnType)
    }

    class ExecutorCallbackCall<T>(var callbackExecutor: Executor, var delegate: Call<T>)
        : ICallBind<T> {
        override fun bind(model: IModel): ICallBind<T> {
            require(delegate is ICallBind<*>) { "Call must be instanceof HttpCall" }
            (delegate as ICallBind<*>).bind(model)
            return this@ExecutorCallbackCall
        }

        override fun enqueue(callback: Callback<T>) {
            delegate.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    callbackExecutor.execute {
                        if (delegate.isCanceled) {
                            // Emulate OkHttp's behavior of throwing/delivering an IOException on cancellation.
                            callback.onFailure(this@ExecutorCallbackCall, IOException("Canceled"))
                        } else {
                            callback.onResponse(this@ExecutorCallbackCall, response)
                        }
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    callbackExecutor.execute { callback.onFailure(this@ExecutorCallbackCall, t) }
                }
            })
        }

        override fun isExecuted(): Boolean {
            return delegate.isExecuted
        }

        override fun timeout(): Timeout {
            return delegate.timeout()
        }

        override fun clone(): Call<T> {
            return ExecutorCallbackCall(callbackExecutor, delegate.clone())
        }

        override fun isCanceled(): Boolean {
            return delegate.isCanceled
        }

        override fun cancel() {
            delegate.cancel()
        }

        override fun execute(): Response<T> {
            return delegate.execute()
        }

        override fun request(): Request {
            return delegate.request()
        }

    }
}