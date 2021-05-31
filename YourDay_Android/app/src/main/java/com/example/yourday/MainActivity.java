package com.example.yourday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yourday.adapter.ViewPagerAdapter;
import com.example.yourday.dialog.FilterTodayTabDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRegistrar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener{
    FirebaseUser currentUser;
    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        setupViewPager();
        setBottomNavigationSelected();

    }

    private void setupViewPager() {
        viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.page_today).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.page_settings).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setBottomNavigationSelected() {
        bottomNavigationView = findViewById(R.id.bottom_naigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.page_today:
                        viewPager.setCurrentItem(0);
                        break;
//                    case R.id.page_future:
//                        Toast.makeText(getApplicationContext(), "This is future page", Toast.LENGTH_SHORT);
//                        break;
//                    case R.id.page_history:
//                        Toast.makeText(getApplicationContext(), "This is history page", Toast.LENGTH_SHORT);
//                        break;
//                    case R.id.page_save:
//                        Toast.makeText(getApplicationContext(), "This is save page", Toast.LENGTH_SHORT);
//                        break;
                    case R.id.page_settings:
                        viewPager.setCurrentItem(1);
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "This is today page", Toast.LENGTH_SHORT);
                        break;
                }
                return true;
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }

}