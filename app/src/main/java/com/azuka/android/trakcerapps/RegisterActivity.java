package com.azuka.android.trakcerapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText email, username, password, password2;
    Button register;
    ProgressDialog loading;
    MaterialTextView existmember;

    FirebaseAuth mAuth;
    DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //setup firebase auth
        mAuth = FirebaseAuth.getInstance();
        //create user to db
        RootRef = FirebaseDatabase.getInstance().getReference();
        //initialize all fields given
        InitializeFields();
        //r register account fucntons
        RegisterAccount();

    }
    private void InitializeFields() {
        email = findViewById(R.id.register_email);
        username = findViewById(R.id.register_name);
        password = findViewById(R.id.register_password);
        password2 = findViewById(R.id.register_password2);
        loading = new ProgressDialog(this);
        register = findViewById(R.id.register_button);
        existmember = findViewById(R.id.register_existmember);
        existmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLogin();
                finish();
            }
        });
    }

    private void RegisterAccount() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
    }

    private void createNewAccount() {

        final String userEmail = Objects.requireNonNull(email.getText()).toString();
        final String userName = Objects.requireNonNull(username.getText()).toString();
        String userPassword = Objects.requireNonNull(password.getText()).toString();
        String userPassword2 = Objects.requireNonNull(password2.getText()).toString();

        if (TextUtils.isEmpty(userEmail))
        {
            email.setError("Email Address Required");
        }

        else if (TextUtils.isEmpty(userName))
        {
            email.setError("Username Required");
        }
        else if (TextUtils.isEmpty(userPassword))
        {
            password.setError("Password Required");
        }
        else if (TextUtils.isEmpty(userPassword2) || (!userPassword.equals(userPassword2)))
        {
            password2.setError("Confirm Password Required");
        }
        else {
            loading.setTitle("Creating New Account");
            loading.setMessage("Please wait while apps creating your account");
            loading.setCanceledOnTouchOutside(true);
            loading.show();
            mAuth.createUserWithEmailAndPassword(userEmail, userPassword2)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                final String  currentUID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                    @Override
                                    public void onSuccess(InstanceIdResult instanceIdResult) {
                                        String deviceToken = instanceIdResult.getToken();
                                        HashMap<String , Object> hashMap = new HashMap<>();
                                        hashMap.put("email", userEmail);
                                        hashMap.put("name", userName);
                                        hashMap.put("status", "no status");
                                        hashMap.put("uid", currentUID);
                                        hashMap.put("device_token", deviceToken);
                                        RootRef.child("Users").child(currentUID).updateChildren(hashMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        SendUserToMain();
                                                        loading.dismiss();
                                                        Toast.makeText(RegisterActivity.this, "Please complete setting information as first step", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                            }
                            else {
                                String message = Objects.requireNonNull(task.getException()).toString();
                                Toast.makeText(RegisterActivity.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void SendUserToMain() {
        //intent action to login
        Intent intent = new Intent(RegisterActivity.this, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void SendUserToLogin() {
        //intent action to login
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
