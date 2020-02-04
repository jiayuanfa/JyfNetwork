package com.example.jyfnetwork;

/**
 * 接收结果的类
 */
public class ResponseBean {

    private int error_code;
    private String resultCode;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String result_code) {
        this.resultCode = result_code;
    }
}
