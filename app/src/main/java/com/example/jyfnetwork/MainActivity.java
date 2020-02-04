package com.example.jyfnetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.http.IJsonDataListener;
import com.example.http.JyfHttp;

public class MainActivity extends AppCompatActivity {

    // 请求的Url
    String url = "http://v.juhe.cn/historyWeather/citys";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendRequest(View view) {

        // 使用for循环来测试，高并发
        for (int i = 0; i < 100; i++) {
            final int finalX = i;
            JyfHttp.senRequest(url, new RequestBean("2", "bb52107206585ab074f5e59a8c73875b"),  ResponseBean.class, new IJsonDataListener<ResponseBean>() {
                @Override
                public void onSuccess(ResponseBean responseBean) {
                    // 主线程
                    if (responseBean != null) {
                        Log.e("JyfHttp============这是第" + finalX, "我成功了" + responseBean.getResultCode() + "------------" + responseBean.getError_code());
                    }
                }

                @Override
                public void onFailed() {
                    Log.e("JyfHttp============这是第" + finalX, "我失败了");
                }
            });
        }
    }
}
