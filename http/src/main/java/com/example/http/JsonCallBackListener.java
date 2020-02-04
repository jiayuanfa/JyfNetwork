package com.example.http;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 内部接口的实现类 将流转为用户想要的类型
 * 实现接口，则接口调用的时候，持有的实例就直接是接口的实现类
 */
public class JsonCallBackListener<T> implements CallBackListener{

    // 知道用户需要的请求结果的类型
    private Class<T> response;

    // 外部的回调接口类
    private IJsonDataListener iJsonDataListener;

    // 切换线程的Handler
    private Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 构造方法
     * 在创建的时候就把需要的参数传递进来
     * @param response
     * @param iJsonDataListener
     */
    public JsonCallBackListener(Class<T> response, IJsonDataListener iJsonDataListener) {
        this.response = response;
        this.iJsonDataListener = iJsonDataListener;
    }

    @Override
    public void onSuccess(InputStream inputStream) {
        // 解析结束 回调给外部的回调接口
        // 使用阿里巴巴的fastjson来解析即可
        // 第一步 将流转化为json字符串
        String content = getContent(inputStream);
        // 第二步 使用阿里巴巴的库将JSON字符串转化为程序员想要的对象类型
        final T t = JSON.parseObject(content, response);
        // 第三部 通过主线程将数据返回出去供外界使用
        handler.post(new Runnable() {
            @Override
            public void run() {
                iJsonDataListener.onSuccess(t);
            }
        });
    }

    @Override
    public void onFailed() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                iJsonDataListener.onFailed();
            }
        });
    }

    /**
     * 讲inputStream转化为String类型
     * @param inputStream
     * @return
     */
    private String getContent(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line + "/n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString().replace("/n", "");
    }
}
