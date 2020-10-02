package com.azuka.android.trakcerapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseUser mCurrentUser;
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    String id;
    CircleImageView profile;
    TextInputEditText userName, status, email;
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        InitializeFields();
        ToolbarSetup();
        //setup fb
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        assert mCurrentUser != null;
        id = mCurrentUser.getUid();
        mRef = FirebaseDatabase.getInstance().getReference();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });
        RetrieveUserInfo();
    }
    private void InitializeFields() {
        toolbar = findViewById(R.id.setting_app_bar);
        profile = findViewById(R.id.setting_row_profile);
        userName = findViewById(R.id.setting_row_name);
        status = findViewById(R.id.setting_row_status);
        update = findViewById(R.id.setting_row_button);
        email = findViewById(R.id.setting_row_email);
    }

    private void ToolbarSetup() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToMain();
            }
        });
    }
    private void sendToMain() {
        // Main Action
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void RetrieveUserInfo() {
        mRef.child("Users").child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.hasChild("email") && dataSnapshot.hasChild("image") && dataSnapshot.hasChild("name"))
                        {
                            String retrieveEmailAddress =  dataSnapshot.child("email").getValue().toString();
                            String retrieveUserStatus =  dataSnapshot.child("status").getValue().toString();
                            String retrieveUserName =  dataSnapshot.child("name").getValue().toString();
                            String retrieveProfileImage =  dataSnapshot.child("image").getValue().toString();

                            userName.setText(retrieveUserName);
                            email.setText(retrieveEmailAddress);
                            status.setText(retrieveUserStatus);
                        }
                        else if (dataSnapshot.exists() && dataSnapshot.hasChild("email"))
                        {
                            String retrieveEmailAddress =  dataSnapshot.child("email").getValue().toString();
                            String retrieveUserStatus =  dataSnapshot.child("status").getValue().toString();
                            String retrieveUserName =  dataSnapshot.child("name").getValue().toString();
                            userName.setText(retrieveUserName);
                            email.setText(retrieveEmailAddress);
                            status.setText(retrieveUserStatus);

                        }
                        else {
                          //   Toast.makeText(SettingActivity.this, "Please update settings information", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void UpdateSettings() {
        final String name = userName.getText().toString();
        final String emailadd = email.getText().toString();
        final String stats = status.getText().toString();

        if (TextUtils.isEmpty(emailadd))
        {
            email.setError("Email Address Required");
        }
        if (TextUtils.isEmpty(name))
        {
            userName.setError("Username Required");
        }
        if (TextUtils.isEmpty(stats))
        {
            status.setText("");
        }
        else {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String deviceToken = instanceIdResult.getToken();
                    HashMap<String, String> profileMap = new HashMap<>();
                    profileMap.put("uid", id);
                    profileMap.put("email", emailadd);
                    profileMap.put("name", name);
                    profileMap.put("status", stats);
                    profileMap.put("device_token", deviceToken);
                    mRef.child("Users").child(id).setValue(profileMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        sendToMain();
                                        Toast.makeText(SettingActivity.this, "Profile updated.", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        String message = task.getException().toString();
                                        Toast.makeText(SettingActivity.this, "Error while updating : "+message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendToMain();
    }
}
