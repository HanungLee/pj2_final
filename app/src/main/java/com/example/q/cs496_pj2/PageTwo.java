package com.example.q.cs496_pj2;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PageTwo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PageTwo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageTwo extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private JSONObject user_info;
    private JSONObject my_likes;

    private ArrayList<String> myList;



    private OnFragmentInteractionListener mListener;


    private RecyclerView recyclerview;
    private LinearLayoutManager linearlayoutmanager;

    private static int loaded = 0;


    public PageTwo (){
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PageTwo.
     */
    // TODO: Rename and change types and number of parameters
    public static PageTwo newInstance(JSONObject info_object) {
        PageTwo fragment = new PageTwo();
        Bundle args = new Bundle();
        Log.d("pathdebuglog", "page2 newinstance 1");
        Log.d("pathdebuglog", "page2 " + info_object.toString());

        args.putString(ARG_PARAM1, info_object.toString());

        fragment.setArguments(args);
        Log.d("pathdebuglog", "page2 newinstance 2");

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {

                user_info = new JSONObject(getArguments().getString(ARG_PARAM1));
                    Log.d("pathdebuglog", "page2 newinstance 2");



            } catch (JSONException e) {
                Log.d("error", "getArguments().getString(ARG_PARAM1)");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_posts, container, false);

        linearlayoutmanager = new LinearLayoutManager(getActivity());

        linearlayoutmanager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerview = view.findViewById(R.id.recyclerview);

        recyclerview.setHasFixedSize(true);

        recyclerview.setLayoutManager(linearlayoutmanager);

        PostAdapter postAdapter = new PostAdapter(getActivity(), null, user_info);
        recyclerview.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();

        //
        String url;

        //get all posts
        try {
            Log.d("pathdebug", "http://52.231.65.165:3000/api/posts/all/" + user_info.get("email").toString());
            getData("http://52.231.65.165:3000/api/posts/all/" + user_info.get("email").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return view;
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
                JSONObject response = null;
                Log.d("pathdebug","my json++" + myJSON);

                try {
                    response = new JSONObject(myJSON);
                } catch (JSONException e) {
                    Log.d("error", myJSON);

                }

                Log.d("pathdebug","make new postadapter++");
                Log.d("pathdebug","make ->" +response.toString());

                JSONArray all_posts = null;
                try {
                    all_posts = response.getJSONArray("allpost");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("pathdebug","catch1");

                }

                JSONObject userposts = null;
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

                }
                /*
                if(userposts != null && userposts_array != null) {
                    Log.d("pathdebug", "for loop started");

                    for (int i = 0; i < userposts_array.length(); i++) {
                        for (int j = 0; j < all_posts.length(); j++) {
                            String like_id = null;
                            JSONObject post;
                            String post_id = null;
                            try {
                                like_id = userposts_array.getJSONObject(i).get("post_id").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                post = (JSONObject) all_posts.get(j);
                                post_id = post.get("_id").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (like_id != null && post_id != null && like_id.equals(post_id)) {

                                try {
                                    all_posts.getJSONObject(j).put("I_LIKE", "1");
                                    Log.d("pathdebug", "like id equals " + like_id);

                                } catch (JSONException e) {
                                    Log.d("pathdebug", "nononoono");

                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                */

                PostAdapter postAdapter = new PostAdapter(getActivity(), all_posts, user_info);
                recyclerview.setAdapter(postAdapter);
                postAdapter.notifyDataSetChanged();


            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
