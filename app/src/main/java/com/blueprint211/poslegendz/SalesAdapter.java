package com.blueprint211.poslegendz;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import android.os.Handler;


public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.SalesHolder>{

    Context ctx;
    List <Products> productsList;
    List <Products> productsListFull;


    public SalesAdapter (Context ctx,List <Products> productsList){
        this.ctx = ctx;
        this.productsList = productsList;
        this.productsListFull = new ArrayList(productsList);

    }


    @NonNull
    @Override
    public SalesAdapter.SalesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SalesAdapter.SalesHolder(LayoutInflater.from(ctx).inflate(R.layout.item_sale, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SalesAdapter.SalesHolder holder, int position) {
        holder.name.setText(productsList.get(position).getName());
        holder.category.setText(productsList.get(position).getCategory());
        holder.quantity.setText(productsList.get(position).getItems()+"");
        holder.reference.setText(productsList.get(position).getReference());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.sellproduct();
            }
        });

    }
    class SalesHolder extends RecyclerView.ViewHolder
    {
        TextView name,category,quantity,reference;
        ImageButton addItem;
        ImageButton minitem ;
        FirebaseUser user;
        FirebaseAuth auth;
        String userid,ofusername,methtype;
        int totalamount;
        boolean isConnected = false;
        ConnectivityManager connectivityManager;



        public SalesHolder(@NonNull View itemView) {
            super(itemView);
            auth = FirebaseAuth.getInstance();
            name =itemView.findViewById(R.id.name);
            category =itemView.findViewById(R.id.category);
            quantity =itemView.findViewById(R.id.quantity);
            reference =itemView.findViewById(R.id.rf);
            addItem = itemView.findViewById(R.id.additem);
            minitem = itemView.findViewById(R.id.minitem);
            registerNetworkCallback();
            new ConnectivityManager.NetworkCallback();

            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    managestock();

                }
            });
            minitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    minusstock();

                }
            });

            user = auth.getCurrentUser();
            assert user != null;
            userid = user.getUid();
            FirebaseFirestore.getInstance().collection("Users").whereEqualTo("userid", userid).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                    assert value != null;
                    for (DocumentSnapshot mu : value.getDocuments()) {
                        Users username = mu.toObject(Users.class);
                        assert username != null;
                        ofusername = username.getName();



                    }
                }



            });






        }
        private void registerNetworkCallback(){


            try {

                connectivityManager = (ConnectivityManager) itemView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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

        private void  minusstock() {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(minitem.getContext());
            View dialogView = LayoutInflater.from(minitem.getContext()).inflate(R.layout.activity_minitem,null);
            alertDialog.setView(dialogView);
            AlertDialog dialog = alertDialog.create();
            EditText edititems = (EditText) dialogView.findViewById(R.id.editTextitems);
            EditText reason = (EditText) dialogView.findViewById(R.id.editreasons);
            Button sell = (Button) dialogView.findViewById(R.id.cirRegisterButton);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy ");
            String time  = dateFormat.format(new Date());
            String ref = reference.getText().toString();

            sell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isConnected) {
                    deleteitem();
                    }else{
                        ctx. startActivity(new Intent(ctx, noInternetActivity.class));
                    }

                }

                private void deleteitem() {
                    String explain = (reason.getText().toString());
                    String items = (edititems.getText().toString());
                    if (explain.isEmpty()) {


                        reason.setError("Enter reason for deleting item");
                    } else if (items.isEmpty()) {

                        edititems.setError("Enter number of items");


                    } else {
                        dialog.dismiss();
                        float foritems = Float.parseFloat(items);
                        FirebaseFirestore.getInstance().collection("Products").whereEqualTo("reference", ref).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot document = task.getResult();
                                    for (DocumentSnapshot shs : document.getDocuments()) {
                                        Products products = shs.toObject(Products.class);
                                        assert products != null;
                                        float numitems = products.getItems();
                                        int amount = products.getPrice();
                                        float totalitems = (numitems - foritems);
                                        float ofamount = Float.parseFloat(String.valueOf(amount));
                                        float oftotalamt = (ofamount * foritems);
                                         totalamount = (int)oftotalamt;
                                        shs.getReference().update("items", totalitems);
                                        ((sellitemActivity) itemView.getContext()).refreshActivtiy();

                                    }
                                }
                            }
                        });
                        FirebaseFirestore.getInstance().collection("Losses").whereEqualTo("ref", time + ref)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot document = task.getResult();

                                    if (document.isEmpty()) {

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("ref", time + ref);
                                        hashMap.put("name", ref);
                                        hashMap.put("reason", explain);
                                        hashMap.put("amount",totalamount );
                                        hashMap.put("timestamp", FieldValue.serverTimestamp());
                                        FirebaseFirestore.getInstance().collection("Losses").document(time + ref).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<Void> task) {


                                            }
                                        });
                                        Toast.makeText(SalesHolder.this.itemView.getContext(), "Item added to Expenses List Successfully!", Toast.LENGTH_SHORT).show();

                                    }
                                }

                            }
                        });


                    }
                }
            });

            dialog.show();
        }

        private void managestock() {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(addItem.getContext());
            View dialogView = LayoutInflater.from(addItem.getContext()).inflate(R.layout.manage_stock, null);

            alertDialog.setView(dialogView);
            EditText itemsadd = (EditText) dialogView.findViewById(R.id.edititems);
            EditText priceadd = (EditText) dialogView.findViewById(R.id.editprice);
            Button add = (Button) dialogView.findViewById(R.id.addButton);
            AlertDialog dialog = alertDialog.create();
            String itemref = reference.getText().toString();
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isConnected) {
                        increaseitem();
                    }else{
                        ctx. startActivity(new Intent(ctx, noInternetActivity.class));
                    }
                }

                private void increaseitem() {
                    String itemsadded = (itemsadd.getText().toString());
                    String priceadded = (priceadd.getText().toString());
                    if (itemsadded .isEmpty()) {


                        itemsadd.setError("Enter number of items to add to your stock");
                    }else if(priceadded .isEmpty()){
                        priceadd.setError("Enter price of items to add to your stock");
                    }
                    else {
                        dialog.dismiss();
                        float ofaddeditems = Float.parseFloat(itemsadded);
                        float ofaddedprice = Float.parseFloat(priceadded);
                        int priceofadded = (int)ofaddedprice;
                        FirebaseFirestore.getInstance().collection("Products").whereEqualTo("reference", itemref).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot document = task.getResult();
                                    for (DocumentSnapshot shs : document.getDocuments()) {
                                        Products products = shs.toObject(Products.class);
                                        assert products != null;
                                        float numitems = products.getItems();
                                        float totalitems = (numitems + ofaddeditems);
                                        shs.getReference().update("items", totalitems);
                                        ((sellitemActivity) itemView.getContext()).refreshActivtiy();
                                        HashMap<String, Object> hashMaps = new HashMap<>();

                                        hashMaps.put("price",priceofadded);
                                        hashMaps.put("items",ofaddeditems);
                                        hashMaps.put("cashier",ofusername);
                                        hashMaps.put("timestamp", FieldValue.serverTimestamp());

                                        FirebaseFirestore.getInstance().collection("Stockintake").document().set(hashMaps).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<Void> task) {

                                            }
                                        });
                                        Toast.makeText(SalesHolder.this.itemView.getContext(), "Successfully Updated Your Stock!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });


                    }
                }
            });
            dialog.show();


        }




        private void  sellproduct() {
            EditText editprice;
            EditText edititems;
            final EditText[] reason = new EditText[1];
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(itemView.getContext());
            View dialogView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.activity_sellitem, null);

            alertDialog.setView(dialogView);

             editprice = (EditText) dialogView.findViewById(R.id.editTextprice);
             edititems = (EditText) dialogView.findViewById(R.id.editTextitems);
            Button sell = (Button) dialogView.findViewById(R.id.cirRegisterButton);

            AlertDialog dialog = alertDialog.create();
            dialog.show();

            String ref = reference.getText().toString();





                        sell.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(isConnected) {
                                    sellitem();
                                }else{
                                    ctx. startActivity(new Intent(ctx, noInternetActivity.class));
                                }

                            }

                            private void sellitem() {
                                String price = (editprice.getText().toString());
                                String items = (edititems.getText().toString());
                                if (price.isEmpty()) {


                                    editprice.setError("Enter selling price of One item");
                                } else if (items.isEmpty()) {

                                    edititems.setError("Enter number of items");


                                } else {
                                    dialog.dismiss();

                                    float ofitems = Float.parseFloat(items);
                                    float ofprice = Float.parseFloat(price);
                                    float totalpricee = (ofitems * ofprice);
                                    int itemprice = (int) ofprice;
                                    int totprice = (int) totalpricee;


                                    FirebaseFirestore.getInstance().collection("Products").whereEqualTo("reference", ref).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot document = task.getResult();
                                                for (DocumentSnapshot shs : document.getDocuments()) {
                                                    Products products = shs.toObject(Products.class);
                                                    assert products != null;
                                                    float numitems = products.getItems();
                                                    String referencee = products.getReference();
                                                    String category = products.getCategory();
                                                    String size = products.getSize();
                                                    int prixe = products.getPrice();
                                                    float prize = Float.parseFloat(String.valueOf(prixe));
                                                    float profitss = (ofprice - prize) * ofitems;
                                                    int profit = (int) profitss;
                                                    float totalitems = (numitems - ofitems);
                                                    shs.getReference().update("items", totalitems);
                                                    ((sellitemActivity) itemView.getContext()).refreshActivtiy();


                                                    FirebaseFirestore.getInstance().collection("Cart").whereEqualTo("refname", referencee)
                                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {


                                                        @Override
                                                        public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                QuerySnapshot document = task.getResult();
                                                                String hint = "Bottled drinks";

                                                                if (document.isEmpty()) {

                                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                                    hashMap.put("refname", referencee);
                                                                    hashMap.put("category", category);
                                                                    hashMap.put("size", size);
                                                                    hashMap.put("purpose", hint);
                                                                    hashMap.put("items", ofitems);
                                                                    hashMap.put("itemsale", itemprice);
                                                                    hashMap.put("sales", totprice);
                                                                    hashMap.put("profit", profit);


                                                                    FirebaseFirestore.getInstance().collection("Cart").document(referencee).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<Void> task) {


                                                                        }
                                                                    });
                                                                    Toast.makeText(SalesHolder.this.itemView.getContext(), "Item added to Cart Successfully!", Toast.LENGTH_SHORT).show();


                                                                } else {
                                                                    Toast.makeText(SalesHolder.this.itemView.getContext(), "The Item is already added to Cart, delete it from Cart in order to add again!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }
                                                    });


                                                }

                                            }
                                        }
                                    });

                                }
                            }
                        });

                    }







        }




    @Override
    public int getItemCount() {
        return productsList.size();
    }
    public void setFilter(List<Products> filterdNames) {
        this.productsList = filterdNames;
        notifyDataSetChanged();
    }

}
