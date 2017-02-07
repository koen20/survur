package com.koenhabets.survur.server;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class timer extends TimerTask {

    private int d = 0;
    private int counter = 500;


    static void main() {
        TimerTask timerTask = new timer();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 10 * 1000);
    }

    @Override
    public void run() {
        double temp = TemperatureHandler.temp;
        double tempOutside = TemperatureHandler.tempOutside;
        counter++;
        if (ActionHandler.sleeping && counter >= 499) {
            counter = 0;
        }
        if (!ActionHandler.inside && counter >= 499) {
            counter = 0;
        } else if (ActionHandler.inside && !ActionHandler.sleeping) {
            counter = 500;
        }
        if (!ActionHandler.sleeping && ActionHandler.inside) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            if (d == 0) {
                LcdHandler.printLcd(hour + ":" + minute, "Binnen:" + temp);
                d = 1;
            } else {
                LcdHandler.printLcd(hour + ":" + minute, "Buiten:" + tempOutside);
                d = 0;
            }
        }

        if (counter == 6) {
            LcdHandler.disableBacklight();
        }

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