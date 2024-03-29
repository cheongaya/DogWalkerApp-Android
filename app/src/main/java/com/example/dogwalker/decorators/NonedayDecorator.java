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

    public NonedayDecorator(CalendarDay calendarDay) {

//        date = CalendarDay.today();
//        date = CalendarDay.from(2020,7,26); //지정하려는 달에서 month -1 해준다
        // ex) 2020-8-23 에 점 찍으려면 CalendarDay.from(2020,7,23); 지정해줘야함
//        date = CalendarDay.from(2020,8,23);
        date = calendarDay;
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
