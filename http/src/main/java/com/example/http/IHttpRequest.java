package com.example.http;

/**
 * 请求对象的顶层接口
 * 封装我们的请求对象
 */
public interface IHttpRequest {
    // 设置Url
    void setUrl(String url);

    // 设置请求参数
    void setData(byte[] data);

    // 设置回调接口 两个回调接口来会调用，实现服务器返回的数据流直接解析为程序员需要的数据类型
    void setLisener(CallBackListener callBackListener);

    // 执行请求的方法
    void execute();
}
