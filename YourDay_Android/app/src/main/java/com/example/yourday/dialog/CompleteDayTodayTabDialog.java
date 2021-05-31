package com.example.yourday.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.yourday.R;
import com.example.yourday.adapter.TaskTabTodayRecyclerViewAdapter;
import com.example.yourday.model.DateTask;

import java.util.List;

public class CompleteDayTodayTabDialog extends AppCompatDialogFragment {
    TextView complete, drop, ignore, completePercent, dropPercent, ignorePercent, text, date;
    DateTask dateTask;
    int completeNum, dropNum, ignoreNum, iCompletePercent, iDropPercent, iIgnorePercent;



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.complete_day_dialog, null);
        builder.setView(view);

        //handle data
        dateTask = (DateTask) getArguments().getSerializable("dateTask");
        completeNum = getArguments().getInt("completeNum");
        dropNum = getArguments().getInt("dropNum");
        ignoreNum = getArguments().getInt("ignoreNum");
        iCompletePercent = getArguments().getInt("iCompletePercent");
        iDropPercent = getArguments().getInt("iDropPercent");
        iIgnorePercent = getArguments().getInt("iIgnorePercent");

        //set date layout
        complete = view.findViewById(R.id.complete_day_complete_task);
        drop = view.findViewById(R.id.complete_day_drop_task);
        ignore = view.findViewById(R.id.complete_day_ignore_task);
        completePercent = view.findViewById(R.id.complete_day_complete_percent);
        dropPercent = view.findViewById(R.id.complete_day_drop_percent);
        ignorePercent = view.findViewById(R.id.complete_day_ignore_percent);
        text = view.findViewById(R.id.complete_day_text);
        date = view.findViewById(R.id.complete_day_date);

        complete.setText(""+completeNum);
        drop.setText(""+dropNum);
        ignore.setText(""+ignoreNum);
        completePercent.setText("("+iCompletePercent+"%)");
        dropPercent.setText("("+iDropPercent+"%)");
        ignorePercent.setText("("+iIgnorePercent+"%)");
        if (iCompletePercent < (iIgnorePercent+iDropPercent)){
            text.setText("So bad bro, try harder in next day!");
        }
        date.setText(""+dateTask.getDate());
        Dialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;

    }

}
