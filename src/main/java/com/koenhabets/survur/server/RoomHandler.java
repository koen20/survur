package com.koenhabets.survur.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class RoomHandler implements HttpHandler {
    static boolean insideRoom = false;
    String response;

    public RoomHandler() {
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new CheckWifi(), 0, 3 * 60 * 1000);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        try {
            if (Objects.equals(parm, "enter")) {
                insideRoom = true;
                VoiceHandler.sendPost("Hallo", "voice");
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
            if(Objects.equals(d, "")){
                ActionHandler.inside = false;
            } else {
                ActionHandler.inside = true;
            }
        }
    }
}
