package com.blueprint211.poslegendz;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    EditText TextEmail, etname, TextPassword;
    FirebaseAuth mAuth;
    boolean isConnected = false;
    ConnectivityManager connectivityManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        changeStatusBarColor();


        etname = findViewById(R.id.editTextName);
        TextEmail = findViewById(R.id.editTextEmail);
        TextPassword = findViewById(R.id.editTextPassword);
        Button signUpBtn = findViewById(R.id.cirRegisterButton);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected) {
                createUser();
                }else{
                    startActivity(new Intent(getApplicationContext(), noInternetActivity.class));
                }
            }
        });
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

    private void createUser () {
        String nameField = etname.getText().toString();
        String editTextEmail = TextEmail.getText().toString();
        String editTextPassword = TextPassword.getText().toString();


        if (nameField.isEmpty()) {


            etname.setError("Enter your Name");
        } else if (editTextEmail.isEmpty()) {

            TextEmail.setError("Enter your Email");


        } else if (editTextPassword.isEmpty() || editTextPassword.length() < 6) {

            TextPassword.setError("Password length must be more than 6");

        } else {

            CreateUsers(nameField, editTextEmail, editTextPassword);


        }


    }

    private void changeStatusBarColor () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void onLoginClick (View view){
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);

    }


    private void CreateUsers (String nameField, String editTextEmail, String editTextPassword){

        mAuth.createUserWithEmailAndPassword(editTextEmail, editTextPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser user = mAuth.getCurrentUser();

                    assert user != null;
                    String userid = user.getUid();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name", nameField);
                    hashMap.put("email", editTextEmail);
                    hashMap.put("userid", userid);


                    firestore.collection("Users").document(nameField).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });


                    Toast.makeText(RegisterActivity.this, "Registered Successfully !!", Toast.LENGTH_SHORT).show();


                    // if the user is registered already
                    // take the user to product list

                    startActivity(new Intent(RegisterActivity.this, dashboardActivity.class));
                    finish();


                }


            }
        });



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