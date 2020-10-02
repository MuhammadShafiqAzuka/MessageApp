package com.azuka.android.trakcerapps.ViewHolder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.azuka.android.trakcerapps.R;
import com.google.android.material.textview.MaterialTextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatViewholder extends RecyclerView.ViewHolder {
    public MaterialTextView userName, userStatus, userChatMessage;
    public CircleImageView profileImage;
    public AppCompatImageView userOnline;

    public ChatViewholder(@NonNull View itemView) {
        super(itemView);
        userName = itemView.findViewById(R.id.user_profile_name);
        profileImage = itemView.findViewById(R.id.user_profile_image);
        userChatMessage = itemView.findViewById(R.id.user_text_message);
    }
}
