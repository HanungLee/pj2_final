package com.example.q.cs496_pj2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PICK_FROM_ALBUM = 0;


    private JSONObject login_info;
    private Toolbar mtoolbar;

    private TextView mname;
    private TextView mdate;
    private ImageView mphoto;
    private ImageView marrow;
    private TextView mcontent;

    private int check_photo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mtoolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mtoolbar);

        Intent intent = getIntent();
        try {
            login_info = new JSONObject(intent.getStringExtra("login_info"));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("pathdebug", "intent json error");
        }


        mname = findViewById(R.id.tv_name_NP);
        try {
            mname.setText(login_info.get("name").toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("pathdebug", "mname.settext");
        }

        mdate = findViewById(R.id.tv_date_NP);

        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA);
        Date currentTime = new Date();
        String mTime = mSimpleDateFormat.format(currentTime);
        mdate.setText(mTime);


        mphoto = findViewById(R.id.photo_NP);
        marrow = findViewById(R.id.image_arrow_NP);
        marrow.setOnClickListener(this);

        mcontent = findViewById(R.id.tv_content_NP);


        mphoto.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.photo_NP:
                doTakeAlbumAction();
                break;

            case R.id.image_arrow_NP:

                if(mcontent.getText().toString().equals("")){
                    Toast.makeText(this, "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                    break;
                }


                /*JSONObject post = new JSONObject();

                try {
                    post.put("writer", login_info.get("email").toString());
                } catch (JSONException e) {
                    Log.d("error", "json error, onclick email");
                }
                try {
                    post.put("content", mcontent.getText());
                } catch (JSONException e) {
                    Log.d("error", "json error, onclick mcontent");
                }

                BitmapDrawable drawable = (BitmapDrawable) mphoto.getDrawable();
                if(drawable != null && check_photo == 1) {
                    Bitmap bitmap = drawable.getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageInByte = baos.toByteArray();
                    String photo_string = Base64.encodeToString(imageInByte, Base64.DEFAULT);
                    try {
                        post.put("photo", photo_string);
                    } catch (JSONException e) {
                        Log.d("error", "json error, onclick photo");
                    }
                }

                try {
                    post.put("date", mdate.getText().toString());
                } catch (JSONException e) {
                    Log.d("error", "json error, onclick date");
                }

                Log.d("pathdebug", post.toString());*/

               /* Intent intent = new Intent();
                intent.putExtra("new_post", post.toString());
                setResult(RESULT_OK, intent);
                finish();*/

                Map<String, Object> post = new LinkedHashMap<>();

                try {
                    post.put("writer", login_info.get("email").toString());
                } catch (JSONException e) {
                    Log.d("error", "json error, onclick email");
                }

                post.put("content", mcontent.getText());


                BitmapDrawable drawable = (BitmapDrawable) mphoto.getDrawable();
                if(drawable != null && check_photo == 1) {
                    Bitmap bitmap = drawable.getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageInByte = baos.toByteArray();
                    String photo_string = Base64.encodeToString(imageInByte, Base64.DEFAULT);

                    post.put("photo", photo_string);

                }


                post.put("date", mdate.getText().toString());

                postNew("http://52.231.65.165:3000/api/posts", post);


                break;
        }


    }

    public void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)
            return;

        switch (requestCode){
            case PICK_FROM_ALBUM:
                mphoto.setImageURI(data.getData());
                check_photo = 1;
                break;
        }

    }


    public void postNew(String url, final Map<String, Object> post) {
        class GetDataJSON extends AsyncTask<String,Void,String> {
            @Override
            protected String doInBackground(String... params) {
                //JSON 받아온다.



                URL url = null;
                try {
                    url = new URL(params[0]);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


                StringBuilder postData = new StringBuilder();
                for(Map.Entry<String,Object> param : post.entrySet()) {
                    if(postData.length() != 0) postData.append('&');
                    try {
                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    postData.append('=');
                    try {
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                byte[] postDataBytes = new byte[0];
                try {
                    postDataBytes = postData.toString().getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection)url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    conn.setRequestMethod("POST");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                conn.setDoOutput(true);
                try {
                    conn.getOutputStream().write(postDataBytes); // POST 호출
                } catch (IOException e) {
                    e.printStackTrace();
                }


                BufferedReader br = null;
                try {
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



                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
}
