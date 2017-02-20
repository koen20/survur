package com.koenhabets.survur.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class RoomHandler implements HttpHandler {
    static boolean insideRoom = false;
    String response = ":)";
    int minute = 100;
    int day = 65;
    int hour = 100;
    int countIn = 0;
    int countOut = 0;

    public RoomHandler() {
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new CheckWifi(), 0, 7 * 60 * 1000);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        try {
            if (Objects.equals(parm, "enter")) {

                Calendar cal = Calendar.getInstance();
                int Cday = cal.get(Calendar.DAY_OF_MONTH);
                int Cminute = cal.get(Calendar.MINUTE);
                int Chour = cal.get(Calendar.HOUR_OF_DAY);
                int minuteDif = Cminute - minute;
                if(minuteDif <= 2 && Chour == hour && Cday == day){
                    insideRoom = true;
                    //VoiceHandler.sendPost("Hallo", "voice");
                } else {
                    day = cal.get(Calendar.DAY_OF_MONTH);
                    minute = cal.get(Calendar.MINUTE);
                    hour = cal.get(Calendar.HOUR_OF_DAY);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private class CheckWifi extends TimerTask {


        @Override
        public void run() {
            ExecuteShellCommand com = new ExecuteShellCommand();
            String d = com.executeCommand("bash /home/pi/scripts/wifiScan");
            if (Objects.equals(d, "")) {
                countIn = 0;
                countOut++;
                if(countOut == 2){
                    ActionHandler.inside = false;
                    lightsHandler.resetLights();
                }
            } else {
                countOut = 0;
                countIn++;
                if(countIn == 2){
                    ActionHandler.inside = true;
                }
            }
        }
    }
}
