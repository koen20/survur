package com.koenhabets.survur.server;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class timer extends TimerTask {

    static void main() {
        TimerTask timerTask = new timer();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 24 * 1000);
    }

    @Override
    public void run() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (ActionHandler.hour == hour && ActionHandler.minute - 2 == minute && ActionHandler.inside){
            lightsHandler.Light("Bon");
        }
        if (ActionHandler.hour == hour && ActionHandler.minute + 5 == minute && ActionHandler.inside){
            lightsHandler.Light("Boff");
        }
    }
}