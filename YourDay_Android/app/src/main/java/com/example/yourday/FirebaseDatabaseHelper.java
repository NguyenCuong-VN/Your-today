package com.example.yourday;

import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.yourday.adapter.TaskTabTodayRecyclerViewAdapter;
import com.example.yourday.model.DateTask;
import com.example.yourday.model.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class FirebaseDatabaseHelper {
    public static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    public static FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    // get today date
    public void getDateToday(DateTask dateTask, TextView tvDateName){
        DatabaseReference myRef = firebaseDatabase.getReference("dateList").child(currentUser.getUid()).child(Long.toString(dateTask.getId()));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    myRef.child("date").setValue(dateTask.getDate());
                    myRef.child("name").setValue(dateTask.getName());
                    myRef.child("complete").setValue(dateTask.isComplete());
                }
                else {
                    DateTask tmpDateTask = snapshot.getValue(DateTask.class);
                    dateTask.setName(tmpDateTask.getName());
                    dateTask.setComplete(tmpDateTask.isComplete());
                    tvDateName.setText(dateTask.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    //set complete status today date
    public void setTodayStatus(DateTask dateTask){
        DatabaseReference myRef  = firebaseDatabase.getReference("dateList").child(currentUser.getUid()).child(Long.toString(dateTask.getId()));
        myRef.child("complete").setValue(dateTask.isComplete());
    }

    //get start time 1 day
    public void getStartTimeADay(TextView textView){
        DatabaseReference myRef  = firebaseDatabase.getReference("userData").child(currentUser.getUid()).child("startTime");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    myRef.setValue("00:00");
                }
                else {
                    String tmp = snapshot.getValue(String.class);
                    textView.setText(tmp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    //get stop time 1 day
    public void getStopTimeADay(TextView textView){
        DatabaseReference myRef  = firebaseDatabase.getReference("userData").child(currentUser.getUid()).child("stopTime");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    myRef.setValue("23:59");
                }
                else {
                    String tmp = snapshot.getValue(String.class);
                    textView.setText(tmp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    //set startTime a day
    public void setStartTimeADay(String startTime){
        DatabaseReference myRef  = firebaseDatabase.getReference("userData").child(currentUser.getUid()).child("startTime");
        myRef.setValue(startTime);
    }

    //set stopTime a day
    public void setStopTimeADay(String stopTime){
        DatabaseReference myRef  = firebaseDatabase.getReference("userData").child(currentUser.getUid()).child("stopTime");
        myRef.setValue(stopTime);
    }

    //get all task a day
    public void getAllTaskADay(DateTask dateTask, TaskTabTodayRecyclerViewAdapter taskTabTodayRecyclerViewAdapter){
        DatabaseReference myRef  = firebaseDatabase.getReference("tasks").child(currentUser.getUid()).child(Long.toString(dateTask.getId()));
        myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Log.d("atas", snapshot.toString());
                    Log.d("sfs", "sao vao day ??");
                    dateTask.getTaskList().clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Task task = dataSnapshot.getValue(Task.class);
                        dateTask.getTaskList().add(task);
                    }
                    taskTabTodayRecyclerViewAdapter.setTaskList(dateTask.getTaskList());
                    Log.d("sfs", " co task");
                }
                else {
                    dateTask.getTaskList().clear();
                    taskTabTodayRecyclerViewAdapter.setTaskList(dateTask.getTaskList());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }

    //set task a day
    public void addTaskInDay(DateTask dateTask, Task task){
        DatabaseReference myRef  = firebaseDatabase.getReference("tasks").child(currentUser.getUid()).child(Long.toString(dateTask.getId())).child(Long.toString(task.getId()));
        myRef.setValue(task);
    }

    //set task a day
    public void removeTaskInDay(DateTask dateTask, Task task){
        DatabaseReference myRef  = firebaseDatabase.getReference("tasks").child(currentUser.getUid()).child(Long.toString(dateTask.getId())).child(Long.toString(task.getId()));
        myRef.removeValue();
    }

    //delete a day
    public void removeDate(DateTask dateTask){
        DatabaseReference myRef  = firebaseDatabase.getReference("tasks").child(currentUser.getUid()).child(Long.toString(dateTask.getId()));
        myRef.removeValue();
        myRef = firebaseDatabase.getReference("dateList").child(currentUser.getUid()).child(Long.toString(dateTask.getId()));
        myRef.removeValue();
    }

    //get name currentUser
    public String getNameCurrentUser(){
        return currentUser.getDisplayName();
    }

    //get email currentUser
    public String getEmailCurrentUser(){
        return currentUser.getEmail();
    }

    //get avatar currentUser
    public Uri getAvatarCurrentUser(){
        return currentUser.getPhotoUrl();
    }

}
