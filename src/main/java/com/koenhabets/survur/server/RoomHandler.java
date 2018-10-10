package com.koenhabets.survur.server;

import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class RoomHandler {
    static boolean insideRoom = false;
    static String lastMovement;
    private int countIn = 0;
    private int countOut = 0;
    private static long milisecondsLast = 0;
    private static boolean sunsetLight = false;

    public RoomHandler() {
        //Timer updateTimer = new Timer();
        //updateTimer.scheduleAtFixedRate(new CheckWifi(), 0, 7 * 60 * 1000);

        Timer updateTimerRoom = new Timer();
        updateTimerRoom.scheduleAtFixedRate(new UpdateInside(), 0, 10 * 1000);
    }

    public static void enterRoom() {
        Calendar calNow = Calendar.getInstance();
        if (!insideRoom) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, SunSetHandler.sunsetHour);
            cal.set(Calendar.MINUTE, SunSetHandler.sunsetMinute);
            if (calNow.getTimeInMillis() > cal.getTimeInMillis() || calNow.get(Calendar.HOUR_OF_DAY) < 7 ) {
                if (!SleepHandler.sleeping && !sunsetLight) {
                    if (!LightsHandler.ledStrip) {
                        LightsHandler.fadeLedStrip(255, 255, 255, 5000);
                    }
                    LightsHandler.Light("Aon");
                    sunsetLight = true;
                } else {
                    if (!LightsHandler.ledStrip) {
                        LightsHandler.fadeLedStrip(13, 13, 13, 700);
                    }
                }
            }
        }
        milisecondsLast = calNow.getTimeInMillis();
        insideRoom = true;
    }

    private class UpdateInside extends TimerTask {
        @Override
        public void run() {
            if (ConfigHandler.motionEnabled) {
                Calendar cal = Calendar.getInstance();
                long Cmiliseconds = cal.getTimeInMillis();
                long milisecondsDif = Cmiliseconds - milisecondsLast;
                if (insideRoom) {
                    if (milisecondsDif > 120 * 1000) {
                        insideRoom = false;
                        sunsetLight = false;
                        LightsHandler.resetLights();
                    }
                }
            }
        }
    }

    private class CheckWifi extends TimerTask {
        @Override
        public void run() {
            ExecuteShellCommand com = new ExecuteShellCommand();
            String d = com.executeCommand("bash /home/pi/scripts/wifiScan");
            if (Objects.equals(d, "")) {
                countIn = 0;
                countOut++;
                if (countOut == 2 && !SleepHandler.sleeping) {
                    SleepHandler.inside = false;
                    LightsHandler.resetLights();
                }
            } else {
                countOut = 0;
                countIn++;
                if (countIn == 2) {
                    SleepHandler.inside = true;
                }
            }
        }
    }
}
