package com.example.q.cs496_pj2;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpConnection {

    private OkHttpClient client;
    private static HttpConnection instance = new HttpConnection();
    public static HttpConnection getInstance() {
        return instance;
    }

    private HttpConnection(){ this.client = new OkHttpClient(); }


    /** 웹 서버로 요청을 한다. */
    public void requestWebServer(String parameter, String parameter2, String parameter3, Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("firstName", parameter)
                .add("lastName", parameter2)
                .add("email", parameter3)
                .build();
        Request request = new Request.Builder()
                .url("http://52.231.64.148:8080/api")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }


}
