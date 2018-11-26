package com.example.andrewtran.superapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "Blah" ;
    private FirebaseAuth mAuth;
    private TextView email;
    private TextView pwd;
    private TextView result;
    private String emailText ="";
    private String pwdText ="";
    private Button send;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        email = findViewById(R.id.emailText);
        pwd = findViewById(R.id.passwordText);
        send = findViewById(R.id.sendButton);

        mAuth = FirebaseAuth.getInstance();
        initSignInButton();

    }
    private void initSignInButton(){



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!email.getText().toString().isEmpty() && !pwd.getText().toString().isEmpty()){
                    emailText = email.getText().toString();
                    pwdText = pwd.getText().toString();
                    Log.d(TAG, "onClick: " + emailText +pwdText);
                    new signingIn(emailText,pwdText).execute();

                }

            }
        });
    }




    private void initSignInActivity(String email, String pwd){
        result = findViewById(R.id.resultText);
        mAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    result.setText("You are now signed in. Press back and click on other activity to refresh message app!");

                }
                else{
                    result.setText("You are not logged in! Check your username and password");
                }
            }
        });
    }

   class signingIn extends AsyncTask<Void,Void,Void>{
        ProgressDialog progressDialog;
        String username;
        String pwd;
        public signingIn(String u,String p){
            username = u;
            pwd = p;
        }
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           progressDialog = new ProgressDialog(SignInActivity.this);
           progressDialog.setCancelable(true);
           progressDialog.setMessage("Siging in ...");
           progressDialog.setIndeterminate(true);
           progressDialog.show();
       }

       @Override
       protected Void doInBackground(Void... voids) {
           initSignInActivity(username,pwd);
           return null;
       }

       @Override
       protected void onPostExecute(Void aVoid) {
           super.onPostExecute(aVoid);
           try {
               Thread.sleep(1000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           progressDialog.dismiss();
       }
   }
}
