package com.cookandroid.todolist.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.cookandroid.todolist.R;

import com.cookandroid.todolist.databinding.CalendarHeaderBinding;
import com.cookandroid.todolist.databinding.DayItemBinding;
import com.cookandroid.todolist.databinding.EmptyDayBinding;
import com.cookandroid.todolist.databinding.PastDayBinding;
import com.cookandroid.todolist.ui.viewmodel.CalendarHeaderViewModel;
import com.cookandroid.todolist.ui.viewmodel.CalendarViewModel;
import com.cookandroid.todolist.ui.viewmodel.EmptyViewModel;
import com.cookandroid.todolist.ui.viewmodel.PastViewModel;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


public class CalendarAdapter extends ListAdapter<Object, RecyclerView.ViewHolder> {
    private final int HEADER_TYPE = 0;
    private final int EMPTY_TYPE = 1;
    private final int PAST_TYPE = 2;
    private final int DAY_TYPE = 3;


    public CalendarAdapter() {
        super(new DiffUtil.ItemCallback<Object>() {
            @Override
            public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                return oldItem == newItem;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                Gson gson = new Gson();
                return gson.toJson(oldItem).equals(gson.toJson(newItem));
            }
        });
    }


    @Override
    public int getItemViewType(int position) { //뷰타입 나누기
        Object item = getItem(position);

        if (item instanceof Long) {
            return HEADER_TYPE; //날짜 타입
        } else if (item instanceof String) {
            return EMPTY_TYPE; // 비어있는 일자 타입
        } else {
            if(item instanceof GregorianCalendar) {
                Calendar calendar = (GregorianCalendar) item;
                String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
                SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, ids[0]);
                Calendar realtime = new GregorianCalendar(pdt);
                Date trialTime = new Date();
                realtime.setTime(trialTime);
                if (isPastDay(calendar, realtime)) return PAST_TYPE; // 지난 일자 타입
            }
            return DAY_TYPE; // 일자 타입
        }
    }

    public boolean isPastDay(Calendar calendar, Calendar realtime) { // calendar가 realtime보다 과거면 true 아니면 false
        if (calendar.get(calendar.YEAR) < realtime.get(realtime.YEAR)) return true;
        else if (calendar.get(calendar.YEAR) == realtime.get(realtime.YEAR) && calendar.get(calendar.MONTH) < realtime.get(realtime.MONTH)) return true;
        else if (calendar.get(calendar.YEAR) == realtime.get(realtime.YEAR) && calendar.get(calendar.MONTH) == realtime.get(realtime.MONTH) && calendar.get(calendar.DAY_OF_MONTH) <= realtime.get(realtime.DAY_OF_MONTH)) return true;
        return false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) { // 날짜 타입
            CalendarHeaderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_calendar_header, parent, false);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(true); //Span을 하나로 통합하기
            binding.getRoot().setLayoutParams(params);
            return new HeaderViewHolder(binding);
        } else if (viewType == EMPTY_TYPE) { //비어있는 일자 타입
            EmptyDayBinding binding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_day_empty, parent, false);
            return new EmptyViewHolder(binding);
        } else if (viewType == PAST_TYPE) {
            PastDayBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_day_past, parent, false);// 지난 일자 타입
            return new PastViewHolder(binding);
        }

        DayItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_day, parent, false);// 일자 타입

        return new DayViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == HEADER_TYPE) { //날짜 타입 꾸미기
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
            Object item = getItem(position);
            CalendarHeaderViewModel model = new CalendarHeaderViewModel();
            if (item instanceof Long) {
                model.setHeaderDate((Long) item);
            }
            holder.setViewModel(model);
        } else if (viewType == EMPTY_TYPE) { //비어있는 날짜 타입 꾸미기
            EmptyViewHolder holder = (EmptyViewHolder) viewHolder;
            EmptyViewModel model = new EmptyViewModel();
            holder.setViewModel(model);
        } else if (viewType == PAST_TYPE) { // 지난 날짜 타입 꾸미기
            PastViewHolder holder = (PastViewHolder) viewHolder;
            Object item = getItem(position);
            PastViewModel model = new PastViewModel();
            if (item instanceof Calendar) {
                model.setCalendar((Calendar) item);
            }
            holder.setViewModel(model);
        } else if (viewType == DAY_TYPE) { // 일자 타입 꾸미기
            DayViewHolder holder = (DayViewHolder) viewHolder;
            Object item = getItem(position);
            CalendarViewModel model = new CalendarViewModel();
            if (item instanceof Calendar) {
                model.setCalendar((Calendar) item);
            }
            holder.setViewModel(model);
        }
    }


    private class HeaderViewHolder extends RecyclerView.ViewHolder { //날짜 타입 ViewHolder
        private CalendarHeaderBinding binding;

        private HeaderViewHolder(@NonNull CalendarHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setViewModel(CalendarHeaderViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }
    }


    private class EmptyViewHolder extends RecyclerView.ViewHolder { // 비어있는 요일 타입 ViewHolder
        private EmptyDayBinding binding;

        private EmptyViewHolder(@NonNull EmptyDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setViewModel(EmptyViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }

    }

    private class PastViewHolder extends RecyclerView.ViewHolder {// 요일 타입 ViewHolder
        private PastDayBinding binding;

        private PastViewHolder(@NonNull PastDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setViewModel(PastViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }
    }

    private class DayViewHolder extends RecyclerView.ViewHolder {// 요일 타입 ViewHolder
        private DayItemBinding binding;

        private DayViewHolder(@NonNull DayItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setViewModel(CalendarViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }
    }
}
