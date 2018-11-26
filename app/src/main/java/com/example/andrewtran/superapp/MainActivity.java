package com.example.andrewtran.superapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";
    private TextView mTextMessage;
    private FirebaseAuth mAuth;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            int itemID = item.getItemId();
            if(itemID == R.id.navigation_map){
                fragment = new Map_Fragment();
                loadFragment(fragment);
                return true;
            }
            if(itemID == R.id.navigation_message){
                if (CheckSigningInStatus()){
                    fragment = new Message_Fragment_Conversation();
                    loadFragment(fragment);
                    return true;
                }
                else{
                    fragment = new Message_Fragment_LogIn();
                    loadFragment(fragment);
                    return true;
                }


            }
            /*switch (item.getItemId()) {
                case R.id.navigation_map:
                    fragment = new Map_Fragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_message:
                    fragment = new Message_Fragment_LogIn();
                    loadFragment(fragment);
                    return true;

            }*/
            return false;
        }
    };


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigationBar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mAuth = FirebaseAuth.getInstance();
        initialiseUI();
    }

    private void initialiseUI() {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    transaction.add(R.id.fragment, new Map_Fragment());
    transaction.commit();

    }
    private boolean CheckSigningInStatus(){

        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser != null){
            Log.d(TAG, "CheckSigningInStatus: "+ "Logged in as " + mFirebaseUser.getDisplayName());
            return true;

        }
        else {
            Log.d(TAG, "CheckSigningInStatus: "+ "Logged out");
            return false;
        }
    }

}
