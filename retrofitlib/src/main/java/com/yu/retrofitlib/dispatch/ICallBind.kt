package com.yu.retrofitlib.dispatch

import com.yu.retrofitlib.module.IModel
import retrofit2.Call

/**
 * Created by yu on 2020/6/25.
 * Http调用接口
 */
interface ICallBind<T> : Call<T> {

    /**
     * 绑定模型
     */
    fun bind(model: IModel): ICallBind<T>

}