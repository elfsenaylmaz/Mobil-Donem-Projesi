package com.example.unimates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Date;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST = 1;
    private static final int GALLERY_PERMISSION_REQUEST = 2;
    FirebaseAuth mAuth;
    StorageReference storageRef;
    FirebaseStorage storage;
    FirebaseFirestore db;
    Button signUpBtn, galleryBtn, cameraBtn;
    EditText nameTxt, surnameTxt, emailTxt, passwordTxt;
    TextView goBackTxt;
    HashMap<String, Object> userData;
    Date date;
    ImageView profile;
    String url = "";
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        emailTxt = findViewById(R.id.editTextTextEmailAddress2);
        passwordTxt = findViewById(R.id.editTextTextPassword);
        nameTxt = findViewById(R.id.nameField);
        surnameTxt = findViewById(R.id.surnameField);
        signUpBtn = findViewById(R.id.signUpButton2);
        goBackTxt = findViewById(R.id.goBackTxt);
        userData = new HashMap<>();
        profile = findViewById(R.id.profile);
        galleryBtn = findViewById(R.id.galleryButton);
        cameraBtn = findViewById(R.id.cameraButton);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForCameraPermission();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForGalleryPermission();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTxt.getText().toString().trim();
                String password = passwordTxt.getText().toString().trim();
                String name = nameTxt.getText().toString().trim();
                String surname = surnameTxt.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty() || url.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill in all the fields!", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String userId = mAuth.getCurrentUser().getUid();
                                        userData.put("name", name);
                                        userData.put("surname", surname);
                                        userData.put("email", email);
                                        userData.put("password", password);
                                        userData.put("image", url);

                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    db.collection("users").document(userId)
                                                            .set(userData)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(SignUpActivity.this, "User created successfully. Verification email sent.", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                                    finish();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(SignUpActivity.this, "Error creating user:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                    } else {
                                        Toast.makeText(SignUpActivity.this, "Error creating user:" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }


    private void imagePicker(boolean i){
        if(i){
            ImagePicker.with(SignUpActivity.this)
                    .cameraOnly()
                    .cropSquare()
                    .maxResultSize(1080, 1080)
                    .start();
        } else{
            ImagePicker.with(SignUpActivity.this)
                    .galleryOnly()
                    .cropSquare()
                    .maxResultSize(1080, 1080)
                    .start();
        }
    }

    private void askForCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST);
            } else {
                imagePicker(true);
            }
        } else {
            imagePicker(true);
        }
    }

    private void askForGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        GALLERY_PERMISSION_REQUEST);
            } else {
                imagePicker(false);
            }
        } else {
            imagePicker(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            uri = data.getData();
            profile.setImageURI(uri);
            addPhotoToStorage();
        } else {
            Toast.makeText(this, "Image selection cancelled.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imagePicker(true);
                } else {
                    Toast.makeText(this, "App needs your permission to proceed.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case GALLERY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imagePicker(false);
                } else {
                    Toast.makeText(this, "App needs your permission to proceed.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void addPhotoToStorage() {
        date = new Date();
        StorageReference imageRef = storageRef.child("profile_images/" + date.toString());
        imageRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUri) {
                                        url = downloadUri.toString();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUpActivity.this, "Error getting image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

