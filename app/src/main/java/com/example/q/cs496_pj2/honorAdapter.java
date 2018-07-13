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

public class honorAdapter extends RecyclerView.Adapter<honorAdapter.ViewHolder>  {

    Context context;
    JSONArray posts;
    JSONObject user_info;
    JSONObject my_likes;

    public honorAdapter(Context context, JSONArray json, JSONObject user_info){

        this.context = context;
        this.posts = json;
        this.user_info = user_info;
        //this.my_likes = my_likes;
    }





    public static class ViewHolder extends RecyclerView.ViewHolder   {

        private TextView Vid;
        private TextView Vdate;
        private ImageView Vphoto;
        private TextView Vcontent;
        private ImageView Vlike_button; //TODO: button으로 바꿔야하지 않을까
        private TextView Vlike_count1;
        private TextView Vlike_count2;
        private TextView Vlike_count3;

        private String object_id;
        private JSONObject user_info;

        public ViewHolder(View v, JSONObject user_info) {
            super(v);

            Vid = v.findViewById(R.id.textView3);
            Vdate = v.findViewById(R.id.textView2);
            Vphoto = v.findViewById(R.id.photo);
            Vcontent = v.findViewById(R.id.tv_content);
            Vlike_button = v.findViewById(R.id.like_button);

            Vlike_count1 = v.findViewById(R.id.tv_like_count1);
            Vlike_count2 = v.findViewById(R.id.tv_like_count2);
            Vlike_count3 = v.findViewById(R.id.tv_like_count3);

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
    public honorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.honor, null);
        honorAdapter.ViewHolder vh = new honorAdapter.ViewHolder(v, user_info);
        return vh;
    }

    @Override
    public void onBindViewHolder(final honorAdapter.ViewHolder holder, int position){

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
