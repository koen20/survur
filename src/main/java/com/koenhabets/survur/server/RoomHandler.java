package com.koenhabets.survur.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class RoomHandler {
    static boolean insideRoom = false;
    String response = ":)";
    private int minute = 100;
    private int day = 65;
    private int hour = 100;
    private int countIn = 0;
    private int countOut = 0;

    public RoomHandler() {
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new CheckWifi(), 0, 7 * 60 * 1000);

        Timer updateTimerRoom = new Timer();
        updateTimerRoom.scheduleAtFixedRate(new UpdateInside(), 0, 60 * 1000);
    }

    public String action(Request request, Response response){
        String parm = request.queryParams("action");
        try {
            if (Objects.equals(parm, "enter") & ConfigHandler.motionEnabled) {
                Calendar cal = Calendar.getInstance();
                int Cday = cal.get(Calendar.DAY_OF_MONTH);
                int Cminute = cal.get(Calendar.MINUTE);
                int Chour = cal.get(Calendar.HOUR_OF_DAY);
                int minuteDif = Cminute - minute;
                if (minuteDif <= 2 && Chour == hour && Cday == day) {
                    if (!insideRoom) {
                        //VoiceHandler.sendPost("Hallo", "voice");
                    }
                    insideRoom = true;

                }
                day = cal.get(Calendar.DAY_OF_MONTH);
                minute = cal.get(Calendar.MINUTE);
                hour = cal.get(Calendar.HOUR_OF_DAY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private class CheckWifi extends TimerTask {


        @Override
        public void run() {
            ExecuteShellCommand com = new ExecuteShellCommand();
            String d = com.executeCommand("bash /home/pi/scripts/wifiScan");
            if (Objects.equals(d, "")) {
                countIn = 0;
                countOut++;
                if (countOut == 2 && !ActionHandler.sleeping) {
                    ActionHandler.inside = false;
                    LightsHandler.resetLights();
                }
            } else {
                countOut = 0;
                countIn++;
                if (countIn == 2) {
                    ActionHandler.inside = true;
                }
            }
        }
    }

    private class UpdateInside extends TimerTask {


        @Override
        public void run() {
            if (ConfigHandler.motionEnabled) {
                Calendar cal = Calendar.getInstance();
                int Cday = cal.get(Calendar.DAY_OF_MONTH);
                int Cminute = cal.get(Calendar.MINUTE);
                int Chour = cal.get(Calendar.HOUR_OF_DAY);
                int minuteDif = Cminute - minute;
                int hourDif = Chour - hour;
                if (insideRoom) {
                    if (Cday == day && minuteDif < 10 && hourDif < 1) {

                    } else {
                        insideRoom = false;
                    }
                }
            }
        }
    }
}
