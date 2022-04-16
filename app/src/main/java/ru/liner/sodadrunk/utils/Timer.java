package ru.liner.sodadrunk.utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 16.04.2022, суббота
 **/
public class Timer {
    private Context context;
    private Callback callback;
    private java.util.Timer timer;
    private long startTime;
    private long endTime;
    private boolean active;

    public Timer(Context context) {
        this.context = context;
    }

    public void start(Callback callback){
        this.callback = callback;
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                (timePicker, hours, minutes) -> {
                    startTime = java.lang.System.currentTimeMillis();
                    endTime = startTime + TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes);
                    startInternal();
                },
                0,
                0,
                true
        );
        timePickerDialog.show();
    }

    public void restore(long startTime, long endTime, Callback callback){
        this.callback = callback;
        this.startTime = startTime;
        this.endTime = endTime;
        startInternal();
    }

    private void startInternal(){
        active = true;
        timer = new java.util.Timer();
        callback.onStart();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(endTime - java.lang.System.currentTimeMillis() <= 0) {
                    callback.onTimeLeft();
                    timer.cancel();
                    return;
                }
                callback.onTimeChanged(endTime - java.lang.System.currentTimeMillis());
            }
        }, 0, TimeUnit.SECONDS.toMillis(1));
    }

    public boolean isActive() {
        return active;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public float getPercent(){
        return getPercentInternal(startTime, endTime);
    }

    private static float getPercentInternal(long startTime, long endTime){
        return ((Float.parseFloat(String.valueOf((java.lang.System.currentTimeMillis()-startTime)))/Float.parseFloat(String.valueOf(endTime-startTime))) * 100f);
    }

    public static String convertSecondsToHMmSs(long s) {
        return String.format(Locale.getDefault(), "%d:%02d:%02d", TimeUnit.MILLISECONDS.toSeconds(s) / 3600, (TimeUnit.MILLISECONDS.toSeconds(s) % 3600) / 60, (TimeUnit.MILLISECONDS.toSeconds(s) % 60));
    }


    public interface Callback{
        void onTimeChanged(long timeLeft);
        default void onTimeLeft(){}
        default void onStart(){}
    }
}
