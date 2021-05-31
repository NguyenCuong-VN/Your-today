package com.example.yourday.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Task implements Serializable {
    long id;
    String name, note, startTime, stopTime, status, realStartTime, realStopTime;

    public Task() {
    }

    public Task(long id, String name, String note, String startTime, String stopTime, String status, String realStartTime, String realStopTime) {
        this.id = id;
        this.name = name;
        this.note = note;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.status = status;
        this.realStartTime = realStartTime;
        this.realStopTime = realStopTime;
    }

    public Task(String name, String note, String startTime, String stopTime, String status, String realStartTime, String realStopTime) {
        this.name = name;
        this.note = note;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.status = status;
        this.realStartTime = realStartTime;
        this.realStopTime = realStopTime;
        //generate id here
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        try {
            Date date = dateFormat.parse(startTime);
            this.id = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRealStartTime() {
        return realStartTime;
    }

    public void setRealStartTime(String realStartTime) {
        this.realStartTime = realStartTime;
    }

    public String getRealStopTime() {
        return realStopTime;
    }

    public void setRealStopTime(String realStopTime) {
        this.realStopTime = realStopTime;
    }
}
