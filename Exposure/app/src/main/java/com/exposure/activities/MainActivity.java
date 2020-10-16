package com.exposure.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.exposure.R;
import com.exposure.fragments.ChatsFragment;
import com.exposure.fragments.MapFragment;
import com.exposure.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    private Fragment mapFragment, chatsFragment, profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                switch (item.getItemId()) {
                    case R.id.fragment_map:
                        fragment = mapFragment;
                        break;
                    case R.id.fragment_chats:
                        fragment = chatsFragment;
                        break;
                    case R.id.fragment_profile:
                        fragment = profileFragment;
                        break;
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