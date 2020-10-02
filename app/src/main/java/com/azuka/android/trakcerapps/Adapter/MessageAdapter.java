package com.azuka.android.trakcerapps.Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.azuka.android.trakcerapps.Data.Messages;
import com.azuka.android.trakcerapps.R;
import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> messagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private View context;

    public MessageAdapter (List<Messages> messagesList)
    {
        this.messagesList = messagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public MaterialTextView senderMessageText, recieverMessageText, sender_time, receiver_time, sender_time_image, receiver_time_image;
        public CircleImageView recieverProfileImage;
        public AppCompatImageView senderImageView, recieverImageView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessageText = itemView.findViewById(R.id.sender_msg_text);
            recieverMessageText = itemView.findViewById(R.id.reciever_msg_text);
            recieverProfileImage = itemView.findViewById(R.id.messge_profile_image);
            senderImageView = itemView.findViewById(R.id.message_sender_imageview);
            recieverImageView = itemView.findViewById(R.id.message_reciever_imageview);
            sender_time = itemView.findViewById(R.id.sender_msg_time_sender);
            receiver_time = itemView.findViewById(R.id.reciever_msg_time_receiver);
            sender_time_image = itemView.findViewById(R.id.sender_msg_time_image);
            receiver_time_image = itemView.findViewById(R.id.reciever_msg_time_image);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_msg_layout, parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(context);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = messagesList.get(position);

        String fromUserID = messages.getSender();
        String fromMessageType = messages.getType();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image"))
                {
                    String reciverImage = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(reciverImage).placeholder(R.drawable.profile).into(holder.recieverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.recieverMessageText.setVisibility(View.GONE);
        holder.recieverProfileImage.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.senderImageView.setVisibility(View.GONE);
        holder.recieverImageView.setVisibility(View.GONE);
        holder.sender_time.setVisibility(View.GONE);
        holder.receiver_time.setVisibility(View.GONE);
        holder.sender_time_image.setVisibility(View.GONE);
        holder.receiver_time_image.setVisibility(View.GONE);

        if (fromMessageType.equals("text"))
        {
            if (fromUserID.equals(messageSenderId))
            {
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.sender_time.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_msg_layout);
                holder.senderMessageText.setText(messages.getMessage());
                holder.sender_time.setText(messages.getTime());
            }
            else {
                holder.recieverMessageText.setVisibility(View.VISIBLE);
                holder.recieverProfileImage.setVisibility(View.VISIBLE);
                holder.receiver_time.setVisibility(View.VISIBLE);
                holder.recieverMessageText.setBackgroundResource(R.drawable.reciever_msg_layout);
                holder.recieverMessageText.setText(messages.getMessage());
                holder.receiver_time.setText(messages.getTime());
            }
        }
        else if (fromMessageType.equals("image"))
        {
            if (fromUserID.equals(messageSenderId))
            {
                holder.senderImageView.setVisibility(View.VISIBLE);
                holder.sender_time_image.setVisibility(View.VISIBLE);
                Glide.with(context).load(messages.getMessage()).into(holder.senderImageView);
                holder.sender_time_image.setText(messages.getTime());
            }
            else {
                holder.receiver_time_image.setVisibility(View.VISIBLE);
                holder.recieverImageView.setVisibility(View.VISIBLE);
                Glide.with(context).load(messages.getMessage()).into(holder.recieverImageView);
                holder.receiver_time_image.setText(messages.getTime());
            }
        }
        else {
            if (fromUserID.equals(messageSenderId))
            {
                holder.senderImageView.setVisibility(View.VISIBLE);
                holder.sender_time_image.setVisibility(View.VISIBLE);
                holder.sender_time_image.setText(messages.getTime());
                holder.senderImageView.setBackgroundResource(R.drawable.pdf);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse((messagesList.get(position).getMessage())));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
            else {

                holder.recieverImageView.setVisibility(View.VISIBLE);
                holder.receiver_time_image.setVisibility(View.VISIBLE);
                holder.receiver_time_image.setText(messages.getTime());
                holder.recieverImageView.setBackgroundResource(R.drawable.pdf);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messagesList.get(position).getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
