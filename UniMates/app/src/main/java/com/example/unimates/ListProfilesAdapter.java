package com.example.unimates;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class ListProfilesAdapter extends RecyclerView.Adapter<ListProfilesAdapter.UserViewHolder>{
    private final Context context;
    private final ArrayList<User> userArrayList;

    public ListProfilesAdapter(Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public ListProfilesAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListProfilesAdapter.UserViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.name.setText(user.getName());
        holder.surname.setText(user.getSurname());
        if(user.getStatus() != null) {
            if(user.getStatus().equals("Choose")){
                holder.status.setText("Status not selected");
            } else {
                holder.status.setText(user.getStatus());
            }
        }
        else { holder.status.setText("Status not selected"); }
        String url = user.getUrl();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.baseline_person_48);
        requestOptions.fitCenter();
        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(url)
                .into(holder.cardProfile);
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView cardProfile;
        TextView name, surname, status;
        CardView cardView;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            cardProfile = itemView.findViewById(R.id.cardProfile);
            name = itemView.findViewById(R.id.name);
            surname = itemView.findViewById(R.id.surname);
            status = itemView.findViewById(R.id.status);
            cardView = itemView.findViewById(R.id.card_layout);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = userArrayList.get(getAdapterPosition());

                    Intent intent = new Intent(context, UserActivity.class);
                    intent.putExtra("uid", user.getUserID());
                    context.startActivity(intent);
                }
            });
        }
    }
}
