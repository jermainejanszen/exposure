package com.exposure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.exposure.R;
import com.exposure.fragments.ChatsFragment;
import com.exposure.fragments.MapFragment;
import com.exposure.fragments.ProfileFragment;
import com.exposure.user.CurrentUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    private Fragment mapFragment, chatsFragment, profileFragment;
    private CurrentUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null == FirebaseAuth.getInstance().getCurrentUser()) {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        }

        currentUser = new CurrentUser(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mapFragment = new MapFragment();
        chatsFragment = new ChatsFragment();
        profileFragment = new ProfileFragment();

        // Set map as the default fragment
        setFragment(mapFragment);

        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                if(R.id.fragment_map == item.getItemId()) {
                    fragment = mapFragment;
                } else if(R.id.fragment_chats == item.getItemId()) {
                    fragment = chatsFragment;
                } else if(R.id.fragment_profile == item.getItemId()) {
                    fragment = profileFragment;
                }

                setFragment(fragment);
                return true;
            }
        });
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main,
                fragment).commit();
    }
}