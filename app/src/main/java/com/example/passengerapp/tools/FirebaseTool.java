package com.example.passengerapp.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.lightning.models.Passenger;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.Objects;

public class FirebaseTool {
    FirebaseAuth firebaseAuth;

    public static void uploadImageAndRegister(Context context, Uri imageUri, Passenger passenger, String password) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        //get image name & extension
        StorageReference filePath = FirebaseStorage.getInstance().getReference("UserImages")
                .child(System.currentTimeMillis() + ".jpg");

        //get image url
        StorageTask uploadTask = filePath.putFile(imageUri);
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                //upload post
                Uri downloadUri = (Uri) task.getResult();
                String imgUrl = downloadUri.toString();

                passenger.setPassengerImageUrl(imgUrl);
                registerUser(context, passenger, password);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void registerUser(Context context, Passenger passenger, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(passenger.getEmail(), password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                passenger.setId(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                firebaseDatabase.getReference().child("Passengers").child(passenger.getId())
                        .setValue(passenger).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
