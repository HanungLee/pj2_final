package com.example.q.cs496_pj2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PageOneLogin extends Fragment{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private PageOneLogin.OnFragmentInteractionListener mListener;
    private String firstName;
    private String lastName;
   // private EditText phoneNumber;
    private String email;
    private Button saveButton;
    private Button fetchButton;
    Context thiscontext;

    private static final String TAG = "TestActivity";
    private HttpConnection httpConn = HttpConnection.getInstance();

    public PageOneLogin() {

    }

    public static PageOneLogin newInstance(String param1, String param2, String param3){
        PageOneLogin fragment = new PageOneLogin();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
        }

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_one_login, container, false);

        saveButton = rootView.findViewById(R.id.save);
        fetchButton = rootView.findViewById(R.id.fetch);
        thiscontext = container.getContext();
        Log.d("fuck1", "12354");


        /*
        fN = rootView.findViewById(R.id.firstName);
        String firstName = fN.getText().toString();
        lN = rootView.findViewById(R.id.lastName);
        String lastName = lN.getText().toString();
        eL = rootView.findViewById(R.id.email);
        String email = eL.getText().toString();
*/
        saveButton.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View view) {

                EditText fN = getView().findViewById(R.id.first_Name);
                EditText lN = getView().findViewById(R.id.last_Name);
                EditText eL = getView().findViewById(R.id.ema_il);
                firstName = fN.getText().toString();
                lastName = lN.getText().toString();
                email = eL.getText().toString();
                Log.d("firstName1 : ", ""+firstName);
                Log.d("lastName1 : ", ""+lastName);
                Log.d("email1 : ", ""+email);

                Toast.makeText(thiscontext, "Saved to the DB", Toast.LENGTH_LONG).show();
                sendData();

            }
        });


        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(thiscontext, "Fetched from DB", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), FetchedActivity.class);
                Log.d("home","123");
                startActivity(intent);

            }
        });
        return rootView;
    }

    private void sendData() {
        new Thread() {
            public void run() {
                httpConn.requestWebServer(firstName, lastName, email, callback);
            }
        }.start();;
    }

    private final Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d(TAG, "CallBack Error:"+e.getMessage());
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String body = response.body().string();
            Log.d(TAG, "Server Requested Body:"+body);
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PageOne.OnFragmentInteractionListener) {
            mListener = (PageOneLogin.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }
}



