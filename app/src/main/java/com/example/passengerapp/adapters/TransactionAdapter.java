package com.example.passengerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passengerapp.R;
import com.example.passengerapp.models.Transaction;
import com.example.passengerapp.tools.Const;
import com.example.passengerapp.tools.Tool;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>{

    List<Transaction> listTrans;
    Context context;
    String userId;

    public TransactionAdapter(List<Transaction> listTrans, Context context, String userId) {
        this.listTrans = listTrans;
        this.context = context;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_transaction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = listTrans.get(position);

        if (transaction.getNote().equals(Const.addMoney)) {
            holder.imgArrow.setImageResource(R.drawable.down_arrow);
            holder.textNote.setText("Added money for L-Wallet");
            holder.textMoney.setText("+" + transaction.getAmount());
        } else {
            if (transaction.getSenderId().equals(userId)) {
                holder.imgArrow.setImageResource(R.drawable.up_arrow);
                holder.textNote.setText("Pay for driver");
                holder.textMoney.setText("-" + transaction.getAmount());
            } else if (transaction.getReceiverId().equals(userId)) {
                holder.imgArrow.setImageResource(R.drawable.down_arrow);
                holder.textNote.setText("Received money from passenger");
                holder.textMoney.setText("+" + transaction.getAmount());
            }
        }

        holder.textTime.setText(Tool.getShortTime(transaction.getTime()));
    }

    @Override
    public int getItemCount() {
        return listTrans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imgArrow;
        TextView textNote, textTime, textMoney;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgArrow = itemView.findViewById(R.id.img_arrow);
            textNote = itemView.findViewById(R.id.text_trans_note);
            textTime = itemView.findViewById(R.id.text_time);
            textMoney = itemView.findViewById(R.id.text_money);
        }
    }
}
