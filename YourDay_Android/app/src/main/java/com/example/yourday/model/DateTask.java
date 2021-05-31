package com.example.yourday.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateTask implements Serializable {
    long id;
    String date, name;
    List<Task> taskList;
    boolean complete;

    public DateTask(long id, String date, String name, boolean complete) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.complete = complete;
    }

    public DateTask() {
    }

    public DateTask(String date, String name, boolean complete) {
        this.date = date;
        this.name = name;
        this.complete = complete;
        //generate id here
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date tmp_date = dateFormat.parse(date);
            this.id = tmp_date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
