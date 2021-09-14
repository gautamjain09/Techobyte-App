package com.gautamjain.techobyte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.gautamjain.techobyte.Fragments.HomeFragment;
import com.gautamjain.techobyte.Fragments.NotificationFragment;
import com.gautamjain.techobyte.Fragments.ProfileFragment;
import com.gautamjain.techobyte.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_home_icon:
                        selectorFragment = new HomeFragment();
                        break;

                    case R.id.nav_search_icon:
                        selectorFragment = new SearchFragment();
                        break;

                    case R.id.nav_add_icon:
                        selectorFragment = null;
                        startActivity(new Intent(MainActivity.this, PostActivity.class));
                        break;

                    case R.id.nav_favorite_icon:
                        selectorFragment = new NotificationFragment();
                        break;

                    case R.id.nav_person_icon:
                        selectorFragment = new ProfileFragment();
                        break;
                }

                if(selectorFragment != null)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }

                return true;
            }
        });

        //Default
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
    }
}