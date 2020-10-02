package com.azuka.android.trakcerapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.azuka.android.trakcerapps.Adapter.TabAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    FirebaseUser mCurrentUser;
    String currentUID;
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize current user
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        //currentUID = mCurrentUser.getUid();
        mRef = FirebaseDatabase.getInstance().getReference();

        initializeTabAndPager();
        intializeFields();
    }

    private void intializeFields() {
        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("TrackerApps");
    }

    private void initializeTabAndPager() {
        ///////////////////tab////////////////////////////
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("MESSAGES").setIcon(R.drawable.message));
        tabLayout.addTab(tabLayout.newTab().setText("CONTACTS").setIcon(R.drawable.contact));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //////////////////pager/////////////////////////////
        final ViewPager viewPager = findViewById(R.id.tab_pager);
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()== R.id.settings_option_menu)
        {
            sendToSetting();
        }
        if (item.getItemId() == R.id.map_option_menu)
        {
           sendToMap();
        }
       if (item.getItemId() == R.id.logout_option_menu)
        {
            mAuth.signOut();
            LoginActivity();
            updateUserStatus("offline");
        }
       return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser == null)
        {
            LoginActivity();
        }
        else {
            VerifyUserExisting();
            updateUserStatus("online");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentUID != null)
        {
            updateUserStatus("online");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentUID != null)
        {
            updateUserStatus("offline");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentUID != null)
        {
            updateUserStatus("offline");
        }
    }

    private void VerifyUserExisting() {
        String currentUID = mCurrentUser.getUid();
        mRef.child("Users").child(currentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("email").exists())
                {
                    //Toast.makeText(MainActivity.this, "Welcome, User : "+, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Complete profile background as your first step", Toast.LENGTH_SHORT).show();
                    sendToSetting();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendToSetting() {
        // Setting Action
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void LoginActivity() {
        // login Action
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void sendToMap() {
        // Map Action
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void updateUserStatus(String state){
        String currentUID = mCurrentUser.getUid();
        Calendar calTime = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = timeFormat.format(calTime.getTime());

        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("time", currentTime);
        hashMap.put("state", state);

        mRef.child("Users").child(currentUID).child("userState")
                .updateChildren(hashMap);
    }
}
