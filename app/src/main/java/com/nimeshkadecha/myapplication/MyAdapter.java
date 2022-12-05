package com.nimeshkadecha.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context context;
    private ArrayList item, price, quantity, subtotal;

    public MyAdapter(Context context, ArrayList item, ArrayList price, ArrayList quantity, ArrayList subtotal) {

        this.context = context;
        this.item = item;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.userentry, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        holder.index.setText(String.valueOf(index.get(position)));
        holder.item.setText(String.valueOf(item.get(position)));
        holder.price.setText(String.valueOf(price.get(position)));
        holder.quantity.setText(String.valueOf(quantity.get(position)));
        holder.subtotal.setText(String.valueOf(subtotal.get(position)));
    }

    @Override
    public int getItemCount() {
        return subtotal.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView item, price, quantity, subtotal;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
//            index = itemView.findViewById(R.id.textindex);
            item = itemView.findViewById(R.id.textitem);
            price = itemView.findViewById(R.id.textprice);
            quantity = itemView.findViewById(R.id.textquantity);
            subtotal = itemView.findViewById(R.id.textsubtotal);

        }
    }
}
