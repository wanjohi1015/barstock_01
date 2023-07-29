package com.blueprint211.poslegendz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.Network;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
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
import java.util.Objects;

public class KegAdapter extends RecyclerView.Adapter<KegAdapter.KegHolder>{
    String donetime ="Currently on Salee";
    Context context;
    List<keg> keglist;
    public KegAdapter(Context context, List<keg> keglist){
        this.context = context;
        this. keglist = keglist;

    }

    @NonNull
    @Override
    public KegAdapter.KegHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new KegAdapter.KegHolder(LayoutInflater.from(context).inflate(R.layout.keg_style, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull KegAdapter.KegHolder holder, int position) {
        holder.name.setText(keglist.get(position).getName());
        holder.bprice.setText(keglist.get(position).getBuyprice()+"");
        holder.buytime.setText(keglist.get(position).getBoughttime()+"");
        holder.fintime.setText(keglist.get(position).getDonetime()+"");
        if((keglist.get(holder.getAbsoluteAdapterPosition()).getDonetime()+"").equalsIgnoreCase("On Sale Period")){

        }else{
            holder.cardView.setBackgroundColor(Color.parseColor("#805300"));
        }




        holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((keglist.get(holder.getAbsoluteAdapterPosition()).getDonetime()+"").equalsIgnoreCase("On Sale Period")){

                        DocumentReference docRef =  FirebaseFirestore.getInstance().collection("Cart").document("Keg Beer");
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        holder.itemView.setOnClickListener(null);
                                        Toast.makeText(holder.itemView.getContext(), "Keg Item already in your Cart,you can only sell one item type at once", Toast.LENGTH_SHORT).show();
                                    } else {
                                        holder.sellproduct();
                                    }
                                }
                            }

                        });
                    } else {
                        holder.itemView.setOnClickListener(null);
                        holder.itemView.setOnLongClickListener(Objects::isNull);
                        Toast.makeText(holder.itemView.getContext(), "Keg Barrel is Sold out!", Toast.LENGTH_SHORT).show();
                }

                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    holder.setdonesell();
                    return true;
                }
            });




    }




    class KegHolder extends RecyclerView.ViewHolder {
         TextView name,bprice,buytime,fintime;
        CardView cardView;
         Button repo ;
         boolean isConnected = false;
         ConnectivityManager connectivityManager;

         public KegHolder(@NonNull View itemView) {
             super(itemView);
             cardView =itemView.findViewById(R.id.card);
             name =itemView.findViewById(R.id.name);
             bprice =itemView.findViewById(R.id.bprice);
             buytime =itemView.findViewById(R.id.bat);
             fintime =itemView.findViewById(R.id.fat);
             repo = itemView.findViewById(R.id.salesrepo);
             registerNetworkCallback();
             new ConnectivityManager.NetworkCallback();

             repo.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if(isConnected) {
                         showrepo();
                     }else{
                         context. startActivity(new Intent(context, noInternetActivity.class));
                     }

                 }
             });

         }

         private void  showrepo() {


             AlertDialog.Builder alertDialog = new AlertDialog.Builder(repo.getContext());
             View dialogView = LayoutInflater.from(repo.getContext()).inflate(R.layout.activity_kegrepo,null);
             alertDialog.setView(dialogView);
             AlertDialog dialog = alertDialog.create();
             String ref = keglist.get(getAbsoluteAdapterPosition()).getBarrelref();
             TextView barreltype = dialogView.findViewById(R.id.barrelview);
             TextView sales = dialogView.findViewById(R.id.salesview);
             ImageView cancel = dialogView.findViewById(R.id.btn_cancel);
             ArrayList<Integer> totbarsales = new ArrayList<>();
             FirebaseFirestore.getInstance().collection("Stocksold").whereEqualTo("purpose", ref).addSnapshotListener(new EventListener<QuerySnapshot>() {
                 @Override
                 public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                     int totsales =0 ;
                     assert value != null;
                     for (DocumentSnapshot shs : value.getDocuments()) {


                             soldstock kegz = shs.toObject(soldstock.class);
                             assert kegz != null;
                             String barname = kegz.getPurpose();
                             barreltype.setText(String.valueOf(barname));
                             int barsales = kegz.getSales();
                             totbarsales.add(barsales);

                             for (int i = 0; i < totbarsales.size(); i++) {
                                 totsales += Integer.parseInt(String.valueOf(totbarsales.get(i)));

                             }
                         sales.setText(String.valueOf(totsales));
                             totsales = 0;

                         }
                     totbarsales.clear();
                     }

             });
             dialog.show();
             cancel.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     dialog.dismiss();


                 }

             });
         }

         public void  sellproduct() {
             EditText editprice, edititems;
             AlertDialog.Builder alertDialog = new AlertDialog.Builder(itemView.getContext());
             View dialogView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.activity_sellbarrelitem, null);

             alertDialog.setView(dialogView);

             editprice = (EditText) dialogView.findViewById(R.id.editTextprice);
             edititems = (EditText) dialogView.findViewById(R.id.editTextitems);
             Button sell = (Button) dialogView.findViewById(R.id.cirRegisterButton);
             Spinner dropdown = (Spinner) dialogView.findViewById(R.id.spinner);
             String[] items = new String[]{"SELECT", "SMALLCUP", "BIGCUP", "JUG"};
             ArrayAdapter<String> adapter = new ArrayAdapter<>(dropdown.getContext(), android.R.layout.simple_spinner_dropdown_item, items);
             dropdown.setAdapter(adapter);

             AlertDialog dialog = alertDialog.create();
             dialog.show();
             String sec = keglist.get(getAbsoluteAdapterPosition()).getBarrelref();
             String ref = keglist.get(getAbsoluteAdapterPosition()).getName();
             dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                 @Override
                 public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                     String methtype = dropdown.getSelectedItem().toString();
                     if (methtype.equals("SELECT")) {

                         sell.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {


                             }

                         });
                     } else {
                         sell.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 if(isConnected) {


                                 String price = (editprice.getText().toString());
                                 String items = (edititems.getText().toString());
                                 if (price.isEmpty()) {


                                     editprice.setError("Enter selling price of One item");
                                 } else if (items.isEmpty()) {

                                     edititems.setError("Enter number of items");


                                 } else {
                                     dialog.dismiss();
                                     int expeprice = Integer.parseInt(price);
                                     int ofitems = Integer.parseInt(items);
                                     int totprices = (ofitems * expeprice);
                                     float foritems = Float.parseFloat(items);
                                     String beer = "Keg Beer";

                                     HashMap<String, Object> hashMap = new HashMap<>();


                                     hashMap.put("sales", totprices);
                                     hashMap.put("items", foritems);
                                     hashMap.put("itemsale",expeprice);
                                     hashMap.put("category", sec);
                                     hashMap.put("purpose", beer);
                                     hashMap.put("refname", methtype+ref );
                                     hashMap.put("size", methtype);
                                     hashMap.put("profit", 0);


                                     FirebaseFirestore.getInstance().collection("Cart").document(beer).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<Void> task) {
                                             Toast.makeText(KegHolder.this.itemView.getContext(), "Keg Item sales Added Successfully", Toast.LENGTH_SHORT).show();
                                         }
                                     });


                                 }
                                 }else{
                                     context. startActivity(new Intent(context, noInternetActivity.class));
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
        private void  setdonesell() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time  = dateFormat.format(new Date());
             Button endsale,cancel;
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(itemView.getContext());
            View dialogView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.activity_stopsale, null);
            endsale= (Button)dialogView.findViewById(R.id.endButton);
            cancel =  (Button)dialogView.findViewById(R.id.cancelButton);

            alertDialog.setView(dialogView);
            AlertDialog dialog = alertDialog.create();
            dialog.show();
            String title = keglist.get(getAbsoluteAdapterPosition()).getBarrelref();
            endsale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isConnected) {
                        dialog.dismiss();
                        FirebaseFirestore.getInstance().collection("Barrels").whereEqualTo("barrelref", title).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot document = task.getResult();
                                    for (DocumentSnapshot shs : document.getDocuments()) {
                                        keg barrels = shs.toObject(keg.class);
                                        assert barrels != null;
                                        shs.getReference().update("donetime", time);


                                    }
                                }
                            }
                        });
                    }else{
                        context. startActivity(new Intent(context, noInternetActivity.class));
                    }

                }
                });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
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
     }


    @Override
    public int getItemCount() {
        return keglist.size();
    }
}
