package com.koenhabets.survur.server;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class LcdHandler {
    private int d = 0;
    private int counter = 500;
    static boolean lcdOn = false;

    public LcdHandler() {
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new UpdateTask(), 0, 10 * 1000);
    }

    public static void printLcd(String text, String text2) {
        ExecuteShellCommand com = new ExecuteShellCommand();
        com.executeCommand("python /home/pi/scripts/text.py " + text + " " + text2);
        lcdOn = true;
    }

    public static void disableBacklight() {
        if (lcdOn) {
            ExecuteShellCommand com = new ExecuteShellCommand();
            com.executeCommand("python /home/pi/scripts/disablelight.py");
        }
    }

    private class UpdateTask extends TimerTask {
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
                    printLcd(hour + ":" + minute, "In:" + TemperatureHandler.round(temp, 2));
                    d = 1;
                } else {
                    printLcd(hour + ":" + minute, "Out:" + TemperatureHandler.round(tempOutside, 2));
                    d = 0;
                }
            }

            if (counter == 6) {
                disableBacklight();
            }
        }
    }
}
