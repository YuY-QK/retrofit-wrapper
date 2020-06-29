package com.yu.retrofitlib.callback

/**
 * Created by yu on 2020/6/25.
 * 回调基础接口
 */
interface OnResponseCallback<T> {

    /**
     * 成功回调
     * @param data 结果数据
     */
    fun onSuccess(data: T?)

    /**
     * 失败回调
     * @param errCode 错误码
     * @param errMsg 错误信息
     * @param ex 错误异常
     */
    fun onFailure(errCode: String, errMsg: String, ex: Throwable?)
}