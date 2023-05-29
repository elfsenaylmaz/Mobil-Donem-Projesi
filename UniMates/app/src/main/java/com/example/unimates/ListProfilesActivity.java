package com.example.unimates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListProfilesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    EditText durMin, durMax, distMin, distMax;
    Button filterBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_profiles);
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setPadding(0, 0, 0, 0);

        recyclerView = findViewById(R.id.recyclerView);
        distMin = findViewById(R.id.distMin);
        distMax = findViewById(R.id.distMax);
        durMin = findViewById(R.id.durMin);
        durMax = findViewById(R.id.durMax);
        filterBtn = findViewById(R.id.filter);

        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_list_profiles);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            recreate();
        }

        CollectionReference newColRef = db.collection("users");
        newColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    ArrayList<User> users = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {

                        String id = documentSnapshot.getId();
                        String name = documentSnapshot.getString("name");
                        String url = documentSnapshot.getString("image");
                        String surname = documentSnapshot.getString("surname");
                        String phone = documentSnapshot.getString("phone");
                        String email = documentSnapshot.getString("email");
                        String distance  = documentSnapshot.getString("distance");
                        String duration  = documentSnapshot.getString("duration");
                        String clas = documentSnapshot.getString("class");
                        String status = documentSnapshot.getString("status");
                        String department = documentSnapshot.getString("department");

                        if(!mAuth.getCurrentUser().getUid().equals(id)) {
                            User u = new User(email, name, surname, department, distance, clas, status, duration, url, id, phone);
                            users.add(u);
                        }
                    }
                    recyclerView.setAdapter(new ListProfilesAdapter(ListProfilesActivity.this, users));
                } else {
                    Toast.makeText(ListProfilesActivity.this, "An error has occured!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            ArrayList<User> users = new ArrayList<>();
                            String id, name, url, surname, phone, email, distance, duration, clas, status, department;
                            float distMinV = 0.0f;
                            float distMaxV = Float.MAX_VALUE;
                            float durMinV = 0.0f;
                            float durMaxV = Float.MAX_VALUE;

                            if (!distMin.getText().toString().isEmpty()) { distMinV = Float.parseFloat(distMin.getText().toString()); }
                            if (!distMax.getText().toString().isEmpty()) { distMaxV = Float.parseFloat(distMax.getText().toString()); }
                            if (!durMin.getText().toString().isEmpty()) { durMinV = Float.parseFloat(durMin.getText().toString()); }
                            if (!durMax.getText().toString().isEmpty()) { durMaxV = Float.parseFloat(durMax.getText().toString()); }

                            for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                id = documentSnapshot.getId();
                                name = documentSnapshot.getString("name");
                                url = documentSnapshot.getString("image");
                                surname = documentSnapshot.getString("surname");
                                phone = documentSnapshot.getString("phone");
                                email = documentSnapshot.getString("email");
                                duration  = documentSnapshot.getString("duration");
                                clas = documentSnapshot.getString("class");
                                status = documentSnapshot.getString("status");
                                department = documentSnapshot.getString("department");
                                distance  = documentSnapshot.getString("distance");

                                if(distance != null && duration != null){
                                    if((distMinV <= Float.parseFloat(distance)) &&  (Float.parseFloat(distance) <= (distMaxV))) {
                                        if((durMinV <= Float.parseFloat(duration)) &&  (Float.parseFloat(duration) <= (durMaxV))) {
                                            if(!mAuth.getCurrentUser().getUid().equals(id)) {
                                                User u = new User(email, name, surname, department, distance, clas, status, duration, url, id, phone);
                                                users.add(u);
                                            }
                                        }
                                    }
                                }

                            }
                            recyclerView.setAdapter(new ListProfilesAdapter(ListProfilesActivity.this, users));
                        } else {
                            Toast.makeText(ListProfilesActivity.this, "An error has occured!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@org.checkerframework.checker.nullness.qual.NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_edit) {
            Intent intent = new Intent(ListProfilesActivity.this, EditProfileActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_list) {
            recreate();
        } else if (itemId == R.id.nav_map) {

        } else if (itemId == R.id.nav_matches) {
            Intent intent = new Intent(ListProfilesActivity.this, MainMenuActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ListProfilesActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}