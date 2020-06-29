package com.yu.retrofitlib.config

import com.yu.retrofitlib.NetManager

/**
 * Created by yu on 2020/6/25.
 */
object HttpCode {
    /**
     * 成功
     */
    var OK = NetManager.Label.DATA_CODE_OK

    /**
     * 网络连接异常
     */
    const val NETWORK_CONNECT_ERROR = "-1"

    /**
     * 服务器异常
     */
    const val HTTP_SERVER_ERROR = "-2"

    /**
     * json解析异常
     */
    const val JSON_SYNTAX_EXCEPTION = "-3"

    /**
     * 网络连接超时
     */
    const val SOCKE_TTIME_OUT_EXCEPTION = "-4"

}