package com.koenhabets.sunrise.server;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class timer extends TimerTask {
    int d = 0;

    @Override
    public void run() {
        if (!ActionHandler.sleeping) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            String temp = TemperatureHandler.getTemp();
            int outsideTemp = WeatherHandler.getTemp();
            if (d == 0) {
                LcdHandler.printLcd(hour + ":" + minute, "Binnen:" + temp);
                d = 1;
            } else {
                LcdHandler.printLcd(hour + ":" + minute, "Buiten:" + outsideTemp);
                d = 0;
            }

        }
    }

    public static void main() {
        TimerTask timerTask = new timer();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 30 * 1000);
    }
}