package com.koenhabets.survur.server;

import spark.Request;
import spark.Response;

import java.util.*;

public class SleepHandler {
    static boolean sleeping = false;
    static boolean inside = true;
    static long sleepingStartTime = 0;

    public SleepHandler() {
        Timer updateTimerRoom = new Timer();
        updateTimerRoom.scheduleAtFixedRate(new updateSleeping(), 0, 60 * 1000);
    }

    private class updateSleeping extends TimerTask {
        @Override
        public void run() {
            Calendar cal = Calendar.getInstance();
            if(cal.getTimeInMillis() - sleepingStartTime > 36000000){
                sleeping = false;
            }
        }
    }

    public static void setSleeping(boolean status){
        if(status){
            Calendar cal = Calendar.getInstance();
            sleepingStartTime = cal.getTimeInMillis();
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
