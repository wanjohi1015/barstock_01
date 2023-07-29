package com.blueprint211.poslegendz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.CaseMap;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    DocumentId id;

    ImageView back;
    RecyclerView recyclerView;
    CartAdapter cAdapter;
    TextView displaytotalprice;
    int totalprice = 0;
    int tprice, total;
    float quantity,quantities, items;
    String title,category,size;

    int custsales,itemsale,profit,profits;
    ProgressBar spinner;


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String time  = dateFormat.format(new Date());


    Button checkOut;
    AlertDialog dialog;
    EditText name;
    List<Cart> cartList = new ArrayList<Cart>();
    List<Integer> savetotalprice = new ArrayList<>();
    List<Float> listquantities = new ArrayList<>();
    List<Integer> totcustsales = new ArrayList<>();
    List<Integer> profittotal = new ArrayList<>();
    String methtype,purpose,hint;
    boolean isConnected = false;
    ConnectivityManager connectivityManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);



        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        firestore = FirebaseFirestore.getInstance();
        back = findViewById(R.id.back);
        displaytotalprice = findViewById(R.id.totalPriceordercart);
        checkOut =(Button) findViewById(R.id.buttoncheckout);
        recyclerView = findViewById(R.id.recyclerviewcart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cAdapter = new CartAdapter(this,cartList);
        recyclerView.setAdapter(cAdapter);








        CollectionReference orders = firestore.collection("Cart");
        Query cart = orders.orderBy("refname", Query.Direction.ASCENDING);


        cart.get().addOnSuccessListener(queryDocumentSnapshots -> {


            if (!queryDocumentSnapshots.isEmpty()) {


                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();


                for (DocumentSnapshot d : list) {
                    Cart obj = d.toObject(Cart.class);

                    assert obj != null;
                    total = obj.getSales();
                    title = obj.getRefname();
                    quantity = obj.getItems();
                    profit = obj.getProfit();
                    hint = obj.getCategory();
                    purpose = obj.getPurpose();
                    itemsale = obj.getItemsale();
                    cartList.add(obj);
                    listquantities.add(quantity);
                    totcustsales.add(total);

                    savetotalprice.add(total);
                    profittotal.add(profit);



                    for (int i = 0; i < savetotalprice.size(); i++) {
                        totalprice += Integer.parseInt(String.valueOf(savetotalprice.get(i)));

                        displaytotalprice.setText(String.valueOf(totalprice));


                    }

                    savetotalprice.clear();

                    for (float i = 0; i < listquantities.size(); i++) {
                        quantities += Float.parseFloat(String.valueOf(listquantities.get((int) i)));
                    }
                    listquantities.clear();
                    for (int i = 0; i < totcustsales.size(); i++) {
                        custsales += Integer.parseInt(String.valueOf(totcustsales.get(i)));
                    }
                    totcustsales.clear();
                    for (int i = 0; i < profittotal.size(); i++) {
                        profits += Integer.parseInt(String.valueOf(profittotal.get(i)));
                    }
                    profittotal.clear();
                }
                cAdapter.notifyDataSetChanged();




            }

        });


        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Completetransaction();


            }

        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void  Completetransaction() {


         AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(checkOut.getContext());
        LayoutInflater inflater = getLayoutInflater();
         View dialogView = inflater.inflate(R.layout.activity_placecart, null);

        dialogBuilder.setView(dialogView);



        name = (EditText) dialogView.findViewById(R.id.editname);
       Button post = (Button) dialogView.findViewById(R.id.cirRegisterButton);
        Spinner dropdown = (Spinner)dialogView.findViewById(R.id.spinner);
        String[] items = new String[]{"ONBILL","CASH", "MPESA"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        AlertDialog dialog = dialogBuilder.create();


        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                methtype = dropdown.getSelectedItem().toString();
                if (methtype.equals("ONBILL")) {
                    name.setEnabled(true);
                    post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isConnected) {


                            String custname = name.getText().toString();


                            if (custname.isEmpty()) {


                                name.setError("Enter Customer Details");
                            }else {
                                dialog.dismiss();
                                HashMap<String, Object> hhashMap = new HashMap<>();
                                hhashMap.put("titleitems", cartList);
                                hhashMap.put("totalprice", totalprice);
                                hhashMap.put("items", quantities);
                                hhashMap.put("profit", profits);
                                hhashMap.put("name", custname);
                                hhashMap.put("purpose", hint);
                                hhashMap.put("define", purpose);
                                hhashMap.put("timestamp", FieldValue.serverTimestamp());

                                firestore.collection("Credits").document().set(hhashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {


                                        firestore.collection("Cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                                                if (task.isSuccessful()) {

                                                    QuerySnapshot tasks = task.getResult();

                                                    assert tasks != null;
                                                    for (DocumentSnapshot ds : tasks.getDocuments()) {


                                                        ds.getReference().delete();
                                                    }


                                                }
                                            }
                                        });
                                        Toast.makeText(CartActivity.this, " Bill items added successfully!", Toast.LENGTH_SHORT).show();
                                        Intent mySuperIntent = new Intent(CartActivity.this, dashboardActivity.class);
                                        startActivity(mySuperIntent);


                                    }
                                });



                            }

                            }else{
                                startActivity(new Intent(getApplicationContext(), noInternetActivity.class));
                            }



                        }

                    });
                } else {
                    name.setEnabled(false);
                    post.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isConnected) {
                                dialog.dismiss();
                            Savestock();
                            Toast.makeText(CartActivity.this, " Items sold recorded successfully!", Toast.LENGTH_SHORT).show();
                            Intent mySuperIntent = new Intent(CartActivity.this, dashboardActivity.class);
                            startActivity(mySuperIntent);
                            }else{
                                startActivity(new Intent(getApplicationContext(), noInternetActivity.class));
                            }



                        }

                    });

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }

        });





        dialog.show();




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


    private void  Savestock() {




                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sales", totalprice);
                    hashMap.put("items", quantities);
                    hashMap.put("profit", profits);
                    hashMap.put("paymethod", methtype);
                    hashMap.put("purpose", hint);
                    hashMap.put("define", purpose);
                    hashMap.put("timestamp", FieldValue.serverTimestamp());


                    firestore.collection("Stocksold").document().set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            firestore.collection("Cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {


                                    if (task.isSuccessful()) {

                                        QuerySnapshot tasks = task.getResult();

                                        assert tasks != null;
                                        for (DocumentSnapshot ds : tasks.getDocuments()) {


                                            ds.getReference().delete();
                                        }


                                    }
                                }
                            });
                        }
                    });




    }

    @Override
    public void onBackPressed() {

        finish();

    }
    public void onLoginClick (View view){
        startActivity(new Intent(this, dashboardActivity.class));


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
