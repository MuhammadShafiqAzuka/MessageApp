package com.azuka.android.trakcerapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.mbms.MbmsErrors;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    TextInputEditText phone, code;
    Button verifyBtn, sendBtn;
    ProgressDialog mDialog;
    DatabaseReference mRef;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String mVerificationId;
    private String TAG = "";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        InitializeFields();

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        verifyBtn.setVisibility(View.INVISIBLE);
        sendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        verifyBtn.setVisibility(View.VISIBLE);
                        //Toast.makeText(PhoneLoginActivity.this, "Verification already sent", Toast.LENGTH_SHORT).show();
                        String PhoneNumber = phone.getText().toString();
                        if (TextUtils.isEmpty(PhoneNumber))
                        {
                            phone.setError("Phone number required");
                        }
                        else {
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    PhoneNumber,        // Phone number to verify
                                    60,                 // Timeout duration
                                    TimeUnit.SECONDS,   // Unit of timeout
                                    PhoneLoginActivity.this,               // Activity (for callback binding)
                                    mCallbacks);        // OnVerificationStateChangedCallbacks
                        }
                    }
                });
                // callback after send phone code
                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                        //signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.w(TAG, "onVerificationFailed", e);
                        phone.setError("Invalid phone number");
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        Log.d(TAG, "onCodeSent:" + verificationId);
                        // Save verification ID and resending token so we can use them later
                        mVerificationId = verificationId;
                        mResendToken = token;
                        Toast.makeText(PhoneLoginActivity.this, "Code has been sent", Toast.LENGTH_SHORT).show();

                    }
                };

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verificationCode = code.getText().toString();
                if (TextUtils.isEmpty(verificationCode))
                {
                    code.setError("Please enter the verification code sent");
                }
                else {
                    mDialog.setTitle("Phone Verification");
                    mDialog.setMessage("Please wait while apps authenticating your phone");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final String currentUID = mAuth.getCurrentUser().getUid();
                            FirebaseInstanceId.getInstance().getInstanceId()
                                    .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                        @Override
                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                            String deviceToken = instanceIdResult.getToken();
                                            mRef.child("Users").child(currentUID).child("device_token").setValue(deviceToken);
                                        }
                                    });
                            Log.d(TAG, "signInWithCredential:success");
                            mDialog.dismiss();
                            sendToMain();
                            Toast.makeText(PhoneLoginActivity.this, "Please complete setting information as first step", Toast.LENGTH_SHORT).show();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(PhoneLoginActivity.this, "Signing failed. Retry", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                code.setError("Invalid code");
                            }
                        }
                    }
                });
    }

    private void sendToMain() {
        Intent intent = new Intent(PhoneLoginActivity.this, SettingActivity.class);
        startActivity(intent);
        finish();
    }

    private void SentToLogin() {
        Intent intent = new Intent(PhoneLoginActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void InitializeFields() {
        phone = findViewById(R.id.phone_login_number);
        phone.setText("+60");
        code = findViewById(R.id.phone_login_code);
        verifyBtn = findViewById(R.id.verify_button);
        sendBtn = findViewById(R.id.send_code_button);
        mDialog = new ProgressDialog(this);
    }
}
