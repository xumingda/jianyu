package com.lte.data;

/*
 *  Date:2018/6/6.
 *  Time:下午5:03.
 *  author:chenxiaojun
 */

public class HttpResult {

    private int result = -1;

    private String time;

    private int authTime;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getAuthTime() {
        return authTime;
    }

    public void setAuthTime(int authTime) {
        this.authTime = authTime;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "result=" + result +
                ", time='" + time + '\'' +
                ", authTime=" + authTime +
                '}';
    }
}
