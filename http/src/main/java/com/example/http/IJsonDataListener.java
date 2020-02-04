package com.example.http;

/**
 * 请求结果的回到接口
 */
public interface IJsonDataListener<T> {
    // 请求成功
    void onSuccess(T t);
    // 请求失败
    void onFailed();
}
