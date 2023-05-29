package com.example.unimates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText emailField, currentField, newField;
    TextView goBackTxt;
    Button changeBtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        emailField = findViewById(R.id.emailFieldC);
        currentField = findViewById(R.id.curPasswordField);
        newField = findViewById(R.id.newPasswordField);
        changeBtn = findViewById(R.id.changePasswordButton);
        goBackTxt = findViewById(R.id.goBackEditTxt);
        mAuth = FirebaseAuth.getInstance();

        goBackTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePasswordActivity.this, EditProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = emailField.getText().toString();
                String current = currentField.getText().toString();
                String newPass = newField.getText().toString();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(email, current);

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ChangePasswordActivity.this, "Password updated.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this, "Error: Password not updated.", Toast.LENGTH_SHORT).show();
                                    }
                                    Intent intent = new Intent(ChangePasswordActivity.this, EditProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Error: Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}