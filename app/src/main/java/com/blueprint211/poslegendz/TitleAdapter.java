package com.blueprint211.poslegendz;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.titleviewholder>{

    Context context;
    List<Titlemodel> titleArrayList;
    List<Titlemodel> titleArrayListFull;
    List<Credit> creditList = new ArrayList<>();

    CreditAdapter myadapter;

    public TitleAdapter(Context context, List<Titlemodel> titleArrayList){

        this.titleArrayList = titleArrayList;
        this.titleArrayListFull = new ArrayList(titleArrayList);
        this.context = context;


    }


    @NonNull
    @Override
    public titleviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.title_item,parent,false);


        return new titleviewholder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull TitleAdapter.titleviewholder holder, int position) {

        Titlemodel titlemodell = titleArrayList.get(position);

        holder.name.setText(titlemodell.getName());
        holder.date.setText(titlemodell.getTimestamp()+"");
        holder.totalprice.setText(titlemodell.getTotalprice()+"");
        holder.items.setText(titlemodell.getItems()+"");
        creditList  = new ArrayList<>();
        List<Credit> creditList  = titlemodell.getTitleItems();

        myadapter = new CreditAdapter(creditList );
        holder.nested_rv.setLayoutManager(new LinearLayoutManager(context));
        holder.nested_rv.setAdapter(myadapter);
        myadapter.notifyDataSetChanged();









    }

    public void setFilter(ArrayList<Titlemodel> filterdNames) {
        this.titleArrayList = filterdNames;
        notifyDataSetChanged();
    }

    public class titleviewholder extends RecyclerView.ViewHolder {


        TextView name, date,totalprice,items;
        RecyclerView nested_rv;
        ImageButton deleteItem;
        int tprice,profit;
        float itemsale;
        String methtype,nameof,time,purpose,define;
        float quantity;
        boolean isConnected = false;
        ConnectivityManager connectivityManager;

        public titleviewholder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.number);
            date = itemView.findViewById(R.id.date);
            items = itemView.findViewById(R.id.items);
            totalprice = itemView.findViewById(R.id.totalprice);
            deleteItem = itemView.findViewById(R.id.deleteitem);
            nested_rv = itemView.findViewById(R.id.nested_rv);
            registerNetworkCallback();
            new ConnectivityManager.NetworkCallback();

            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(deleteItem.getContext());
                    View dialogView = LayoutInflater.from(deleteItem.getContext()).inflate(R.layout.activity_paybill, null);

                    alertDialog.setView(dialogView);
                    Button sell = (Button) dialogView.findViewById(R.id.cirRegisterButton);
                    Spinner dropdown = (Spinner)dialogView.findViewById(R.id.spinner);
                    String[] items = new String[]{"SELECT","CASH", "MPESA"};
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(dropdown.getContext(), android.R.layout.simple_spinner_dropdown_item, items);
                    dropdown.setAdapter(adapter);

                    AlertDialog dialog = alertDialog.create();
                    dialog.show();

                    dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            methtype = dropdown.getSelectedItem().toString();
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
                                        Savestock();
                                        Toast.makeText(TitleAdapter.titleviewholder.this.itemView.getContext(), "Bill Item Paid Successfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
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

                        private void  Savestock() {
                            nameof = titleArrayList.get(getAbsoluteAdapterPosition()).getName();
                            tprice = titleArrayList.get(getAbsoluteAdapterPosition()).getTotalprice();
                            profit = titleArrayList.get(getAbsoluteAdapterPosition()).getProfits();
                            itemsale = titleArrayList.get(getAbsoluteAdapterPosition()).getItems();
                            purpose = titleArrayList.get(getAbsoluteAdapterPosition()).getPurpose();
                            time  = titleArrayList.get(getAbsoluteAdapterPosition()).getTimestamp()+"";
                            define  = titleArrayList.get(getAbsoluteAdapterPosition()).getDefine();


                            HashMap<String, Object> hashMap = new HashMap<>();


                            hashMap.put("sales", tprice);
                            hashMap.put("profit", profit);
                            hashMap.put("paymethod", methtype);
                            hashMap.put("items", itemsale);
                            hashMap.put("purpose", purpose);
                            hashMap.put("define", define);
                            hashMap.put("timestamp", FieldValue.serverTimestamp());


                            FirebaseFirestore.getInstance().collection("Stocksold").document().set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    int position = getAbsoluteAdapterPosition();
                                    titleArrayList.remove(position);
                                    notifyDataSetChanged();
                                    notifyItemRemoved(position);
                                    FirebaseFirestore.getInstance().collection("Credits").whereEqualTo("name", nameof)
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {



                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot document = task.getResult();
                                                for (DocumentSnapshot shs : document.getDocuments()) {
                                                    shs.getReference().delete();
                                                }


                                            }
                                        }
                                    });

                                }

                            });


                        }


                    });


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
        return titleArrayList.size();
    }





}
