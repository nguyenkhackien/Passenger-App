package com.example.passengerapp.activities;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passengerapp.R;
import com.example.passengerapp.adapters.TransactionAdapter;
import com.example.passengerapp.models.Transaction;
import com.example.passengerapp.tools.Const;
import com.example.passengerapp.tools.Tool;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class WalletActivity extends AppCompatActivity {

    RelativeLayout relativeAddMoney, relativeHistory, relativeShowAddMoney, relativeShowHistory,
                    relativeBack;
    FrameLayout frameAddMoney, frameHistory;
    EditText edtAmount;
    AppCompatButton buttonOk, buttonConfirm;
    TextView textFormattedMoney, textConfirmation, textAddMoney, textHistory, textBalance;

    ProgressDialog progressDialog;

    boolean addMoneyIsChosen, historyIsChosen;

    RecyclerView recyclerTransHistory;
    TransactionAdapter transactionAdapter;
    List<Transaction> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        init();
        listener();
        calculateBalance();
    }

    private void init() {
        relativeAddMoney = findViewById(R.id.relativeAddMoney);
        relativeHistory = findViewById(R.id.relativeHistory);
        frameAddMoney = findViewById(R.id.frame_add_money_bot);
        frameHistory = findViewById(R.id.frame_history_bot);
        edtAmount = findViewById(R.id.edt_money);
        buttonOk = findViewById(R.id.buttonOk);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        textFormattedMoney = findViewById(R.id.text_formatted_money);
        relativeShowAddMoney = findViewById(R.id.relativeShowAddMoney);
        relativeShowHistory = findViewById(R.id.relativeShowHistory);
        textConfirmation = findViewById(R.id.text_confirmation);
        textAddMoney = findViewById(R.id.text_addMoney);
        textHistory = findViewById(R.id.text_history);
        relativeBack = findViewById(R.id.relative_back);
        textBalance = findViewById(R.id.text_money);
        recyclerTransHistory = findViewById(R.id.recycler_history);

        addMoneyIsChosen = true;
        historyIsChosen = false;

        progressDialog = new ProgressDialog(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerTransHistory.setHasFixedSize(true);
        recyclerTransHistory.setLayoutManager(linearLayoutManager);

        transactionList = new ArrayList<>();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        transactionAdapter = new TransactionAdapter(transactionList, this, userId);

        recyclerTransHistory.setAdapter(transactionAdapter);
    }

    private void listener() {
        relativeAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAddMoney();
            }
        });

        relativeHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistory();
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = edtAmount.getText().toString().trim();
                if (!amount.isEmpty()) {
                    textFormattedMoney.setVisibility(View.VISIBLE);
                    textConfirmation.setVisibility(View.VISIBLE);
                    buttonConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background));
                    textFormattedMoney.setText(Tool.getCurrencyFormat(Double.parseDouble(amount)));
                    Tool.hideSoftKeyboard(WalletActivity.this);
                } else {
                    Toast.makeText(WalletActivity.this, "Please enter the amount of money!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        relativeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMoneyToWallet();
            }
        });
    }

    private void addMoneyToWallet() {
        if (addMoneyIsChosen) {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            String amount = textFormattedMoney.getText().toString();
            if (!amount.isEmpty()) {
                String senderId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("L-Wallet")
                        .child("Transactions");
                String transId = databaseReference.push().getKey();

                Transaction transaction = new Transaction(
                        transId,
                        senderId,
                        null,
                        amount,
                        Calendar.getInstance().getTime().toString(),
                        Const.addMoney);

                assert transId != null;
                FirebaseDatabase.getInstance().getReference().child("L-Wallet")
                        .child("Transactions")
                        .child(transId)
                        .setValue(transaction)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Toast.makeText(WalletActivity.this, "Successful!", Toast.LENGTH_SHORT).show();
                                clearAddMoney();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(WalletActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void clearAddMoney() {
        frameAddMoney.setVisibility(View.VISIBLE);
        frameHistory.setVisibility(View.GONE);
        relativeShowAddMoney.setVisibility(View.VISIBLE);
        relativeShowHistory.setVisibility(View.GONE);
        edtAmount.setText("");
        textFormattedMoney.setText("");
        textConfirmation.setVisibility(View.GONE);
        buttonConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_grey_line));
        addMoneyIsChosen = true;
        historyIsChosen = false;

        textAddMoney.setTypeface(null, Typeface.BOLD);
        textAddMoney.setTextColor(getResources().getColor(R.color.primary_text));
        textHistory.setTypeface(null, Typeface.NORMAL);
        textHistory.setTextColor(getResources().getColor(R.color.secondary_text));
    }

    private void clearHistory() {
        frameHistory.setVisibility(View.VISIBLE);
        frameAddMoney.setVisibility(View.GONE);
        relativeShowAddMoney.setVisibility(View.GONE);
        relativeShowHistory.setVisibility(View.VISIBLE);
        edtAmount.setText("");
        textFormattedMoney.setText("");
        textConfirmation.setVisibility(View.GONE);
        buttonConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_grey_line));
        addMoneyIsChosen = false;
        historyIsChosen = true;

        textHistory.setTypeface(null, Typeface.BOLD);
        textHistory.setTextColor(getResources().getColor(R.color.primary_text));
        textAddMoney.setTypeface(null, Typeface.NORMAL);
        textAddMoney.setTextColor(getResources().getColor(R.color.secondary_text));
    }

    private void calculateBalance() {
        List<Transaction> listReceiveTrans  = new ArrayList<>();
        List<Transaction> listSendTrans = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("L-Wallet")
                .child("Transactions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listReceiveTrans.clear();
                        listSendTrans.clear();
                        transactionList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Transaction transaction = dataSnapshot.getValue(Transaction.class);
                            String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                            if (transaction != null) {
                                if (transaction.getNote().equals(Const.addMoney) && transaction.getSenderId().equals(userId)) {
                                    listReceiveTrans.add(transaction);
                                    transactionList.add(transaction);
                                } else if (transaction.getSenderId().equals(userId)) {
                                    listSendTrans.add(transaction);
                                    transactionList.add(transaction);
                                } else if (transaction.getReceiverId() != null) {
                                    if (transaction.getReceiverId().equals(userId)) {
                                        listReceiveTrans.add(transaction);
                                        transactionList.add(transaction);
                                    }
                                }
                            }
                        }

                        String totalBalance = getTotalBalance(listReceiveTrans, listSendTrans);
                        textBalance.setText(totalBalance);
                        transactionAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static String getTotalBalance(List<Transaction> listReceiveTrans, List<Transaction> listSendTrans) {
        double totalBalance = 0;
        double totalSendMoney = 0;
        double totalReceivedMoney = 0;

        for (int i = 0; i < listReceiveTrans.size(); i ++) {
            double money = Tool.getDoubleFromFormattedMoney(listReceiveTrans.get(i).getAmount());
            totalReceivedMoney += money;
        }

        for (int i = 0; i < listSendTrans.size(); i ++) {
            double money = Tool.getDoubleFromFormattedMoney(listSendTrans.get(i).getAmount());
            totalSendMoney += money;
        }

        totalBalance = totalReceivedMoney - totalSendMoney;

        return Tool.getCurrencyFormat(totalBalance);
    }
}