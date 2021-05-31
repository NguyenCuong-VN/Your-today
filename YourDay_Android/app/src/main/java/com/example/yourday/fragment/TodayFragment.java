package com.example.yourday.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yourday.FirebaseDatabaseHelper;
import com.example.yourday.R;
import com.example.yourday.Receiver.AlarmReceiver;
import com.example.yourday.adapter.TaskTabTodayRecyclerViewAdapter;
import com.example.yourday.dialog.AddTaskTodayTabDialog;
import com.example.yourday.dialog.CompleteDayTodayTabDialog;
import com.example.yourday.dialog.FilterTodayTabDialog;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TodayFragment extends Fragment{
    RecyclerView recyclerView;
    List<Task> taskList;
    List<String> filterStatus;
    TaskTabTodayRecyclerViewAdapter taskTabTodayRecyclerViewAdapter;
    SearchView searchView;
    DateTask dateTask;
    ImageView imgFilter, imgAdd;
    TextView tvDateName, tvTotalTask, tvRemainTask;
    View moreView;
    FirebaseDatabaseHelper firebaseDatabaseHelper;

    public TodayFragment(){
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int date = calendar.get(Calendar.DATE);
        dateTask = new DateTask(Integer.toString(date)+"/"+Integer.toString(month)+"/"+Integer.toString(year), "date 1", false);
        generateTaskList();


        filterStatus = new ArrayList<String>();
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        recyclerView = view.findViewById(R.id.today_tab_rcv_taskList);
        recyclerView.setHasFixedSize(true);
        taskTabTodayRecyclerViewAdapter = new TaskTabTodayRecyclerViewAdapter(getContext(), dateTask, getFragmentManager());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(taskTabTodayRecyclerViewAdapter);

        tvDateName = view.findViewById(R.id.todayTab_dateName);
        tvDateName.setText(dateTask.getName());

        //filter handle
        imgFilter = view.findViewById(R.id.filter_search_today_tab);
        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterTodayTabDialog filterTodayTabDialog = new FilterTodayTabDialog(taskTabTodayRecyclerViewAdapter, filterStatus);
                filterTodayTabDialog.show(getFragmentManager(), "Filter today tab dialog");
            }
        });

        //add handle
        imgAdd = view.findViewById(R.id.today_tab_add_icon);
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dateTask.isComplete()){
                    AddTaskTodayTabDialog addTaskTodayTabDialog = new AddTaskTodayTabDialog(taskTabTodayRecyclerViewAdapter);
                    addTaskTodayTabDialog.show(getFragmentManager(), "Add task today tab dialog");
                }
                else {
                    Toast.makeText(getContext(), "Ngày đã kết thúc", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //more menu
        moreView = view.findViewById(R.id.todayTab_moreView);
        moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), moreView);
                popupMenu.inflate(R.menu.today_tab_more_menu);
                popupMenu.getMenu().findItem(R.id.tabToday_moreMenu_Endday).setEnabled(!dateTask.isComplete());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().toString().equals("Save day")){
                            Toast.makeText(getContext(), "Save day", Toast.LENGTH_SHORT).show();
                        }
                        else if (item.getTitle().toString().equals("End day")){
                            dateTask.setComplete(true);
                            firebaseDatabaseHelper.setTodayStatus(dateTask);
                            int completeNum = 0, dropNum = 0, ignoreNum = 0, iCompletePercent = 0, iDropPercent = 0, iIgnorePercent = 0;
//                            for (Task task : dateTask.getTaskList()){
                            for (int i=0; i< dateTask.getTaskList().size(); i++){
                                Task task = dateTask.getTaskList().get(i);
                                Log.d("tag", task.getName());
                                //set all task complete
                                if (task.getStatus().equals(getString(R.string.coming_status))){
                                    Task oldTask = new Task(task.getId(), task.getName(), task.getNote(), task.getStartTime(), task.getStopTime(), task.getStatus(), task.getRealStartTime(), task.getRealStopTime());
                                    task.setStatus(getString(R.string.ignore_status));
                                    taskTabTodayRecyclerViewAdapter.changeTask(oldTask, task);
                                }
                                else if (task.getStatus().equals(getString(R.string.running_status))){
                                    Task oldTask = new Task(task.getId(), task.getName(), task.getNote(), task.getStartTime(), task.getStopTime(), task.getStatus(), task.getRealStartTime(), task.getRealStopTime());
                                    task.setStatus(getString(R.string.drop_status));
                                    String realStopTime = Calendar.getInstance().getTime().getHours()+":"+Calendar.getInstance().getTime().getMinutes();
                                    task.setRealStopTime(realStopTime);
                                    taskTabTodayRecyclerViewAdapter.changeTask(oldTask, task);
                                }
                                //get statistic
                                if (task.getStatus().equals(getString(R.string.complete_status))){
                                    completeNum+=1;
                                }
                                else if (task.getStatus().equals(getString(R.string.drop_status))){
                                    dropNum+=1;
                                }
                                else if (task.getStatus().equals(getString(R.string.ignore_status))){
                                    ignoreNum+=1;
                                }
                            }

                            //calcu percent
                            double onePercent = (completeNum+dropNum+ignoreNum) / 100.0f;
                            iCompletePercent = (int) (completeNum/onePercent);
                            iDropPercent = (int) (dropNum/onePercent);
                            iIgnorePercent = (int) (ignoreNum/onePercent);
//                            taskTabTodayRecyclerViewAdapter.setTaskList(dateTask.getTaskList());

                            //create alert dialog
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("dateTask", dateTask);
                            bundle.putInt("completeNum", completeNum);
                            bundle.putInt("dropNum", dropNum);
                            bundle.putInt("ignoreNum", ignoreNum);
                            bundle.putInt("iCompletePercent", iCompletePercent);
                            bundle.putInt("iDropPercent", iDropPercent);
                            bundle.putInt("iIgnorePercent", iIgnorePercent);
                            CompleteDayTodayTabDialog completeDayTodayTabDialog = new CompleteDayTodayTabDialog();
                            completeDayTodayTabDialog.setArguments(bundle);
                            completeDayTodayTabDialog.show(getFragmentManager(), "statistic day");
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        //search handle
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = view.findViewById(R.id.search_today_tab);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                taskTabTodayRecyclerViewAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                taskTabTodayRecyclerViewAdapter.filter(newText);
                return false;
            }
        });

        //firebase
        //get date
        firebaseDatabaseHelper = new FirebaseDatabaseHelper();
        firebaseDatabaseHelper.getDateToday(dateTask, tvDateName);
        //get task list a day
        firebaseDatabaseHelper.getAllTaskADay(dateTask, taskTabTodayRecyclerViewAdapter);

        return view;
    }

    private void generateTaskList(){
        taskList = new ArrayList<Task>();
        dateTask.setTaskList(taskList);
    }

}
