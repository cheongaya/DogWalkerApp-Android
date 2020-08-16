package com.example.dogwalker.decorators;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.example.dogwalker.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NonedayDecorator implements DayViewDecorator {

    private CalendarDay date;

    public NonedayDecorator() {

//        date = CalendarDay.today();
        date = CalendarDay.from(2020,8,23);
//        date = CalendarDay.from(2020,8,23);
    }


    @Override
    public boolean shouldDecorate(CalendarDay day) {
//        return date != null && day.equals(date);
        return date != null && day.equals(date);

    }

    @Override
    public void decorate(DayViewFacade view) {
//        view.addSpan(new StyleSpan(Typeface.BOLD));
//        view.addSpan(new RelativeSizeSpan(1.4f));
//        view.addSpan(new ForegroundColorSpan(Color.GREEN));
        view.addSpan(new DotSpan(20, Color.RED));
    }

//    /**
//     * We're changing the internals, so make sure to call {@linkplain MaterialCalendarView#invalidateDecorators()}
//     */
    public void setDate(Date date) {
        this.date = CalendarDay.from(date);
    }
}
