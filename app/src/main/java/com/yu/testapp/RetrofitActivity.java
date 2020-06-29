package com.yu.testapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yu.retrofitlib.NetManager;
import com.yu.retrofitlib.callback.OnResponseCallback;
import com.yu.testapp.bean.NewsBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by yu on 2020/6/25.
 */
public class RetrofitActivity extends AppCompatActivity {

    private static final String TAG = "RetrofitActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);
        NetManager.Config.BASE_URL = "https://api.apiopen.top/";

        NetManager.Config.responseBodyLabel(
            "code",
            "message",
            "result",
            "200");
    }

    /**
     * 请求
     *
     * @param view
     */
    public void requestClick(View view) {
        NetManager.build(RetrofitActivity.class)
            .url("getWangYiNews")
            .beanClass(NewsBean.class)
            .onGet(new OnResponseCallback<List<NewsBean>>() {
                @Override
                public void onSuccess(@Nullable List<NewsBean> data) {
                    if (data == null || data.isEmpty()) {
                        Log.e(TAG, "暂无数据");
                        return;
                    }
                    for (NewsBean c : data) {
                        Log.e(TAG, "title::" + c.getTitle());
                    }
                }

                @Override
                public void onFailure(@NotNull String errCode, @NotNull String errMsg, @org.jetbrains.annotations.Nullable Throwable ex) {
                    Log.e(TAG, "error===>" +
                        "code:" + errCode + "\n" +
                        "msg:" + errMsg);
                }
            });
    }
}
