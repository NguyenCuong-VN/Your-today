package com.example.yourday.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.yourday.R;
import com.example.yourday.adapter.TaskTabTodayRecyclerViewAdapter;

import java.util.List;

public class FilterTodayTabDialog extends AppCompatDialogFragment {
    TextView hoanthanh, saptoi, boqua, dangtienhanh, drop, cancel, confirm;
    List<String> filterKey;
    TaskTabTodayRecyclerViewAdapter taskTabTodayRecyclerViewAdapter;

    public FilterTodayTabDialog(TaskTabTodayRecyclerViewAdapter taskTabTodayRecyclerViewAdapter, List<String> filterKey) {
        this.taskTabTodayRecyclerViewAdapter = taskTabTodayRecyclerViewAdapter;
        this.filterKey = filterKey;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.today_tab_filter_task, null);
        builder.setView(view);




        //set layout
        hoanthanh = view.findViewById(R.id.todayTab_filter_complete);
        saptoi = view.findViewById(R.id.todayTab_filter_coming);
        boqua = view.findViewById(R.id.todayTab_filter_ignore);
        dangtienhanh = view.findViewById(R.id.todayTab_filter_running);
        drop = view.findViewById(R.id.todayTab_filter_drop);

        hoanthanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFilterKey(hoanthanh.getText().toString(), hoanthanh);
            }
        });
        saptoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFilterKey(saptoi.getText().toString(), saptoi);
            }
        });
        boqua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFilterKey(boqua.getText().toString(), boqua);
            }
        });
        dangtienhanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFilterKey(dangtienhanh.getText().toString(), dangtienhanh);
            }
        });
        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFilterKey(drop.getText().toString(), drop);
            }
        });

        setBackgroundFilterName();

        Dialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set cancel / confirm option
        cancel = view.findViewById(R.id.todayTab_filter_cancelTextView);
        confirm = view.findViewById(R.id.todayTab_filter_confirmTextView);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTabTodayRecyclerViewAdapter.filterStatus(filterKey);
                dialog.dismiss();
            }
        });

        return dialog;

    }

    private void onClickFilterKey(String key, TextView textView){
        if (filterKey.contains(key)){
            filterKey.remove(key);
            textView.setBackground(getResources().getDrawable(R.drawable.background_filter_options));
        }
        else {
            filterKey.add(key);
            textView.setBackground(getResources().getDrawable(R.drawable.background_filter_options_selected));
        }
    }

    private void setBackgroundFilterName(){
        if (filterKey.contains(hoanthanh.getText().toString())){
            hoanthanh.setBackground(getResources().getDrawable(R.drawable.background_filter_options_selected));
        }
        if (filterKey.contains(saptoi.getText().toString())){
            saptoi.setBackground(getResources().getDrawable(R.drawable.background_filter_options_selected));
        }
        if (filterKey.contains(boqua.getText().toString())){
            boqua.setBackground(getResources().getDrawable(R.drawable.background_filter_options_selected));
        }
        if (filterKey.contains(dangtienhanh.getText().toString())){
            dangtienhanh.setBackground(getResources().getDrawable(R.drawable.background_filter_options_selected));
        }
        if (filterKey.contains(drop.getText().toString())){
            drop.setBackground(getResources().getDrawable(R.drawable.background_filter_options_selected));
        }
    }

}
