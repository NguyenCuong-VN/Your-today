package com.example.yourday;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Splat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(getBaseContext(), Login.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }
        finish();
    }

}