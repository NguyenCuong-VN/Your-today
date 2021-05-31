package com.example.yourday.fragment;

import android.app.TimePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yourday.FirebaseDatabaseHelper;
import com.example.yourday.R;
import com.example.yourday.dialog.AboutUsDialog;
import com.example.yourday.model.DateTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

public class SettingFragment extends Fragment {
    LinearLayout aboutUs, startTime, stopTime, deleteHistory;
    TextView tvStartTime, tvStopTime, name, email;
    FirebaseDatabaseHelper firebaseDatabaseHelper;
    ImageView avatar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }

        aboutUs = view.findViewById(R.id.setting_tab_about_us);
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutUsDialog aboutUsDialog = new AboutUsDialog();
                aboutUsDialog.show(getFragmentManager(), "about us");
            }
        });

        tvStartTime = view.findViewById(R.id.time_start_setting_tab);
        tvStopTime = view.findViewById(R.id.time_stop_setting_tab);

        startTime = view.findViewById(R.id.setting_tab_start_time);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tvStartTime.setText(hourOfDay+":"+minute);
                        firebaseDatabaseHelper.setStartTimeADay(tvStartTime.getText().toString());
                    }
                }, 0,0,true);
                timePickerDialog.show();
            }
        });
        stopTime = view.findViewById(R.id.setting_tab_stop_time);
        stopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tvStopTime.setText(hourOfDay+":"+minute);
                        firebaseDatabaseHelper.setStopTimeADay(tvStopTime.getText().toString());
                    }
                }, 0,0,true);
                timePickerDialog.show();
            }
        });

        deleteHistory = view.findViewById(R.id.setting_tab_delete_history);
        deleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH)+1;
                int date = calendar.get(Calendar.DATE);
                DateTask dateTask = new DateTask(Integer.toString(date)+"/"+Integer.toString(month)+"/"+Integer.toString(year), "date 1", false);
                firebaseDatabaseHelper.removeDate(dateTask);
            }
        });

        //firebase
        firebaseDatabaseHelper = new FirebaseDatabaseHelper();
        firebaseDatabaseHelper.getStartTimeADay(tvStartTime);
        firebaseDatabaseHelper.getStopTimeADay(tvStopTime);

        //set profile
        name = view.findViewById(R.id.setting_tab_name);
        email = view.findViewById(R.id.setting_tab_email);
        avatar = view.findViewById(R.id.setting_tab_avatar);
        name.setText(firebaseDatabaseHelper.getNameCurrentUser());
        email.setText(firebaseDatabaseHelper.getEmailCurrentUser());
        Drawable drawable = loadImageFromWeb(firebaseDatabaseHelper.getAvatarCurrentUser().toString());
        if (drawable != null){
            avatar.setImageDrawable(drawable);
        }

        return view;
    }

    public Drawable loadImageFromWeb(String url){
        InputStream is = null;
        Drawable d = null;
        try {
            is = (InputStream) new URL(url).getContent();
             d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return d;
    }


}
