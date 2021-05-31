package com.example.yourday.adapter;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourday.FirebaseDatabaseHelper;
import com.example.yourday.R;
import com.example.yourday.dialog.AddTaskTodayTabDialog;
import com.example.yourday.dialog.DetailTaskTodayTabDialog;
import com.example.yourday.model.DateTask;
import com.example.yourday.model.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskTabTodayRecyclerViewAdapter extends RecyclerView.Adapter<TaskTabTodayRecyclerViewAdapter.TaskViewHolder> {
    private Context context;
    List<Task> taskFilteredList;
    DateTask dateTask;
    FragmentManager fragmentManager;
    FirebaseDatabaseHelper firebaseDatabaseHelper = new FirebaseDatabaseHelper();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public TaskTabTodayRecyclerViewAdapter(Context context, DateTask dateTask, FragmentManager fragmentManager) {
        this.context = context;
        this.dateTask = dateTask;
        this.fragmentManager = fragmentManager;
        dateTask.getTaskList().sort((o1, o2) -> (int)o1.getId()-(int)o2.getId());
        setTaskFilteredList(this.dateTask.getTaskList());
    }

    public void setTaskFilteredList(List<Task> list) {
        this.taskFilteredList = new ArrayList<Task>();
        taskFilteredList.addAll(list);
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setTaskList(List<Task> taskList) {
        this.dateTask.setTaskList(taskList);
        dateTask.getTaskList().sort((o1, o2) -> (int) ((long)o1.getId()-(long)o2.getId()));
        setTaskFilteredList(this.dateTask.getTaskList());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addTask(Task task){
        this.dateTask.getTaskList().add(task);
        firebaseDatabaseHelper.addTaskInDay(dateTask, task);
        setTaskList(dateTask.getTaskList());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void changeTask(Task oldTask, Task newTask){
        if (this.dateTask.getTaskList().contains(oldTask)){
            this.dateTask.getTaskList().remove(oldTask);
        }
        this.dateTask.getTaskList().add(newTask);
        firebaseDatabaseHelper.removeTaskInDay(dateTask, oldTask);
        firebaseDatabaseHelper.addTaskInDay(dateTask, newTask);
        setTaskList(this.dateTask.getTaskList());
    }

    public void filter(String searchName){
        taskFilteredList.clear();
        if (searchName.isEmpty()){
            taskFilteredList.addAll(dateTask.getTaskList());
        }
        else {
            for (Task task : dateTask.getTaskList()){
                if (task.getName().toLowerCase().contains(searchName.toLowerCase())){
                    taskFilteredList.add(task);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterStatus(List<String> filterName){
        taskFilteredList.clear();
        if (filterName.isEmpty()){
            taskFilteredList.addAll(dateTask.getTaskList());
        }
        else {
            for (Task task : dateTask.getTaskList()){
                if (filterName.contains(task.getStatus())){
                    taskFilteredList.add(task);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row_today_tab, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskFilteredList.get(position);
        if (task == null){
            return;
        }
        holder.tvName.setText(task.getName());
        if (task.getNote().length() > 30){
            holder.tvNote.setText(task.getNote().substring(0, 27)+"...");
        }
        else{
            holder.tvNote.setText(task.getNote());
        }
        holder.tvTime.setText(task.getStartTime()+" - "+task.getStopTime());
        if (task.getRealStartTime() == null){
            holder.tvRealTime.setText("");
            holder.imgToRealTime.setVisibility(View.INVISIBLE);
        }
        else {
            holder.tvRealTime.setText(task.getRealStartTime()+" - "+task.getRealStopTime());
            holder.imgToRealTime.setVisibility(View.VISIBLE);
        }
        holder.tvStatus.setText(task.getStatus());
        if (task.getStatus() == null){
            holder.card.setBackgroundColor(context.getResources().getColor(R.color.today_tab_complete_card));
        }
        else{
            if (task.getStatus().equals(context.getString(R.string.coming_status))){
                holder.card.setBackgroundColor(context.getResources().getColor(R.color.today_tab_comming_card));
            }
            else if (task.getStatus().equals(context.getString(R.string.drop_status))){
                holder.card.setBackgroundColor(context.getResources().getColor(R.color.today_tab_drop_card));
            }
            else if (task.getStatus().equals(context.getString(R.string.ignore_status))){
                holder.card.setBackgroundColor(context.getResources().getColor(R.color.today_tab_ignore_card));
            }
            else if (task.getStatus().equals(context.getString(R.string.running_status))){
                holder.card.setBackgroundColor(context.getResources().getColor(R.color.today_tab_running_card));
            }
            else if (task.getStatus().equals(context.getString(R.string.complete_status))){
                holder.card.setBackgroundColor(context.getResources().getColor(R.color.today_tab_complete_card));
            }
        }

        holder.moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.moreView);
                popupMenu.inflate(R.menu.today_tab_card_task_more_menu);

                setupEnableMenu(popupMenu, holder.tvStatus);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().toString().equals("Start")){
                            Task oldTask = new Task(task.getId(), task.getName(), task.getNote(), task.getStartTime(), task.getStopTime(), task.getStatus(), task.getRealStartTime(), task.getRealStopTime());
                            task.setStatus(context.getString(R.string.running_status));
                            String realStartTime = Calendar.getInstance().getTime().getHours()+":"+Calendar.getInstance().getTime().getMinutes();
                            task.setRealStartTime(realStartTime);
                            changeTask(oldTask, task);
                            notifyItemChanged(position);
                        }
                        else if (item.getTitle().toString().equals("Stop")){
                            Task oldTask = new Task(task.getId(), task.getName(), task.getNote(), task.getStartTime(), task.getStopTime(), task.getStatus(), task.getRealStartTime(), task.getRealStopTime());
                            task.setStatus(context.getString(R.string.complete_status));
                            String realStopTime = Calendar.getInstance().getTime().getHours()+":"+Calendar.getInstance().getTime().getMinutes();
                            task.setRealStopTime(realStopTime);
                            changeTask(oldTask, task);
                            notifyItemChanged(position);
                        }
                        else if (item.getTitle().toString().equals("Ignore")){
                            Task oldTask = new Task(task.getId(), task.getName(), task.getNote(), task.getStartTime(), task.getStopTime(), task.getStatus(), task.getRealStartTime(), task.getRealStopTime());
                            task.setStatus(context.getString(R.string.ignore_status));
                            changeTask(oldTask, task);
                            notifyItemChanged(position);
                        }
                        else if (item.getTitle().toString().equals("Drop")){
                            Task oldTask = new Task(task.getId(), task.getName(), task.getNote(), task.getStartTime(), task.getStopTime(), task.getStatus(), task.getRealStartTime(), task.getRealStopTime());
                            task.setStatus(context.getString(R.string.drop_status));
                            String realStopTime = Calendar.getInstance().getTime().getHours()+":"+Calendar.getInstance().getTime().getMinutes();
                            task.setRealStopTime(realStopTime);
                            changeTask(oldTask, task);
                            notifyItemChanged(position);
                        }
                        else if (item.getTitle().toString().equals("Save task")){
                            Toast.makeText(context, "saved", Toast.LENGTH_SHORT).show();
                            notifyItemChanged(position);
                        }
                        else if (item.getTitle().toString().equals("Delete")){
                            dateTask.getTaskList().remove(task);
                            setTaskFilteredList(dateTask.getTaskList());
                            notifyItemRemoved(position);
                            firebaseDatabaseHelper.removeTaskInDay(dateTask, task);
                        }

                        setupEnableMenu(popupMenu, holder.tvStatus);
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        if(taskFilteredList.isEmpty()){
            return 0;
        }
        return taskFilteredList.size();
    }


    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName, tvNote, tvTime, tvRealTime, tvStatus;
        ConstraintLayout card;
        ImageView imgToRealTime;
        View moreView;


        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.todayTab_cardTask_nameTask);
            tvNote = itemView.findViewById(R.id.todayTab_cardTask_noteTask);
            tvTime = itemView.findViewById(R.id.todayTab_cardTask_timeTask);
            tvRealTime = itemView.findViewById(R.id.todayTab_cardTask_realTimeTask);
            tvStatus = itemView.findViewById(R.id.todayTab_cardTask_statusTask);
            card = itemView.findViewById(R.id.todayTab_cardTask_card);
            imgToRealTime = itemView.findViewById(R.id.todayTab_cardTask_toRealTime);
            moreView = itemView.findViewById(R.id.todayTab_cardTask_moreIconView);
            itemView.setOnClickListener(this::onClick);
        }


        @Override
        public void onClick(View v) {
            Task task = taskFilteredList.get(getAdapterPosition());
            if (task == null){
                Toast.makeText(context, "bi null", Toast.LENGTH_SHORT).show();
            }
            else {
                Bundle bundle = new Bundle();
                bundle.putSerializable("task", task);
                DetailTaskTodayTabDialog addTaskTodayTabDialog = new DetailTaskTodayTabDialog(TaskTabTodayRecyclerViewAdapter.this);
                addTaskTodayTabDialog.setArguments(bundle);
                addTaskTodayTabDialog.show(fragmentManager, "Detail task today tab dialog");
            }
        }
    }



    private void setupEnableMenu(PopupMenu popupMenu, TextView tvStatus){
        if (tvStatus.getText().toString().equals(context.getString(R.string.coming_status))){
            setComingMenu(popupMenu);
        }
        else if (tvStatus.getText().toString().equals(context.getString(R.string.running_status))){
            setRunningMenu(popupMenu);
        }
        else {
            setDropCompleteIgnoreMenu(popupMenu);
            Log.d("asds", tvStatus.getText().toString());
        }
    }

    private void setComingMenu(PopupMenu popupMenu){
        popupMenu.getMenu().findItem(R.id.tabToday_cardTask_moreMenu_Start).setEnabled(true);
        popupMenu.getMenu().findItem(R.id.tabToday_cardTask_moreMenu_Stop).setEnabled(false);
        popupMenu.getMenu().findItem(R.id.tabToday_cardTask_moreMenu_Ignore).setEnabled(true);
        popupMenu.getMenu().findItem(R.id.tabToday_cardTask_moreMenu_Drop).setEnabled(false);
    }

    private void setRunningMenu(PopupMenu popupMenu){
        popupMenu.getMenu().findItem(R.id.tabToday_cardTask_moreMenu_Start).setEnabled(false);
        popupMenu.getMenu().findItem(R.id.tabToday_cardTask_moreMenu_Stop).setEnabled(true);
        popupMenu.getMenu().findItem(R.id.tabToday_cardTask_moreMenu_Ignore).setEnabled(false);
        popupMenu.getMenu().findItem(R.id.tabToday_cardTask_moreMenu_Drop).setEnabled(true);
    }

    private void setDropCompleteIgnoreMenu(PopupMenu popupMenu){
        popupMenu.getMenu().findItem(R.id.tabToday_cardTask_moreMenu_Start).setEnabled(false);
        popupMenu.getMenu().findItem(R.id.tabToday_cardTask_moreMenu_Stop).setEnabled(false);
        popupMenu.getMenu().findItem(R.id.tabToday_cardTask_moreMenu_Ignore).setEnabled(false);
        popupMenu.getMenu().findItem(R.id.tabToday_cardTask_moreMenu_Drop).setEnabled(false);
    }
}
