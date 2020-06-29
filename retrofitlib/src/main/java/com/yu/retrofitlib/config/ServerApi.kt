package com.yu.retrofitlib.config

import com.yu.retrofitlib.dispatch.ICallBind
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by yu on 2020/6/25.
 * 服务接口
 */
interface ServerApi {

    /**========================= GET ============================**/

    /**
     * Get方式请求数据
     * @param url
     * @return
     */
    @GET
    fun onGet(@Url url: String)
            : ICallBind<ResponseBody>

    /**
     * Get方式请求数据
     * @param url
     * @param headerMap
     * @return
     */
    @GET
    fun onGet(@Url url: String,
              @HeaderMap headerMap: Map<String, @JvmSuppressWildcards Any>)
            : ICallBind<ResponseBody>


    /**========================= POST ============================**/

    /**
     * 请求数据
     * @param url
     * @return
     */
    @POST
    fun onPost(@Url url: String)
            : ICallBind<ResponseBody>

    /**
     * 请求数据
     * @param url
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST
    fun onPost(@Url url: String,
               @FieldMap params: Map<String, @JvmSuppressWildcards Any>)
            : ICallBind<ResponseBody>

    /**
     * 请求数据
     * @param url
     * @param params
     * @return
     */
    @POST
    fun onPost(@Url url: String,
               @Body params: Any)
            : ICallBind<ResponseBody>


    /**========================= UP & DOWN ============================**/

    /**
     * 上传
     * @param url
     * @param map
     * @param part
     * @return
     */
    @Multipart
    @POST
    fun onUpload(@Url url: String,
                 @PartMap map: Map<String, RequestBody>,
                 @Part part: List<MultipartBody.Part>)
            : ICallBind<ResponseBody>

    /**
     * 下载
     * @param url
     * @return
     */
    @Streaming
    @GET
    fun onDownload(@Url url: String)
            : ICallBind<ResponseBody>
}
