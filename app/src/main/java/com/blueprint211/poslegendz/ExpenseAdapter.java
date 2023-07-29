package com.blueprint211.poslegendz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseHolder>{
    Context context;
    List<expensesmodel> expenseList;
    public ExpenseAdapter(Context context,List<expensesmodel> expenseList){
        this.context = context;
        this.expenseList = expenseList;

    }
    @NonNull
    @Override
    public ExpenseAdapter.ExpenseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExpenseAdapter.ExpenseHolder(LayoutInflater.from(context).inflate(R.layout.expense_style, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ExpenseHolder holder, int position) {
        holder.name.setText(expenseList.get(position).getName());
        holder.reason.setText(expenseList.get(position).getReason());
        holder.amount.setText(expenseList.get(position).getAmount()+"");
        holder.time.setText(expenseList.get(position).getTimestamp()+"");

    }

    public void  setFilter(ArrayList<expensesmodel> filterditems) {
        this.expenseList = filterditems;
        notifyDataSetChanged();
    }

    class ExpenseHolder extends RecyclerView.ViewHolder
    {
        TextView name,reason,amount,time;

        public ExpenseHolder(@NonNull View itemView) {
            super(itemView);
            name =itemView.findViewById(R.id.name);
            reason =itemView.findViewById(R.id.reason);
            amount =itemView.findViewById(R.id.amount);
            time =itemView.findViewById(R.id.time);
        }
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }
}
