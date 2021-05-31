package com.example.yourday.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.yourday.R;
import com.example.yourday.adapter.TaskTabTodayRecyclerViewAdapter;
import com.example.yourday.model.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DetailTaskTodayTabDialog extends AppCompatDialogFragment {
    EditText edtName, edtNote;
    TextView edtStartTime, edtStopTime;
    TextView tvCancel, tvConfirm;
    TaskTabTodayRecyclerViewAdapter taskTabTodayRecyclerViewAdapter;
    Task task;
    Switch editSwitch;

    public DetailTaskTodayTabDialog(TaskTabTodayRecyclerViewAdapter taskTabTodayRecyclerViewAdapter) {
        this.taskTabTodayRecyclerViewAdapter = taskTabTodayRecyclerViewAdapter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.today_tab_detail_task_dialog, null);
        builder.setView(view);

        Dialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set open time picker when click
        edtStartTime = view.findViewById(R.id.todayTab_detailTab_startTimeEditText);
        edtStopTime = view.findViewById(R.id.todayTab_detailTab_stopTimeEditText);
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
                }, Calendar.getInstance().getTime().getHours(),Calendar.getInstance().getTime().getMinutes(),true);
                timePickerDialog.show();
            }
        });

        //set option dialog
        tvCancel = view.findViewById(R.id.todayTab_detailTab_cancelTextView);
        tvConfirm = view.findViewById(R.id.todayTab_detailTab_confirmTextView);
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
                if (editSwitch.isChecked()){
                    if (checkField(edtName, edtNote, edtStartTime, edtStopTime)){
                        Task newTask = new Task(edtName.getText().toString(), edtNote.getText().toString(), edtStartTime.getText().toString(), edtStopTime.getText().toString(), getString(R.string.coming_status), null,null);
                        taskTabTodayRecyclerViewAdapter.changeTask(task, newTask);
                        Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Bạn phải enable edit trước khi lưu", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //set data field
        this.task = (Task) getArguments().getSerializable("task");
        edtName = view.findViewById(R.id.todayTab_detailTab_nameEditText);
        edtNote = view.findViewById(R.id.todayTab_detailTab_noteEditText);
        edtStartTime = view.findViewById(R.id.todayTab_detailTab_startTimeEditText);
        edtStopTime = view.findViewById(R.id.todayTab_detailTab_stopTimeEditText);
        edtName.setText(task.getName());
        edtNote.setText(task.getNote());
        edtStartTime.setText(task.getStartTime());
        edtStopTime.setText(task.getStopTime());

        //check status when edit
        editSwitch = view.findViewById(R.id.todayTab_detailTab_editableSwitch);
        editSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (!task.getStatus().equals(getString(R.string.coming_status))){
                        editSwitch.setChecked(false);
                        Toast.makeText(getContext(), "Bạn chỉ có thể thay đổi task chưa bắt đầu", Toast.LENGTH_SHORT).show();
                    }
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

            long timeNow = dateFormat.parse(""+Calendar.getInstance().getTime().getHours()+":"+Calendar.getInstance().getTime().getMinutes()).getTime();
            long timeStart = date.getTime();
            Log.d("tag", "checkField: "+timeNow);
            Log.d("tag", "checkField: "+timeStart);
            if (timeNow > timeStart){
                Toast.makeText(getContext(), "Thời gian bắt đầu không thể ở trong quá khứ", Toast.LENGTH_SHORT).show();
                return false;
            }
            date = dateFormat.parse(edtStopTime.getText().toString());
            long dateStop = date.getTime();
            Log.d("tag", "checkField: "+dateStop);
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
