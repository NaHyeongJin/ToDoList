package com.cookandroid.todolist.ui.viewmodel;

import androidx.lifecycle.ViewModel;

import com.cookandroid.todolist.data.TSLiveData;

public class CalendarHeaderViewModel extends ViewModel {
    public TSLiveData<Long> mHeaderDate = new TSLiveData<>();

    public void setHeaderDate(long headerDate) {
        this.mHeaderDate.setValue(headerDate);
    }
}
