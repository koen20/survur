package com.koenhabets.survur.server;

import spark.Request;
import spark.Response;

import java.util.*;

import static com.koenhabets.survur.server.LightsHandler.Light;
import static com.koenhabets.survur.server.LightsHandler.fadeLedStrip;
import static com.koenhabets.survur.server.LightsHandler.setLedStrip;

public class SleepHandler {
    static boolean sleeping = false;
    static long sleepingStartTime = 0;

    public SleepHandler() {
        Timer updateTimerRoom = new Timer();
        updateTimerRoom.scheduleAtFixedRate(new updateSleeping(), 0, 5 * 60 * 1000);
    }

    private class updateSleeping extends TimerTask {
        @Override
        public void run() {
            Calendar cal = Calendar.getInstance();
            if (cal.getTimeInMillis() - sleepingStartTime > 43200000) {
                setSleeping(false);
            }
        }
    }

    public static void setSleeping(boolean status) {
        if (status) {
            if (!sleeping) {
                Calendar cal = Calendar.getInstance();
                sleepingStartTime = cal.getTimeInMillis();
                //Light("Aoff");
                Light("B", false);
                Light("C", false);
                Thread t = new Thread(() -> {
                    try {
                        fadeLedStrip(90, 0, 0, 60001);
                        Thread.sleep(90 * 1000);
                        setLedStrip(50, 0, 0);
                        Thread.sleep(50000);
                        setLedStrip(40, 0, 0);
                        Thread.sleep(40000);
                        setLedStrip(30, 0, 0);
                        Thread.sleep(5000);
                        setLedStrip(0, 0, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                });
                t.start();
            }
            sleeping = true;
        } else {
            sleeping = false;
        }
    }

    public String action(Request request, Response response) {
        String action = request.queryParams("action");
        if (Objects.equals(action, "Wake-up")) {
            sleeping = false;
        } else if (Objects.equals(action, "Sleep")) {
            sleeping = true;
            LcdHandler.disableBacklight();
        }
        return "";
    }
}
