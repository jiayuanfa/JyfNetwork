package com.example.http;

import java.io.InputStream;

/**
 * 请求接口的回调 给框架层调用的接口
 * @param <T>
 */
public interface CallBackListener<T> {
    // 请求成功
    void onSuccess(InputStream inputStream);

    // 请求失败
    void onFailed();
}
