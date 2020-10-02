package com.azuka.android.trakcerapps.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.azuka.android.trakcerapps.R;
import com.google.android.material.textview.MaterialTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactViewHolder extends RecyclerView.ViewHolder {

    public MaterialTextView userName, userStatus;
    public CircleImageView profileImage;
    public AppCompatImageView userOnline;

    public ContactViewHolder(@NonNull View itemView) {
        super(itemView);
        userName = itemView.findViewById(R.id.user_profile_name);
        userStatus = itemView.findViewById(R.id.user_status);
        profileImage = itemView.findViewById(R.id.user_profile_image);
        userOnline = itemView.findViewById(R.id.user_profile_online);
    }
}

