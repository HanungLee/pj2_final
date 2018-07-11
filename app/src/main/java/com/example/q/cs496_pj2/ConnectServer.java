package com.example.q.cs496_pj2;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class ConnectServer{
    //Client 생성
    OkHttpClient client = new OkHttpClient();

    private static String URL_Login = "http://52.231.65.165:3000/api/login";
    private static String URL_MY_LIKES = "http://52.231.65.165:3000/api/posts/like";

    private static String URL_NEW_POST = "http://52.231.65.165:3000/api/posts";
    private static String URL_Press_Like = "http://52.231.65.165:3000/api/press_like";

    public void requestGet(String url, String searchKey){

        //URL에 포함할 Query문 작성 Name&Value
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addEncodedQueryParameter("searchKey", searchKey);
        String requestUrl = urlBuilder.build().toString();

        //Query문이 들어간 URL을 토대로 Request 생성
        Request request = new Request.Builder().url(requestUrl).build();

        //만들어진 Request를 서버로 요청할 Client 생성
        //Callback을 통해 비동기 방식으로 통신을 하여 서버로부터 받은 응답을 어떻게 처리 할 지 정의함
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("error", "Connect Server Error is " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("aaaa", "Response Body is " + response.body().string());
            }
        });
    }

    public void request_MY_LIKES(String email, String searchKey){

        String url = URL_MY_LIKES;

        //URL에 포함할 Query문 작성 Name&Value
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addEncodedQueryParameter("user", email);
        String requestUrl = urlBuilder.build().toString();

        //Query문이 들어간 URL을 토대로 Request 생성
        Request request = new Request.Builder().url(requestUrl).build();

        //만들어진 Request를 서버로 요청할 Client 생성
        //Callback을 통해 비동기 방식으로 통신을 하여 서버로부터 받은 응답을 어떻게 처리 할 지 정의함
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("error", "Connect Server Error is " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("pathdebug", "Response Body is " + response.body().string());
            }
        });
    }




    public void requestPost(String json){

        String url = URL_NEW_POST;

        JSONObject json_post = null;
        try {
            json_post = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("pathdebug", "error 0 request post");

        }

        FormBody.Builder requestBodyBuilder = new FormBody.Builder();
        try {
            requestBodyBuilder = new FormBody.Builder().add("writer", json_post.get("writer").toString())
                                                        .add("content", json_post.get("content").toString())
                                                        .add("date", json_post.get("date").toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("pathdebug", "error 1 request post");
        }

        try {
            requestBodyBuilder.add("photo", json_post.get("photo").toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("pathdebug", "error 2 request post");

        }


        //Request Body에 서버에 보낼 데이터 작성
        RequestBody requestBody = requestBodyBuilder.build();

        //작성한 Request Body와 데이터를 보낼 url을 Request에 붙임
        Request request = new Request.Builder().url(url).post(requestBody).build();

        //request를 Client에 세팅하고 Server로 부터 온 Response를 처리할 Callback 작성
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("error", "Connect Server Error is " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("aaaa", "Response Body is " + response.body().string());

            }
        });
    }


    public void requestLogin(String json){

        String url = URL_Login;

        JSONObject json_post = null;
        try {
            json_post = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("pathdebug", "error 0 request login");

        }

        FormBody.Builder requestBodyBuilder = new FormBody.Builder();
        try {
            requestBodyBuilder = new FormBody.Builder().add("name", json_post.get("name").toString())
                    .add("email", json_post.get("email").toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("pathdebug", "error 1 request login");
        }




        //Request Body에 서버에 보낼 데이터 작성
        RequestBody requestBody = requestBodyBuilder.build();

        //작성한 Request Body와 데이터를 보낼 url을 Request에 붙임
        Request request = new Request.Builder().url(url).post(requestBody).build();

        //request를 Client에 세팅하고 Server로 부터 온 Response를 처리할 Callback 작성
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("error", "Connect Server Error is " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("aaaa", "Response Body is " + response.body().string());


            }
        });
    }

    public void requestPress_like(String object_id, String email){
        String url = URL_Press_Like;


        FormBody.Builder requestBodyBuilder = new FormBody.Builder();
        requestBodyBuilder = new FormBody.Builder().add("email", email).add("post_id", object_id);





        //Request Body에 서버에 보낼 데이터 작성
        RequestBody requestBody = requestBodyBuilder.build();

        //작성한 Request Body와 데이터를 보낼 url을 Request에 붙임
        Request request = new Request.Builder().url(url).put(requestBody).build();

        Log.d("pathdebug", request.toString());
        //request를 Client에 세팅하고 Server로 부터 온 Response를 처리할 Callback 작성
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("error", "Connect Server Error is " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("aaaa", "Response Body is " + response.body().string());


            }
        });
    }



}


