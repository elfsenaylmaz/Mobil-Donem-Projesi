package com.example.unimates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserActivity extends AppCompatActivity {
    String uID;
    TextView nameField, surnameField, depField, classField, distanceField, durField, phoneField, emailField, statusField;
    Button goBackBtn;
    ImageView profile;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        db = FirebaseFirestore.getInstance();

        profile = findViewById(R.id.profilePU);
        nameField = findViewById(R.id.nameFieldU);
        surnameField = findViewById(R.id.surnameFieldU);
        depField = findViewById(R.id.depFieldU);
        classField = findViewById(R.id.classFieldU);
        distanceField = findViewById(R.id.distanceFieldU);
        durField = findViewById(R.id.durationFieldU);
        phoneField = findViewById(R.id.phoneFieldU);
        emailField = findViewById(R.id.emailFieldU);
        statusField = findViewById(R.id.statusFieldU);
        goBackBtn = findViewById(R.id.goBackButton);

        Intent intent = getIntent();
        uID = intent.getStringExtra("uid");

        db.collection("users").document(uID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("name");
                String surname = documentSnapshot.getString("surname");
                String phone = documentSnapshot.getString("phone");
                String email = documentSnapshot.getString("email");
                String url = documentSnapshot.getString("image");
                String dep = documentSnapshot.getString("department");
                String clas = documentSnapshot.getString("class");
                String dist = documentSnapshot.getString("distance");
                String duration = documentSnapshot.getString("duration");
                String status = documentSnapshot.getString("status");

                if(name != null) {
                    nameField.setText("Name: " + name);
                }
                if(surname != null) {
                    surnameField.setText("Surname: " +surname);
                }
                if(dep != null) {
                    depField.setText("Deaprtment: " +dep);
                }
                if(clas != null) {
                    classField.setText("Class: " +clas);
                }
                if(dist != null) {
                    distanceField.setText("Distance: " +dist+ " km");
                }
                if(phone != null) {
                    phoneField.setText("Phone: " +phone);
                }
                if(email != null) {
                    emailField.setText("Email: " +email);
                }
                if(duration != null) {
                    durField.setText("Duration: " +duration + " month");
                }
                if(status != null) {
                    statusField.setText("Status: " +status);
                }

                Glide.with(UserActivity.this).load(url).into(profile);
            }
        });

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, ListProfilesActivity.class);
                startActivity(intent);
            }
        });
    }
}