package com.example.q.cs496_pj2;

        import android.app.ProgressDialog;
        import android.content.Context;
        import android.graphics.Color;
        import android.graphics.drawable.ColorDrawable;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListAdapter;
        import android.widget.SimpleAdapter;
        import android.widget.Toast;


        import com.baoyz.swipemenulistview.SwipeMenu;
        import com.baoyz.swipemenulistview.SwipeMenuCreator;
        import com.baoyz.swipemenulistview.SwipeMenuItem;
        import com.baoyz.swipemenulistview.SwipeMenuListView;
        import com.google.gson.Gson;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedReader;

        import java.io.InputStream;
        import java.io.InputStreamReader;

        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.util.ArrayList;
        import java.util.HashMap;


public class FetchedActivity extends AppCompatActivity {
    private static String TAG = "Test_FetchedActivity";
    private static final String TAG1 = "Swipe_test";
    private static final String TAG_JSON="profiles";
    private static final String TAG_fName = "firstName";
    private static final String TAG_lNAME = "lastName";
    private static final String TAG_email ="email";
    String search_text;
    SwipeMenuListView mlistView;
    String mJsonString;
    String parsedString;
    private ArrayList<HashMap<String, String>> mArrayList2 = new ArrayList<HashMap<String, String>>();

    public FetchedActivity() {
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetched);
        mlistView = (SwipeMenuListView) findViewById(R.id.listView_list);
        /*
        try {
            JSONArray jsonArray = new JSONArray(mJsonString);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String firstname = item.getString(TAG_fName);
                String lastname = item.getString(TAG_lNAME);
                String email = item.getString(TAG_email);

                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put(TAG_fName, firstname);
                hashMap.put(TAG_lNAME, lastname);
                hashMap.put(TAG_email, email);
                mArrayList1.add(hashMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

*/
        Button button = findViewById(R.id.searchbutton);
        GetData task = new GetData();
        final String stringJson = task.execute("http://52.231.64.148:8080/api/people").toString();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText fN = findViewById(R.id.search_profile);
                search_text = fN.getText().toString();

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(mJsonString);
                    JSONArray newJsonArray = new JSONArray();

                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        if(item.getString("firstName").equals(search_text)){
                            newJsonArray.put(item);
                        }
                        else if(item.getString("lastName").equals(search_text)){
                            newJsonArray.put(item);
                        }
                        else if(item.getString("email").equals(search_text)){
                            newJsonArray.put(item);
                        }
                        else{
                            continue;
                        }
                    }

                    if(search_text.equals("") || search_text == null){
                        showResult(mJsonString);
                    }
                    else{
                        parsedString = newJsonArray.toString();
                        showResult(parsedString);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



    }


    private class GetData extends AsyncTask<String, Void, String>{
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Please wait", Toast.LENGTH_LONG);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){
                Toast.makeText(FetchedActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
            else {
                mJsonString = result;
                showResult(mJsonString);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }

    private void showResult(String s){
         final ArrayList<HashMap<String, String>> mArrayList1 = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(s);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String firstname = item.getString(TAG_fName);
                String lastname = item.getString(TAG_lNAME);
                String email = item.getString(TAG_email);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_fName, firstname);
                hashMap.put(TAG_lNAME, lastname);
                hashMap.put(TAG_email, email);
                mArrayList1.add(hashMap);
            }


            ListAdapter adapter = new SimpleAdapter(
                    getApplicationContext(), mArrayList1, R.layout.item_list,
                    new String[]{TAG_fName, TAG_lNAME, TAG_email},
                    new int[]{R.id.textView_list_fname, R.id.textView_list_lname, R.id.textView_list_email}
            );
            mlistView.clearAnimation();
            mlistView.setAdapter(adapter);

            SwipeMenuCreator creator = new SwipeMenuCreator() {

                @Override
                public void create(SwipeMenu menu) {
                    // create "open" item
                    SwipeMenuItem openItem = new SwipeMenuItem(
                            getApplicationContext());
                    // set item background
                    openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                            0xCE)));
                    // set item width
                    openItem.setWidth(150);
                    // set item title
                    openItem.setTitle("Open");
                    // set item title fontsize
                    openItem.setTitleSize(18);
                    // set item title font color
                    openItem.setTitleColor(Color.WHITE);
                    // add to menu
                    menu.addMenuItem(openItem);

                    // create "delete" item
                    SwipeMenuItem deleteItem = new SwipeMenuItem(
                            getApplicationContext());
                    // set item background
                    deleteItem.setBackground(new ColorDrawable(Color.rgb(0x42,
                            0xaa, 0xf4)));
                    // set item width
                    deleteItem.setWidth(150);
                    // set a icon
                    deleteItem.setIcon(R.drawable.ic_delete);
                    // add to menu
                    menu.addMenuItem(deleteItem);
                }
            };
            mlistView.setMenuCreator(creator);

            mlistView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                    switch (index) {
                        case 0:
                            // open
                            Log.d(TAG1, "onMenuItemClick : clicked item" + index);
                            break;
                        case 1:
                            // delete
                            Toast.makeText(FetchedActivity.this, "delete process", Toast.LENGTH_LONG).show();
                            String value = new Gson().toJson(mArrayList1.remove(position)).toString();
                            remove(value);
                            showResult(value);

                            break;
                    }
                    // false : close the menu; true : not close the menu
                    return false;
                }
            });

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
    public void remove(String s){
        mJsonString = s;

    }
/*
    public HashMap<String, String> getRemovedArray(ArrayList<HashMap<String, String>> paramArray, int position){
        //ArrayList<HashMap<String, String>> resultArray = paramArray;
        //resultArray.remove(position);
        return paramArray.remove(position);
    }
*/
/*
    private void removeItem(){
        HashMap<String,String>map = new HashMap<String, String>();
        map.put("row_1",txtItem.getText().toString());
        map.put("row_2",txtItem2.getText().toString());
  }
*/
}
