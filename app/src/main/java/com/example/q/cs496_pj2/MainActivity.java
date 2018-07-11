package com.example.q.cs496_pj2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements PageOneLogin.OnFragmentInteractionListener, PageOne.OnFragmentInteractionListener, View.OnClickListener{


    private ViewPager mViewPager;
    private PagerAdapter mSectionsPagerAdapter;
    private CallbackManager callbackManager;
    private JSONObject login_info;
    private JSONObject my_likes;
     ArrayList<Fragment> pages;
     AccessTokenTracker accessTokenTracker;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;

    private static final int CODE_NEW_POST = 1;
    private ArrayList<String> myList;



    private void saveLogin() {
        SharedPreferences pref = getSharedPreferences("Game", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("login_info", login_info.toString());
        editor.commit();
    }

    private void saveLogout(){
        SharedPreferences pref = getSharedPreferences("Game", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("login_info");
        editor.commit();
    }


    private void loadLogin() {
        SharedPreferences pref = getSharedPreferences("Game", Activity.MODE_PRIVATE);
        String login = pref.getString("login_info", null);
        if(login != null) {
            try {
                login_info = new JSONObject(login);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Toast.makeText(MainActivity.this, "username : " + login_info.get("email").toString(), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState){
        Bundle bundle = new Bundle();
        if(login_info != null) {
            bundle.putString("login_info", login_info.toString());
            Log.d("pathdebugerror", "onsaveinstance " + login_info.toString());
        }
        outState.putBundle("save_data", bundle);
        super.onSaveInstanceState(outState);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pages = new ArrayList<>();

        Log.d("pathdebug", "oncreate");

        if(savedInstanceState != null){
            Bundle bundle = savedInstanceState.getBundle("save_data");
            try {
                login_info = new JSONObject(bundle.getString("login_info"));

            } catch (JSONException e) {
                Log.d("error", "json error : savedinstance");
                e.printStackTrace();
            }
        }else{
            loadLogin();
        }



        Fragment pageone, pagetwo, pagethree;
        if(AccessToken.getCurrentAccessToken() == null){
            Log.d("pathdebuglog", "oncreate token null");
             pageone = LogoutFragment.newInstance();
             pagetwo = LogoutFragment.newInstance();
             pagethree = LogoutFragment.newInstance();
        }
        else{
            Log.d("pathdebuglog", "oncreate token not null");


            try {
                pageone = PageOneLogin.newInstance(login_info.getString("id"), login_info.getString("name"), login_info.getString("email"));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("pathdebug", "login info parsing");
                pageone = null;
            }
            pagetwo = PageTwo.newInstance(login_info);
            pagethree = PageTwo.newInstance(login_info);

            ConnectServer connectServer = new ConnectServer();
            connectServer.requestLogin(login_info.toString());

        }

        pages.add(0, pageone);
        pages.add(1, pagetwo);
        pages.add(2, pagethree);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), pages);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });*/

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);




        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("result",object.toString());
                        login_info = object;
                        saveLogin();

                        ConnectServer connectServer = new ConnectServer();
                        connectServer.requestLogin(login_info.toString());


                        Log.d("pathdebuglog", "oncompleted " + login_info.toString());

                        pages.clear();
                        try {
                            pages.add(PageOneLogin.newInstance(login_info.getString("id"), login_info.getString("name"), login_info.getString("email")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("pathdebug", "login info parsing 2");
                        }

                        pages.add(PageTwo.newInstance(login_info));
                        pages.add(PageTwo.newInstance(login_info));

                        mViewPager.getAdapter().notifyDataSetChanged();
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

             /*   Log.d("pathdebuglog", "onsuccess " + login_info.toString());

                pages.clear();
                pages.add(PageTwo.newInstance(login_info, myList));
                pages.add(PageTwo.newInstance(login_info, myList));
                pages.add(PageTwo.newInstance(login_info, myList));

                mViewPager.getAdapter().notifyDataSetChanged();
                Log.d("pathdebuglog", "onSuccess finish");*/

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "login canceled.", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("LoginErr",error.toString());
                Toast.makeText(getApplicationContext(), "login error.", Toast.LENGTH_LONG).show();
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    pages.clear();
                    pages.add(LogoutFragment.newInstance());
                    pages.add(LogoutFragment.newInstance());
                    pages.add(LogoutFragment.newInstance());
                    mViewPager.getAdapter().notifyDataSetChanged();

                    saveLogout();

                }

                /*else{
                    Log.d("pathdebuglog", "token change, create pagetwo");
                    Log.d("pathdebuglog", "login_info" + login_info.toString());

                    pages.clear();
                    pages.add(PageTwo.newInstance(login_info, myList));
                    pages.add(PageTwo.newInstance(login_info, myList));
                    pages.add(PageTwo.newInstance(login_info, myList));
                    Log.d("pathdebuglog", "token change, create pagetwo2");

                    mViewPager.getAdapter().notifyDataSetChanged();
                    Log.d("pathdebuglog", "token change, create pagetwo3");


                }*/
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK){
            Toast.makeText(MainActivity.this, "activity result not ok", Toast.LENGTH_SHORT).show();
        }

        if(requestCode == CODE_NEW_POST){
            if(data != null) {

                String new_post = data.getStringExtra("new_post");
                Log.d("pathdebug", new_post);
                OkHttpClient client = new OkHttpClient();


               // ConnectServer connectServer = new ConnectServer();
               // connectServer.requestPost(new_post);

                String url = "http://52.231.65.165:3000/api/posts";

                JSONObject json_post = null;
                try {
                    json_post = new JSONObject(new_post);
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
                        Log.d("pathdebug", "post new finished " + response.body().string());

                        runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                pages.clear();
                                try {
                                    pages.add(PageOneLogin.newInstance(login_info.getString("id"), login_info.getString("name"), login_info.getString("email")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("pathdebug", "login info parsing");
                                }

                                pages.add(PageTwo.newInstance(login_info));
                                pages.add(PageTwo.newInstance(login_info));

                                mViewPager.getAdapter().notifyDataSetChanged();
                            }


                        });
                        /*pages.clear();
                        pages.add(PageTwo.newInstance(login_info));
                        pages.add(PageTwo.newInstance(login_info));
                        pages.add(PageTwo.newInstance(login_info));

                        mViewPager.getAdapter().notifyDataSetChanged();
                        */
                        //startActivity();

                    }
                });




            }
            return;
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    private boolean post_new_background(String new_post){



        return false;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
                Toast.makeText(this, "Floating Action Button", Toast.LENGTH_SHORT).show();

                break;
            case R.id.fab1:
                anim();
                Toast.makeText(this, "Button1 :  insert new post", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, NewPostActivity.class);
                intent.putExtra("login_info", login_info.toString());
                startActivityForResult(intent, CODE_NEW_POST);
                break;
            case R.id.fab2:
                anim();
                Toast.makeText(this, "Button2", Toast.LENGTH_SHORT).show();
                break;
        }


    }

    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    public static class LogoutFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public LogoutFragment() {
        }


        public static LogoutFragment newInstance() {
            LogoutFragment fragment = new LogoutFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText("Login please");

            return rootView;
        }
    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<Fragment> pages;

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<Fragment> pages){
            super(fm);
            this.pages = pages;
        }

        @Override
        public Fragment getItem(int position) {

            if(position == 0)
                return pages.get(0);
            else if(position == 1)
                return pages.get(1);
            else
                return pages.get(2);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }


}
