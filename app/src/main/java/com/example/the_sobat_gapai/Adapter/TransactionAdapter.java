package com.example.the_sobat_gapai.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.the_sobat_gapai.Model.Transaction;
import com.example.the_sobat_gapai.R;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;
    Context context;

    // Constructor
    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.tvType.setText(transaction.getType());
        holder.tvAmount.setText(String.valueOf(transaction.getAmount()));
        holder.tvBalanceAfter.setText(String.valueOf(transaction.getBalanceAfter()));
        holder.tvCreatedAt.setText(transaction.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    // ViewHolder class
    public static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView tvType, tvAmount, tvBalanceAfter, tvCreatedAt;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvBalanceAfter = itemView.findViewById(R.id.tvBalanceAfter);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
        }
    }
}