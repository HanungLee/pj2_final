package com.example.q.cs496_pj2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HonorActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    private LinearLayoutManager linearlayoutmanager;
    private JSONObject login_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_honor);
        setTitle("명예의 전당");

        Intent intent = getIntent();
        try {
            login_info = new JSONObject(intent.getStringExtra("login_info"));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("pathdebug", "intent json error");
        }


        linearlayoutmanager = new LinearLayoutManager(this);

        linearlayoutmanager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerview = findViewById(R.id.recy2);

        recyclerview.setHasFixedSize(true);

        recyclerview.setLayoutManager(linearlayoutmanager);

        honorAdapter adapter = new honorAdapter(this, null, login_info);
        recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        getData("http://52.231.65.165:3000/api/posts/honor");



    }


    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String,Void,String> {
            @Override
            protected String doInBackground(String... params) {
                //JSON 받아온다.
                String uri = params[0];
                BufferedReader br = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String json;
                    while((json = br.readLine()) != null) {
                        sb.append(json+"\n");
                    }
                    return sb.toString().trim();
                }catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String myJSON) {
                //makeList(myJSON); //리스트를 보여줌
                /*JSONArray response = null;
                Log.d("pathdebug","my json++" + myJSON);

                try {
                    response = new JSONArray(myJSON);
                } catch (JSONException e) {
                    Log.d("error", myJSON);

                }

                Log.d("pathdebug","make new postadapter++");
//                Log.d("pathdebug","make ->" +response.toString());*/

                JSONArray all_posts = null;
                try {
                    all_posts = new JSONArray(myJSON);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("pathdebug","catch1");

                }

            /*    JSONObject userposts = null;
                try {
                    userposts = response.getJSONObject("userpost");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("pathdebug","catch2");

                }

                JSONArray userposts_array = null;

                try {
                    userposts_array = userposts.getJSONArray("post_like");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("pathdebug","catch3");

                }*/


                honorAdapter postAdapter = new honorAdapter(getApplicationContext(), all_posts, login_info);
                recyclerview.setAdapter(postAdapter);
                postAdapter.notifyDataSetChanged();


            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

}
