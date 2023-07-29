package com.blueprint211.poslegendz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class sellitemActivity extends AppCompatActivity {

    RecyclerView Precview;
    ArrayList<Products> productsList = new ArrayList<>();
    ArrayList<Cart> cartList = new ArrayList<>();
    FirebaseFirestore firestore;
    SalesAdapter salesadapter;
    TextView quantityincart;
    float sum=0;
    FloatingActionButton fab;
    List<Float> sumitem = new ArrayList<>();
    ProgressBar spinner;
    EditText searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.precview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        Precview = findViewById(R.id.precview);

        quantityincart = findViewById(R.id.cartquantity);
        fab = findViewById(R.id.cart);
        Precview.setLayoutManager(new LinearLayoutManager(this));
        salesadapter = new SalesAdapter (this, productsList);
        Precview.setAdapter(salesadapter);
        firestore = FirebaseFirestore.getInstance();

        spinner.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                spinner.setVisibility(View.INVISIBLE);

            }
        }, 2000);
        searchBox =(EditText) findViewById(R.id.searchName);
        if (cartList.isEmpty()){
             sum=0;
        }


        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       float ordered = Float.parseFloat((String) quantityincart.getText());
                                       if (ordered == 0) {
                                           Toast.makeText(sellitemActivity.this, "Add Products To Your Cart List To Proceed", Toast.LENGTH_SHORT).show();
                                       } else {
                                           startActivity(new Intent(sellitemActivity.this, CartActivity.class));
                                       }
                                   }
                               });


        firestore.collection("Cart").addSnapshotListener(new EventListener<QuerySnapshot>()
        {

                                                             @Override
                                                             public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                                                                if( value != null) {

                                                                    for (DocumentSnapshot dsn : value.getDocuments()) {


                                                                        Cart cart = dsn.toObject(Cart.class);
                                                                        assert cart != null;


                                                                        float items = cart.getItems();



                                                                        sumitem.add(items);
                                                                        for (float i = 0; i < sumitem.size(); i++) {

                                                                            sum += Float.parseFloat(String.valueOf(sumitem.get((int) i)));

                                                                        }
                                                                        quantityincart.setText(String.valueOf(sum));
                                                                        sum = 0;


                                                                    }
                                                                    sumitem.clear();
                                                                }else{
                                                                    sum = 0;
                                                                }


                                                             }

                                                         });



        CollectionReference orders = firestore.collection("Products");
        Query products = orders.orderBy("name", Query.Direction.ASCENDING);


        products.get().addOnSuccessListener(queryDocumentSnapshots -> {


            if (!queryDocumentSnapshots.isEmpty()) {

                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                for (DocumentSnapshot d : list) {
                    Products ob = d.toObject(Products.class);
                    productsList.add(ob);

                }
                salesadapter.notifyDataSetChanged();



            }

        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterQuery(s.toString());
            }

            private void  filterQuery(String text) { ArrayList<Products> filterdNames = new ArrayList<>();
                for (Products s : productsList){
                    if (s.getName().toUpperCase().toLowerCase().contains(text) ) {
                        filterdNames.add(s);
                    }
                }
                salesadapter.setFilter(filterdNames);
            }
        });










    }

    public void onLoginClick (View view){
        startActivity(new Intent(this, dashboardActivity.class));


    }
    @Override
    public void onBackPressed() {

        finish();
        Intent intent = new Intent(sellitemActivity.this, dashboardActivity.class);
        startActivity(intent);
    }


    public void  refreshActivtiy() {


        spinner.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                recreate();


            }
        }, 1000);
        startActivity(new Intent(this, sellitemActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        sum = 0;

    }
}
