package com.example.q.cs496_pj2;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Context context;
    JSONArray posts;
    JSONObject user_info;
    JSONObject my_likes;



    public PostAdapter(Context context, JSONArray json, JSONObject user_info){

        this.context = context;
        this.posts = json;
        this.user_info = user_info;
        //this.my_likes = my_likes;


    }

    public static class ViewHolder extends RecyclerView.ViewHolder   {

        private ImageView Vprofile;
        private TextView Vid;
        private TextView Vdate;
        private ImageView Vphoto;
        private TextView Vcontent;
        private ImageView Vlike_button; //TODO: button으로 바꿔야하지 않을까
        private TextView Vlike_count1;
        private TextView Vlike_count2;
        private TextView Vlike_count3;

        private TextView Vcomment_count;
        private String object_id;
        private JSONObject user_info;

        public ViewHolder(View v, JSONObject user_info) {
            super(v);

            Vprofile = v.findViewById(R.id.profile_image);
            Vid = v.findViewById(R.id.tv_name);
            Vdate = v.findViewById(R.id.tv_date);
            Vphoto = v.findViewById(R.id.photo);
            Vcontent = v.findViewById(R.id.tv_content);
            Vlike_button = v.findViewById(R.id.like_button);
            /*Vlike_button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Log.d("pathdebug", "like button pressed");

                    ConnectServer connectServer = new ConnectServer();
                    try {
                        Log.d("pathdebug", "like button pressed");
                        ViewHolder holder = (ViewHolder) v.getParent();

                        connectServer.requestPress_like(v.gethol.object_id, holder.user_info.get("email").toString());
                        holder.Vlike_button.setImageResource(R.drawable.like_pressed);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });*/
            Vlike_count1 = v.findViewById(R.id.tv_like_count1);
            Vlike_count2 = v.findViewById(R.id.tv_like_count2);
            Vlike_count3 = v.findViewById(R.id.tv_like_count3);

            Vcomment_count = v.findViewById(R.id.tv_comment_count);
            this.user_info = user_info;
        }

/*
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.like_button:
                    Log.d("pathdebug", "like button pressed");

                    ConnectServer connectServer = new ConnectServer();
                    try {
                        Log.d("pathdebug", "like button pressed");
                        ViewHolder holder = (ViewHolder) v.getParent();

                        connectServer.requestPress_like(holder.object_id, holder.user_info.get("email").toString());
                        holder.Vlike_button.setImageResource(R.drawable.like_pressed);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    break;
            }
        }*/

    }

    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, null);
        ViewHolder vh = new ViewHolder(v, user_info);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position){

        JSONObject json = null;

        try {
            json = posts.getJSONObject(position);
            Log.d("pathdebug", "onbindviewholder: " + json.toString());

        } catch (JSONException e) {
            Log.d("error", "json error 1");
        }

        try {
            holder.object_id = json.get("_id").toString();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("error", "json error 1.5");

        }

        try {
            holder.Vcontent.setText(json.get("content").toString());
        } catch (JSONException e) {
            Log.d("error", "json error 2");
        }

        try {
            holder.Vcomment_count.setText(json.get("comment_count").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }



        try {
            String date = json.get("date").toString();
            holder.Vdate.setText(date.substring(0,10) + "  "  + date.substring(11,16));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            holder.Vid.setText(json.get("writer").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int total_like = 0;
        JSONArray liked_array;
        int my_like_count = 0;

        try {
            total_like = json.getInt("like_count");
            liked_array = json.getJSONArray("liked");

            for(int i = 0; i < liked_array.length(); i++){
                if(liked_array.getJSONObject(i).get("user_id").toString().equals(user_info.get("email"))){
                    my_like_count = liked_array.getJSONObject(i).getInt("number");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        holder.Vlike_count1.setText(Integer.toString(total_like));
        holder.Vlike_count2.setText(Integer.toString(my_like_count));

        if(total_like == 0)
            holder.Vlike_count3.setText("0%");
        else{
            float a = (float)my_like_count/(float)total_like*100;
            double my_contribution = Math.round(a*100d)/100d;
            holder.Vlike_count3.setText(Double.toString(my_contribution)+"%");
        }

        if(my_like_count == 0){
            holder.Vlike_button.setImageResource(R.drawable.like);
        }else{
            holder.Vlike_button.setImageResource(R.drawable.like_pressed);

        }





        holder.Vlike_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("pathdebug", "like button pressed");
                if(!holder.Vlike_button.isClickable())
                    return;
                ConnectServer connectServer = new ConnectServer();
                try {
                    Log.d("pathdebug", "like button pressed");

                    connectServer.requestPress_like(holder.object_id, holder.user_info.get("email").toString());
                    holder.Vlike_button.setImageResource(R.drawable.like_pressed);

                    int total_count = Integer.parseInt(holder.Vlike_count1.getText().toString())+1;
                    int my_count = Integer.parseInt(holder.Vlike_count2.getText().toString())+1;

                    holder.Vlike_count1.setText(Integer.toString(total_count));
                    holder.Vlike_count2.setText(Integer.toString(my_count));

                    float a = (float)my_count/(float)total_count*100;
                    double my_contribution = Math.round(a*100d)/100d;
                    holder.Vlike_count3.setText(Double.toString(my_contribution)+"%");

                    JSONObject json_position = posts.getJSONObject(holder.getAdapterPosition());

                    json_position.put("like_count", total_count);
                    Log.d("pathdebug1111", "" + total_count);

                    JSONArray liked_array = json_position.getJSONArray("liked");

                    for(int i = 0; i < liked_array.length(); i++){
                        if(liked_array.getJSONObject(i).get("user_id").toString().equals(user_info.get("email"))){

                            JSONObject addob = new JSONObject();
                            addob.put("email", user_info.get("email"));
                            addob.put("number", my_count);

                            Log.d("pathdebug1111", addob.toString());
                            json_position.getJSONArray("liked").put(i, addob);

                        }
                    }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


            }
        });

        String photo = null;
        try {
            photo = json.get("photo").toString();
            byte[] image = Base64.decode(photo, Base64.DEFAULT);
            holder.Vphoto.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("error", "json error 3");

        }



    }

    @Override
    public int getItemCount(){
        if(posts == null){
            return 0;
        }
        return posts.length();
    }



}
