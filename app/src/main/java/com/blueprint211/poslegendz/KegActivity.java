package com.blueprint211.poslegendz;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class KegActivity extends AppCompatActivity {

    RecyclerView kegview;
    ArrayList<keg> keglist = new ArrayList<>();
    FirebaseFirestore firestore;
    KegAdapter kadapter;
    ProgressBar spinner;
    EditText searchBox;
    TextView emptylist;
    Button addbarrel;
    FloatingActionButton fab;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String time  = dateFormat.format(new Date());
    boolean isConnected = false;
    ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keg);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        spinner = (ProgressBar) findViewById(R.id.progressBar);
        kegview = findViewById(R.id.precview);
        addbarrel = findViewById(R.id.addButton);
        fab = findViewById(R.id.cart);
        kegview.setLayoutManager(new LinearLayoutManager(this));
        kadapter = new KegAdapter(this, keglist);
        kegview.setAdapter(kadapter);
        firestore = FirebaseFirestore.getInstance();
        emptylist = findViewById(R.id.empty);


        spinner.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                spinner.setVisibility(View.INVISIBLE);

            }
        }, 2000);
        addbarrel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savebarrel();

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference docRef =  FirebaseFirestore.getInstance().collection("Cart").document("Keg Beer");
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                startActivity(new Intent(KegActivity.this, CartActivity.class));
                            } else {
                                Toast.makeText(KegActivity.this, "No Keg Item is added in your Cart,click on either Barrels to sell", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                });




            }
        });
        searchBox = (EditText) findViewById(R.id.searchName);
        CollectionReference orders = firestore.collection("Barrels");
        Query expenses = orders.orderBy("boughttime", Query.Direction.DESCENDING);


        expenses.get().addOnSuccessListener(queryDocumentSnapshots -> {


            if (!queryDocumentSnapshots.isEmpty()) {
                emptylist.setVisibility(View.INVISIBLE);

                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                for (DocumentSnapshot d : list) {
                    keg ob = d.toObject(keg.class);
                    assert ob != null;
                    keglist.add(ob);

                }
                kadapter.notifyDataSetChanged();

            } else {
                emptylist.setVisibility(View.VISIBLE);
            }

        });


    }

    private void savebarrel() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(addbarrel.getContext());
        View dialogView = LayoutInflater.from(addbarrel.getContext()).inflate(R.layout.add_barrel, null);
        EditText editprice = (EditText) dialogView.findViewById(R.id.editTextprice);
        EditText editname = (EditText) dialogView.findViewById(R.id.editTextname);
        Button barrel = (Button) dialogView.findViewById(R.id.cirRegisterButton);

        alertDialog.setView(dialogView);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
        barrel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected) {


                String price = (editprice.getText().toString());
                String names = (editname.getText().toString());
                if (price.isEmpty()) {


                    editprice.setError("Enter selling price of One item");
                } else if (names.isEmpty()) {

                    editname.setError("Enter number of items");


                } else {
                    dialog.dismiss();
                    int barprice = Integer.parseInt(price);
                    HashMap<String, Object> hashMap = new HashMap<>();


                    hashMap.put("buyprice", barprice);
                    hashMap.put("name", names);
                    hashMap.put("barrelref",  names+time);
                    hashMap.put("donetime", "On Sale Period");
                    hashMap.put("boughttime", FieldValue.serverTimestamp());
                    FirebaseFirestore.getInstance().collection("Barrels").document().set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<Void> task) {
                            recreate();
                        }
                    });
                }
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

    public void onLoginClick (View view){
        startActivity(new Intent(this, KegActivity.class));


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