package com.azuka.android.trakcerapps.Tab;

import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azuka.android.trakcerapps.ChatActivity;
import com.azuka.android.trakcerapps.Data.Contacts;
import com.azuka.android.trakcerapps.Data.Messages;
import com.azuka.android.trakcerapps.R;
import com.azuka.android.trakcerapps.SettingActivity;
import com.azuka.android.trakcerapps.ViewHolder.ChatViewholder;
import com.azuka.android.trakcerapps.ViewHolder.ContactViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class TabOne extends Fragment {

    private View ChatView;
    private RecyclerView recyclerView;

    DatabaseReference chatRef, messageRef;
    FirebaseAuth mAuth;
    FirebaseUser mUsee;
    String currentUID;

    public TabOne() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ChatView = inflater.inflate(R.layout.fragment_one_location, container, false);
        recyclerView = ChatView.findViewById(R.id.fragment_one_chat_list_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        mUsee = mAuth.getCurrentUser();
        currentUID = mAuth.getCurrentUser().getUid();
        chatRef = FirebaseDatabase.getInstance().getReference().child("Users");
        messageRef = FirebaseDatabase.getInstance().getReference().child("Message");
        return ChatView;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mUsee != null)
        {
            updateUserStatus("online");
        }
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, ChatViewholder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatViewholder holder, final int position, @NonNull final Contacts model) {
                        final String userID = getRef(position).getKey();
                        chatRef.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              if (dataSnapshot.exists())
                              {
                                  if (dataSnapshot.child("userState").hasChild("state"))
                                  {
                                      final String retName = dataSnapshot.child("name").getValue().toString();
                                      final String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                      holder.userName.setText(retName);
                                      holder.itemView.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              if (userID.equals(currentUID))
                                              {
                                                  Intent intent = new Intent(getActivity(), SettingActivity.class);
                                                  startActivity(intent);
                                              }
                                              else {
                                                  Intent intent = new Intent(getActivity(), ChatActivity.class);
                                                  intent.putExtra("visit_name", model.getName());
                                                  intent.putExtra("visit_user_id", userID);
                                                  intent.putExtra("visit_time", time);
                                                  startActivity(intent);
                                              }
                                          }
                                      });
                                  }
                              }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                        messageRef.child(currentUID).child("message").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists())
                                {
                                    dataSnapshot.hasChild("message");
                                    {
                                        Messages messages = new Messages();
                                        holder.userChatMessage.setText(messages.getMessage());
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public ChatViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout_chat, parent, false);
                        return new ChatViewholder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    private void updateUserStatus(String state) {
        Calendar calTime = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = timeFormat.format(calTime.getTime());

        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("time", currentTime);
        hashMap.put("state", state);

        //currentUID = mAuth.getCurrentUser().getUid();
        chatRef.child(currentUID).child("userState")
                .updateChildren(hashMap);
    }
}
