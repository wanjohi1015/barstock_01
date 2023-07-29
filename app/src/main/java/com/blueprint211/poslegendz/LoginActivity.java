package com.blueprint211.poslegendz;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {
    private EditText TextEmail, TextPassword;
    private FirebaseAuth mAuth;
    boolean isConnected = false;
    ConnectivityManager connectivityManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);

        TextEmail = findViewById(R.id.editTextEmail);
        TextPassword = findViewById(R.id.editTextPassword);
        Button signinBtn = findViewById(R.id.cirLoginButton);

        mAuth = FirebaseAuth.getInstance();

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected) {
                loginUser();
                }else{
                    startActivity(new Intent(getApplicationContext(), noInternetActivity.class));
                }

            }
        });



    }
    private void loginUser(){
        String email = TextEmail.getText().toString();
        String pass = TextPassword.getText().toString();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            if (!pass.isEmpty()){
                mAuth.signInWithEmailAndPassword(email , pass)
                        .addOnSuccessListener(authResult -> {
                            Toast.makeText(LoginActivity.this, "Login Successfully !!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this , dashboardActivity.class));
                            finish();
                        }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Login Failed !!", Toast.LENGTH_SHORT).show());
            }else{
                TextPassword.setError("Empty Fields Are not Allowed");
            }
        }else if(email.isEmpty()){
            TextEmail.setError("Empty Fields Are not Allowed");
        }else{
            TextEmail.setError("Pleas Enter Correct Email");
        }
    }


    public void onLoginClick(View View){
        startActivity(new Intent(this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);

    }
    private void registerNetworkCallback(){


        try {

            connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){

                @Override
                public void onAvailable(@NonNull Network network) {
                    isConnected = true;
                }

                @Override
                public void onLost(@NonNull Network network) {
                    isConnected = false;
                }
            });




        }catch (Exception e){

            isConnected = false;

        }

    }
    @Override
    public void onStart() {

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

            if (firebaseUser != null) {

                startActivity(new Intent(LoginActivity.this, dashboardActivity.class));

            }

            super.onStart();

    }
    @Override
    protected void onResume() {
        super.onResume();
        registerNetworkCallback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        new ConnectivityManager.NetworkCallback();

    }

}
