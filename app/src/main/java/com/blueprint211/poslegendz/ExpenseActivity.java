package com.blueprint211.poslegendz;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {

    RecyclerView Precview;
    ArrayList<expensesmodel> expenseList = new ArrayList<>();
    FirebaseFirestore firestore;
    ExpenseAdapter eadapter;
    ProgressBar spinner;
    EditText searchBox;
    TextView emptylist;
    Button addexpense;
    boolean isConnected = false;
    ConnectivityManager connectivityManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expenseview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        Precview = findViewById(R.id.precview);
        addexpense = findViewById(R.id.addButton);
        Precview.setLayoutManager(new LinearLayoutManager(this));
        eadapter = new ExpenseAdapter(this,expenseList);
        Precview.setAdapter(eadapter);
        firestore = FirebaseFirestore.getInstance();
        emptylist = findViewById(R.id.empty);

        spinner.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                spinner.setVisibility(View.INVISIBLE);

            }
        }, 2000);
        addexpense.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              makeexpense();

                                          }
                                      });
        searchBox =(EditText) findViewById(R.id.searchName);

        CollectionReference orders = firestore.collection("Expenses");
        Query expenses = orders.orderBy("timestamp", Query.Direction.DESCENDING);


        expenses.get().addOnSuccessListener(queryDocumentSnapshots -> {


            if (!queryDocumentSnapshots.isEmpty()) {
                emptylist.setVisibility(View.INVISIBLE);

                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                for (DocumentSnapshot d : list) {
                    expensesmodel ob = d.toObject(expensesmodel.class);
                    expenseList.add(ob);

                }
                eadapter.notifyDataSetChanged();

            }else{
                emptylist.setVisibility(View.VISIBLE);
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

            private void  filterQuery(String text) { ArrayList<expensesmodel> filterditems = new ArrayList<>();
                for (expensesmodel s : expenseList){
                    if (s.getName().toUpperCase().toLowerCase().contains(text) || (s.getTimestamp()+"").toUpperCase().toLowerCase().contains(text) )
                        filterditems.add(s);
                }
                eadapter.setFilter(filterditems);
            }
        });



    }

    private void  makeexpense() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(addexpense.getContext());
        View dialogView = LayoutInflater.from(addexpense.getContext()).inflate(R.layout.add_expense, null);

        alertDialog.setView(dialogView);

        EditText editprice = (EditText) dialogView.findViewById(R.id.editTextprice);
        EditText editname = (EditText) dialogView.findViewById(R.id.editTextname);
        Button expense = (Button) dialogView.findViewById(R.id.cirRegisterButton);
        Spinner dropdown = (Spinner) dialogView.findViewById(R.id.spinner);
        String[] items = new String[]{"SELECT", "PURCHASEITEM", "PAYBILL", "PAYSERVICE", "PAYSALARY"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(dropdown.getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        AlertDialog dialog = alertDialog.create();
        dialog.show();
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               String methtype = dropdown.getSelectedItem().toString();
                if (methtype.equals("SELECT")) {

                    expense.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                        }

                    });
                } else {
                    expense.setOnClickListener(new View.OnClickListener() {
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
                                int expeprice = Integer.parseInt(price);

                                HashMap<String, Object> hashMap = new HashMap<>();


                                hashMap.put("amount", expeprice);
                                hashMap.put("name", names);
                                hashMap.put("reason", methtype);
                                hashMap.put("timestamp", FieldValue.serverTimestamp() );
                                FirebaseFirestore.getInstance().collection("Expenses").document().set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<Void> task) {


                                    }
                                });
                                Toast.makeText(ExpenseActivity.this, "Expenses Added Successfully", Toast.LENGTH_SHORT).show();

                            }
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


    public void onLoginClick(View view) {
        finish();

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
