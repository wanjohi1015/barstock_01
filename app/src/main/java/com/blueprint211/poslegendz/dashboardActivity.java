package com.blueprint211.poslegendz;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class dashboardActivity extends AppCompatActivity implements View.OnClickListener{
     FirebaseAuth mAuth;
     FirebaseFirestore firestore;
    FirebaseUser user;
    TextView username;
    String cashier,userid;
    ImageView logout;
    CardView addItems, deleteItems, sellItems, viewInventory,viewexpenses,keg;
    boolean isConnected = false;
    ConnectivityManager connectivityManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("TIPSY2BAR DASHBOARD");
        setSupportActionBar(toolbar);
        username = findViewById(R.id.firebasename);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        logout = findViewById(R.id.imageView2);





        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }});

        addItems = (CardView)findViewById(R.id.addItems);
        sellItems = (CardView) findViewById(R.id.sellItems);
        viewInventory = (CardView) findViewById(R.id.viewInventory);
        deleteItems = (CardView) findViewById(R.id.deleteItems);
        viewexpenses = (CardView) findViewById(R.id.viewexpenses);
        keg = (CardView) findViewById(R.id.keg);


        addItems.setOnClickListener(this);
        sellItems.setOnClickListener(this);
        viewInventory.setOnClickListener(this);
        deleteItems.setOnClickListener(this);
        viewexpenses.setOnClickListener(this);
        keg.setOnClickListener(this);

        getusername();
        


    }

    private void  getusername() {
        user = mAuth.getCurrentUser();
        assert user != null;
        userid = user.getUid();
        firestore.collection("Users").whereEqualTo("userid", userid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    for (DocumentSnapshot shs : document.getDocuments()) {
                        Users name = shs.toObject(Users.class);
                        assert name != null;
                        cashier = name.getName();
                        username.setText("Welcome, " + cashier);


                    }
                }
            }

        });

    }

    @Override
    public void onClick(View v) {

        Intent i;

        if(isConnected) {
        switch (v.getId()){
            case R.id.addItems : i = new Intent(this,additemActivity.class); startActivity(i); break;
            case R.id.sellItems : i = new Intent(this,sellitemActivity.class);startActivity(i); break;
            case R.id.viewInventory : i = new Intent(this,creditview.class);startActivity(i); break;
            case R.id.deleteItems:  i = new Intent(this, ReportActivity.class);startActivity(i); break;
            case R.id.viewexpenses:  i = new Intent(this,ExpenseActivity.class);startActivity(i); break;
            case R.id.keg:  i = new Intent(this,KegActivity.class);startActivity(i); break;
            default: break;
        }
        }else{
            startActivity(new Intent(getApplicationContext(), noInternetActivity.class));
        }

    }
    private void showLoginDialog() {
        final FlatDialog flatDialog = new FlatDialog(dashboardActivity.this);
        flatDialog. setTitle("   Logout?")
                .setFirstButtonText("ACCEPT")
                .setSecondButtonText("CANCEL")
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FirebaseAuth.getInstance().signOut();
                        Intent mySuperIntent = new Intent(dashboardActivity.this, LoginActivity.class);
                        startActivity(mySuperIntent);

                        flatDialog.dismiss();
                    }
                })
                .withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        flatDialog.dismiss();
                    }
                })
                .show();
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
    protected void onResume() {
        super.onResume();
        registerNetworkCallback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        new ConnectivityManager.NetworkCallback();

    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
