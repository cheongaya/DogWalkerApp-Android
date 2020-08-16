package com.example.dogwalker.decorators;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.dogwalker.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

public class Decorator implements DayViewDecorator {

    Context context;

    private final Calendar calendar = Calendar.getInstance();
    private final Drawable highlightDrawable;
    private CalendarDay date;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public Decorator() {
        //오늘 날짜를 파랑으로 표시해준다
        highlightDrawable = new ColorDrawable(context.getResources().getColor(R.color.colorBlue,context.getTheme()));
//            date = CalendarDay.today();
        date = CalendarDay.from(calendar);
    }

    //캘린더의 모든 날짜를 띄울때 decoration 이 필요한지 판단하여 출력해준다
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return date != null && day.equals(date);
    }

    //네가지 메소드를 통해 커스터마이징 옵션을 제공하고 커스마이제이션을 얻어올때 한번만 호출된다
    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(highlightDrawable);
    }
}