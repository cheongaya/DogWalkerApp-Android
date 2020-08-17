package com.example.dogwalker.decorators;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.example.dogwalker.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class NoneDaysDecorator implements DayViewDecorator {

    private final Drawable drawable;
    private int color;
    private HashSet<CalendarDay> dates;

//    public EventDecorator(int color, Collection<CalendarDay> dates, Activity context) {
//        drawable = context.getResources().getDrawable(R.drawable.more);
//        this.color = color;
//        this.dates = new HashSet<>(dates);
//    }

    public NoneDaysDecorator(int color, Collection<CalendarDay> dates, Activity context) {
        drawable = context.getResources().getDrawable(R.drawable.none_service_date_background);
        this.color = color; // 날자밑에 점 색상
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
//        view.addSpan(new DotSpan(5, color)); // 날자밑에 점
    }
}
