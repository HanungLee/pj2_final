package com.example.q.cs496_pj2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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


                JSONObject post = new JSONObject();

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

                Log.d("pathdebug", post.toString());

                Intent intent = new Intent();
                intent.putExtra("new_post", post.toString());
                setResult(RESULT_OK, intent);
                finish();
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

}
