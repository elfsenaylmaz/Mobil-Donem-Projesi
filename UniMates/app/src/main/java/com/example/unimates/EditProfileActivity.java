package com.example.unimates;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;

    private static final int CAMERA_PERMISSION_REQUEST = 1;
    private static final int GALLERY_PERMISSION_REQUEST = 2;
    FirebaseAuth mAuth;
    Button galleryBtn, cameraBtn, saveBtn, passBtn;
    Spinner spinner;
    EditText nameField, surnameField, depField, classField, distanceField, durField, phoneField, emailField;
    StorageReference storageRef;
    FirebaseStorage storage;
    FirebaseFirestore db;
    Date date;
    ImageView profile;
    String url;
    Uri uri;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        userId = mAuth.getCurrentUser().getUid();

        profile = findViewById(R.id.profilePE);
        nameField = findViewById(R.id.nameFieldE);
        surnameField = findViewById(R.id.surnameFieldE);
        depField = findViewById(R.id.depField);
        classField = findViewById(R.id.classField);
        distanceField = findViewById(R.id.distanceField);
        durField = findViewById(R.id.durationField);
        phoneField = findViewById(R.id.phoneField);
        emailField = findViewById(R.id.emailFieldE);
        spinner = findViewById(R.id.spinner);
        galleryBtn = findViewById(R.id.galleryButtonE);
        cameraBtn = findViewById(R.id.cameraButtonE);
        saveBtn = findViewById(R.id.saveButton);
        passBtn = findViewById(R.id.passwordButton);

        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            recreate();
        }

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForCameraPermission();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { askForGalleryPermission(); }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        passBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    protected void onStart() {
        super.onStart();

        db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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

                nameField.setText(name);
                surnameField.setText(surname);
                depField.setText(dep);
                classField.setText(clas);
                distanceField.setText(dist);
                phoneField.setText(phone);
                emailField.setText(email);
                emailField.setEnabled(false);
                durField.setText(duration);
                Glide.with(EditProfileActivity.this).load(url).into(profile);

                if (status == null) {
                    spinner.setSelection(0);
                } else {
                    switch (status) {
                        case "Looking for Accommodation":
                            spinner.setSelection(1);
                            break;
                        case "Looking for Roommate":
                            spinner.setSelection(2);
                            break;
                        case "Not Searching":
                            spinner.setSelection(3);
                            break;
                        default:
                            spinner.setSelection(0);
                            break;
                    }
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_edit) {
            recreate();
        } else if (itemId == R.id.nav_list) {
            Intent intent = new Intent(EditProfileActivity.this, ListProfilesActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_map) {

        } else if (itemId == R.id.nav_matches) {
            Intent intent = new Intent(EditProfileActivity.this, MainMenuActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateProfile() {
        String name = nameField.getText().toString();
        String surname = surnameField.getText().toString();
        String phone = phoneField.getText().toString();
        String dep = depField.getText().toString();
        String clas = classField.getText().toString();
        String dist = distanceField.getText().toString();
        String duration = durField.getText().toString();
        String status = spinner.getSelectedItem().toString();

        final DocumentReference ref = db.collection("users").document(userId);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@androidx.annotation.NonNull Transaction transaction) throws FirebaseFirestoreException {
                transaction.update(ref, "name", name);
                transaction.update(ref, "surname" ,surname);
                transaction.update(ref, "department" ,dep);
                transaction.update(ref, "class" ,clas);
                transaction.update(ref, "distance" ,dist);
                transaction.update(ref, "phone" ,phone);
                transaction.update(ref, "duration" ,duration);
                transaction.update(ref, "status" ,status);
                if(url != null && !url.isEmpty()){
                    transaction.update(ref, "image", url);
                }

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditProfileActivity.this, "Profile updated.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Profile not updated.", Toast.LENGTH_SHORT).show();
                System.out.println(e.toString());
            }
        });

        Intent intent = new Intent(EditProfileActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void imagePicker(boolean i){
        if(i){
            ImagePicker.with(EditProfileActivity.this)
                    .cameraOnly()
                    .cropSquare()
                    .maxResultSize(1080, 1080)
                    .start();
        } else{
            ImagePicker.with(EditProfileActivity.this)
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
                                        Glide.with(EditProfileActivity.this).load(url).into(profile);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditProfileActivity.this, "Error getting image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}