package com.azuka.android.trakcerapps;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azuka.android.trakcerapps.Adapter.MessageAdapter;
import com.azuka.android.trakcerapps.Data.Messages;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    String userChatName;
    TextInputEditText chat_input;
    ImageButton send_btn, files_btn;
    ProgressDialog loading;
    MaterialAlertDialogBuilder builder;

    FirebaseAuth mAuth;
    DatabaseReference mRef, chatRef;

    //APIService apiService;
    //boolean notify = false;
    //private RequestQueue requestQueue;
    //private String URL = "https://fcm.googleapis.com/fcm/send";

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    RecyclerView chat_RV;

    private String messageSenderID, messageRecieverID, timeSeen = "", checker = "", myURL = "";
    private StorageTask uploadTask;
    private Uri fileUri;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

            userChatName = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("visit_name")).toString();
            messageRecieverID = Objects.requireNonNull(getIntent().getExtras().get("visit_user_id")).toString();
            timeSeen = Objects.requireNonNull(getIntent().getExtras().get("visit_time")).toString();

            InitializeField();
            ToolbarSetting();

            //apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();
            messageSenderID = mAuth.getCurrentUser().getUid();
            chatRef = FirebaseDatabase.getInstance().getReference();
            mRef = FirebaseDatabase.getInstance().getReference();
            //GetUserInfo();

            send_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveMessageInfoToDB();
                    chat_input.setText("");
                }
            });
            files_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CharSequence[] options1 = new CharSequence[]
                            {
                                    "Images",
                                    "PDF Files",
                                    "Ms Word Files"
                            };
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setTitle("Select Files: ");
                    builder.setItems(options1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                checker = "image";
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Select Image"), 43);
                            }
                            if (which == 1) {
                                checker = "pdf";
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("application/pdf");
                                startActivityForResult(Intent.createChooser(intent, "Select PDF File"), 43);
                            }
                            if (which == 2) {
                                checker = "docx";
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("application/msword");
                                startActivityForResult(intent.createChooser(intent, "Select Ms Word File"), 43);
                            }
                        }
                    });

                    builder.show();
                }
            });

            DisplayLastSeen();
            chatRef.child("Message").child(messageSenderID).child(messageRecieverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        chat_RV.smoothScrollToPosition(chat_RV.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /*private void sendNotification() {
        JSONObject mainObject = new JSONObject();
        try {
            mainObject.put("to", "/to"+ "news");
            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title", "any title");
            notificationObject.put("body", "any body");
            mainObject.put("notification", notificationObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    mainObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==43 && resultCode==RESULT_OK && data!= null && data.getData()!=null)
        {
            fileUri = data.getData();
            if (!checker.equals("image"))
            {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
                final String messageSenderRef = "Message/"+ messageSenderID + "/" + messageRecieverID;
                final String messageRecieverRef = "Message/" + messageRecieverID+ "/" + messageSenderID;
                DatabaseReference userMessageKeyRef = chatRef.child("Messages")
                        .child(messageSenderID).child(messageRecieverID)
                        .push();

                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID+ "." + checker);

                filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    final String downloadUrl = uri.toString();
                                    final Calendar calDate = Calendar.getInstance();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                                    String currentDate = dateFormat.format(calDate.getTime());

                                    Calendar calTime = Calendar.getInstance();
                                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                                    String currentTime = timeFormat.format(calTime.getTime());

                                    HashMap<String ,Object> hashMapBody = new HashMap<>();
                                    hashMapBody.put("sender", messageSenderID);
                                    hashMapBody.put("receiver",messageRecieverID);
                                    hashMapBody.put("messageID", messagePushID);
                                    hashMapBody.put("message", downloadUrl);
                                    hashMapBody.put("name", fileUri.getLastPathSegment());
                                    hashMapBody.put("date", currentDate);
                                    hashMapBody.put("time", currentTime);
                                    hashMapBody.put("type", checker);

                                    HashMap<String ,Object> hashMapBodyDetail = new HashMap<>();
                                    hashMapBodyDetail.put(messageSenderRef + "/"+ messagePushID, hashMapBody);
                                    hashMapBodyDetail.put(messageRecieverRef + "/"+ messagePushID, hashMapBody);

                                    chatRef.updateChildren(hashMapBodyDetail);
                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else if (checker.equals("image"))
            {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
                final String messageSenderRef = "Message/"+ messageSenderID + "/" + messageRecieverID;
                final String messageRecieverRef = "Message/" + messageRecieverID+ "/" + messageSenderID;
                DatabaseReference userMessageKeyRef = chatRef.child("Messages")
                        .child(messageSenderID).child(messageRecieverID)
                        .push();

               final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID+ "." + "jpg");
                uploadTask = filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>(){
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful())
                        {
                            Uri downloadUri = task.getResult();
                            myURL = downloadUri.toString();

                            final Calendar calDate = Calendar.getInstance();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
                            String currentDate = dateFormat.format(calDate.getTime());

                            Calendar calTime = Calendar.getInstance();
                            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                            String currentTime = timeFormat.format(calTime.getTime());

                            HashMap<String ,Object> hashMapBody = new HashMap<>();
                            hashMapBody.put("sender", messageSenderID);
                            hashMapBody.put("receiver",messageRecieverID);
                            hashMapBody.put("messageID", messagePushID);
                            hashMapBody.put("message", myURL);
                            hashMapBody.put("name", fileUri.getLastPathSegment());
                            hashMapBody.put("date", currentDate);
                            hashMapBody.put("time", currentTime);
                            hashMapBody.put("type", checker);

                            HashMap<String ,Object> hashMapBodyDetail = new HashMap<>();
                            hashMapBodyDetail.put(messageSenderRef + "/"+ messagePushID, hashMapBody);
                            hashMapBodyDetail.put(messageRecieverRef + "/"+ messagePushID, hashMapBody);

                            chatRef.updateChildren(hashMapBodyDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        //loading.dismiss();
                                    }
                                    else {
                                        Toast.makeText(ChatActivity.this, "Cant send message", Toast.LENGTH_SHORT).show();
                                    }
                                    chat_input.setText("");
                                }
                            });
                        }
                    }
                });
            }
            else {
                Toast.makeText(this, "Image discarded, Error.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void DisplayLastSeen() {
       mRef.child("Users").child(messageSenderID)
               .addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("userState").hasChild("state"))
                        {
                            //final String time = dataSnapshot.child("userState").child("time").getValue().toString();
                        }
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
    }

    private void ToolbarSetting() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle(userChatName);
        getSupportActionBar().setSubtitle("Last seen on : "+timeSeen);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBackToMain();
            }
        });
    }
    private void InitializeField() {
        toolbar = findViewById(R.id.chat_page_toolbar);
        chat_input = findViewById(R.id.chat_input_msg);
        send_btn = findViewById(R.id.chat_send);
        files_btn = findViewById(R.id.chat_file_send);
        messageAdapter = new MessageAdapter(messagesList);
        chat_RV = findViewById(R.id.chat_scroll);
        linearLayoutManager = new LinearLayoutManager(this);
        chat_RV.setLayoutManager(linearLayoutManager);
        chat_RV.setAdapter(messageAdapter);
        loading = new ProgressDialog(this);
        //requestQueue = Volley.newRequestQueue(this);
       // messageAdapter.notifyDataSetChanged();
    }
    private void sendBackToMain() {
        // Main Action
        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void SaveMessageInfoToDB() {
        final String message = chat_input.getText().toString();
        String msgKey = chatRef.push().getKey();

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Please write some messages", Toast.LENGTH_SHORT).show();
        }
        else {

            String messageSenderRef = "Message/"+ messageSenderID + "/" + messageRecieverID;
            String messageRecieverRef = "Message/" + messageRecieverID+ "/" + messageSenderID;

            final Calendar calDate = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            String currentDate = dateFormat.format(calDate.getTime());

            Calendar calTime = Calendar.getInstance();
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            String currentTime = timeFormat.format(calTime.getTime());

            DatabaseReference userMessageKeyRef = chatRef.child("Messages")
                    .child(messageSenderID).child(messageRecieverID)
                    .push();

            String messagePushID = userMessageKeyRef.getKey();

            HashMap<String ,Object> hashMapBody = new HashMap<>();
            hashMapBody.put("sender", messageSenderID);
            hashMapBody.put("receiver",messageRecieverID);
            hashMapBody.put("messageID", messagePushID);
            hashMapBody.put("message", message);
            hashMapBody.put("date", currentDate);
            hashMapBody.put("time", currentTime);
            hashMapBody.put("type", "text");

            HashMap<String ,Object> hashMapBodyDetail = new HashMap<>();
            hashMapBodyDetail.put(messageSenderRef + "/"+ messagePushID, hashMapBody);
            hashMapBodyDetail.put(messageRecieverRef + "/"+ messagePushID, hashMapBody);

            chatRef.updateChildren(hashMapBodyDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        //sendNotification(messageRecieverID,message);
                    }
                    else {
                        Toast.makeText(ChatActivity.this, "Cant send message", Toast.LENGTH_SHORT).show();
                    }
                    chat_input.setText("");
                }
            });
        }
    }

    private void sendNotification(String messageRecieverID, String message) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()== R.id.chat_option_call)
        {
            //sendToSetting();
        }
        if (item.getItemId() == R.id.chat_option_profile)
        {
            //sendToMap();
        }
        if (item.getItemId() == R.id.chat_option_report)
        {
           //
        }
        return true;
    }

    private void updateUserStatus(String state){
        Calendar calTime = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = timeFormat.format(calTime.getTime());

        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("time", currentTime);
        hashMap.put("state", state);

        //currentUID = mAuth.getCurrentUser().getUid();
        chatRef.child("Users").child(messageSenderID).child("userState")
                .updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mCurrentUser != null)
        {
            updateUserStatus("online");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCurrentUser != null)
        {
            updateUserStatus("offline");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCurrentUser != null)
        {
            updateUserStatus("offline");
        }
    }
}
