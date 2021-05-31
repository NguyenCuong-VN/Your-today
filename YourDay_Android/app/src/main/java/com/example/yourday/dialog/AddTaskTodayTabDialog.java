package com.example.yourday.dialog;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.yourday.R;
import com.example.yourday.Receiver.AlarmReceiver;
import com.example.yourday.adapter.TaskTabTodayRecyclerViewAdapter;
import com.example.yourday.model.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTaskTodayTabDialog extends AppCompatDialogFragment {
    EditText edtName, edtNote;
    TextView edtStartTime, edtStopTime;
    TextView tvCancel, tvConfirm;
    TaskTabTodayRecyclerViewAdapter taskTabTodayRecyclerViewAdapter;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    public AddTaskTodayTabDialog(TaskTabTodayRecyclerViewAdapter taskTabTodayRecyclerViewAdapter) {
        this.taskTabTodayRecyclerViewAdapter = taskTabTodayRecyclerViewAdapter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.today_tab_add_task_dialog, null);
        builder.setView(view);

        Dialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set open time picker when click
        edtStartTime = view.findViewById(R.id.todayTab_addTask_startTimeEditText);
        edtStopTime = view.findViewById(R.id.todayTab_addTask_stopTimeEditText);
        edtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        edtStartTime.setText(Integer.toString(hourOfDay)+":"+Integer.toString(minute));
                    }
                }, Calendar.getInstance().getTime().getHours(),Calendar.getInstance().getTime().getMinutes(),true);
                timePickerDialog.show();
            }
        });
        edtStopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        edtStopTime.setText(Integer.toString(hourOfDay)+":"+Integer.toString(minute));
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE),true);
                timePickerDialog.show();
            }
        });

        //set option dialog
        tvCancel = view.findViewById(R.id.todayTab_addTask_cancelTextView);
        tvConfirm = view.findViewById(R.id.todayTab_addTask_confirmTextView);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                edtName = view.findViewById(R.id.todayTab_addTask_nameEditText);
                edtNote = view.findViewById(R.id.todayTab_addTask_noteEditText);
                edtStartTime = view.findViewById(R.id.todayTab_addTask_startTimeEditText);
                edtStopTime = view.findViewById(R.id.todayTab_addTask_stopTimeEditText);

                if (checkField(edtName, edtNote, edtStartTime, edtStopTime)){
                    Task task = new Task(edtName.getText().toString(), edtNote.getText().toString(), edtStartTime.getText().toString(), edtStopTime.getText().toString(), getString(R.string.coming_status), null,null);
                    taskTabTodayRecyclerViewAdapter.addTask(task);
                    Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();

                    //setup alarm
                    alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getContext(), AlarmReceiver.class);
                    intent.putExtra("title", task.getName());
                    intent.putExtra("des", task.getNote());
                    pendingIntent = PendingIntent.getBroadcast(
                            getContext(), 0, intent, 0
                    );
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(task.getStartTime().split(":")[0]));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(task.getStartTime().split(":")[1]));
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    Log.d("asd", calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));

                    dialog.dismiss();
                }



            }
        });

        return dialog;
    }

    private boolean checkField(EditText edtName, EditText edtNote, TextView edtStartTime, TextView edtStopTime){
        if (edtName.getText().toString().isEmpty() || edtStopTime.getText().toString().isEmpty() || edtStartTime.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Bạn chưa điền hết nội dung các trường", Toast.LENGTH_SHORT).show();
            return false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        try {
            Date date = dateFormat.parse(edtStartTime.getText().toString());

            long timeNow = (long) dateFormat.parse(""+Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+Calendar.getInstance().get(Calendar.MINUTE)).getTime();
            long timeStart = (long) date.getTime();
            Log.d("tag", "timeNow: "+timeNow);
            Log.d("tag", "timeStart: "+timeStart);
            if (timeNow > timeStart){
                Toast.makeText(getContext(), "Thời gian bắt đầu không thể ở trong quá khứ", Toast.LENGTH_SHORT).show();
                return false;
            }
            date = dateFormat.parse(edtStopTime.getText().toString());
            long dateStop = (long) date.getTime();
            Log.d("tag", "timeStop: "+dateStop);
            if (timeStart > dateStop){
                Toast.makeText(getContext(), "Thời gian kết thúc không thể sớm hơn thời gian bắt đầu", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Sai định dạng thời gian", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
