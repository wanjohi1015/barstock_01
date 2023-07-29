package com.blueprint211.poslegendz;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class additemActivity extends AppCompatActivity {
    EditText itemname,itemcategory, itemsize,itemprice,itemnumber;
    Button  additem;
    FirebaseFirestore firestore;
    FirebaseUser user;
    FirebaseAuth auth;
    String reference,usname,userid,categoryy;
    float numitems,totalitems;
    boolean isConnected = false;
    ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        itemname = findViewById(R.id.editTextName);
        itemcategory= findViewById(R.id.editTextcategory);
        itemprice = findViewById(R.id.editTextprice);
        itemsize = findViewById(R.id.editTextsize);
        itemnumber = findViewById(R.id.editTextitems);
        additem = findViewById(R.id.cirRegisterButton);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();




        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected) {
                    additem();
                }else{
                    startActivity(new Intent(getApplicationContext(), noInternetActivity.class));
                }

            }
        });

        user = auth.getCurrentUser();
        assert user != null;
        userid = user.getUid();


        firestore.collection("Users").whereEqualTo("userid", userid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                assert value != null;
                for (DocumentSnapshot mu : value.getDocuments()) {
                    Users username = mu.toObject(Users.class);
                    assert username != null;
                    usname = username.getName();



                }
            }



        });


    }

    public  void additem() {
        String name = itemname.getText().toString();
        String category = itemcategory.getText().toString();
        String size = itemsize.getText().toString();
        String price = itemprice.getText().toString();
        float items = Float.parseFloat(itemnumber.getText().toString());

        if (name.isEmpty()) {


            itemname.setError("Enter Name of Product");
        } else if (category.isEmpty()) {

            itemcategory.setError("Enter Your Product Category");


        }
        else if (size.isEmpty()) {

            itemsize.setError("Enter Your Product Size");


        }
        else if (price.isEmpty()) {

            itemprice.setError("Enter Your Product Cost");


        }else if (items == 0) {

            itemnumber.setError("Enter the number of Products");


        } else {
            int iprice=Integer.parseInt(price);
            addstock(name,category,size,iprice,items);
        }




    }

    private void addstock(String name, String category, String size, int iprice, float items) {
        reference = (name+category+size+iprice);

        firestore.collection("Products").whereEqualTo("reference", reference).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    if ( document.isEmpty()) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("name", name);
                        hashMap.put("category", category);
                        hashMap.put("size",size);
                        hashMap.put("price",iprice);
                        hashMap.put("items",items);
                        hashMap.put("reference",(name+category+size));

                        firestore.collection("Products").document((name+size+category+iprice)).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                        HashMap<String, Object> hashMaps = new HashMap<>();

                        hashMaps.put("price",iprice);
                        hashMaps.put("items",items);
                        hashMaps.put("cashier",usname);
                        hashMaps.put("timestamp", FieldValue.serverTimestamp());

                        firestore.collection("Stockintake").document().set(hashMaps).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                        Toast.makeText(additemActivity.this, "Successfully Added Your Stock!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(additemActivity.this, dashboardActivity.class));
                        finish();


                    }else{
                        Toast.makeText(additemActivity.this, "Cannot add this product. it already exists in Your Stock!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(additemActivity.this, dashboardActivity.class));
                        finish();


                    }



                }
                }

        });
    }
    public void onLoginClick (View view){
        startActivity(new Intent(this, dashboardActivity.class));


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







}