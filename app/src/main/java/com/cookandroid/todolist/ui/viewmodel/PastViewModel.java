package com.cookandroid.todolist.ui.viewmodel;

import java.util.Calendar;

import androidx.lifecycle.ViewModel;

import com.cookandroid.todolist.data.TSLiveData;

public class PastViewModel extends ViewModel {
    public TSLiveData<Calendar> mCalendar = new TSLiveData<>();

    public void setCalendar(Calendar calendar) {
        this.mCalendar.setValue(calendar);
    }

}
