package com.example.andrewtran.superapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class Message_Fragment_LogIn extends Fragment implements Serializable {
    private static final String TAG = "MainActivity" ;
    private static final int RC_SIGN_IN = 123 ;
    ListView allConversation;
    ArrayList<String> listOfConversation;
    ArrayAdapter arrayAdapter;
    View view;
    String UserName;
    String user_msg_key;
    //All of the information of your data will be stored here
    private DatabaseReference databaseReference;


    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.message_fragment_login,container,false);
        initDatabase();
        initCreateAccountButton();
        initSignInButton();

        return view;
    }

    private void initDatabase(){
        databaseReference= FirebaseDatabase.getInstance("https://composed-hangar-219019-9f967.firebaseio.com/").getReference().child("Users");
    }


    public void initCreateAccountButton(){
        Button button = view.findViewById(R.id.NewAccountButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccountActivity();
            }
        });
    }

    public void initSignInButton(){
        Button button = view.findViewById(R.id.signInButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInActivity();
            }
        });
    }

    public void signInActivity(){
        Intent i = new Intent(getContext(),SignInActivity.class);
        startActivity(i);
    }

    public void createNewAccountActivity(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),RC_SIGN_IN);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userName = user.getDisplayName();
                String userEmail = user.getEmail();
                user_msg_key = databaseReference.push().getKey();
                DatabaseReference databaseReference1 = databaseReference.child(user_msg_key);
                Map<String,Object> map = new HashMap<>();
                map.put("Email",userEmail);
                map.put("Name",userName);
                databaseReference1.updateChildren(map);
                Log.d(TAG, "onActivityResult: Sigining in successfully");
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Log.d(TAG, "onActivityResult: Sigining in has failed");
            }
        }
    }







}

