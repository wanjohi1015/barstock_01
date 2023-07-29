package com.blueprint211.poslegendz;

import static com.google.android.gms.common.util.CollectionUtils.mapOf;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.CreditHolder> {

    List<Credit> creditList;

    public CreditAdapter(List<Credit> creditList) {
        this.creditList = creditList;


    }

    @NonNull
    @Override
    public CreditAdapter.CreditHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_row, parent, false);

        return new CreditHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditAdapter.CreditHolder holder, int position) {
        holder.refname.setText(creditList.get(position).getRefname());
        holder.items.setText(creditList.get(position).getItems() + "");
        holder.price.setText(creditList.get(position).getSales() + "");



    }

    @Override
    public int getItemCount() {
        if (creditList == null) {

            return 0;


        } else {
            return creditList.size();
        }

    }


    class CreditHolder extends RecyclerView.ViewHolder {


        TextView refname;
        TextView price;
        TextView items;



        public CreditHolder(@NonNull View itemView) {
            super(itemView);

            refname = (TextView)  itemView.findViewById(R.id.name);
            items = (TextView) itemView.findViewById(R.id.quantity);
            price = (TextView) itemView.findViewById(R.id.price);




        }
    }




}
