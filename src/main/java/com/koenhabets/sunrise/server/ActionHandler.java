package com.koenhabets.sunrise.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class ActionHandler implements HttpHandler {
    String response = "sent";
    private int code = 200;
    static boolean sleeping = false;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        String[] parts = parm.split("=");
        String action = parts[1];
        if (Objects.equals(action, "Wake-up")){
            try {
                int temp = WeatherHandler.getTemp();
                VoiceHandler.sendPost("Goedemorgen Koen. Het is "+ temp + " graden buiten. Je volgende afspraak is: "+ CalendarHandler.getResponse(), "voice");
                VoiceHandler.sendPost("scholica","app");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(Objects.equals(action, "Prep-Sleep")){
                //LcdHandler.disableBacklight();
                sleeping = true;
                try {
                    //// TODO: 10/5/2016 Controle of ik het eerste uur vrij heb
                    VoiceHandler.sendPost("Welterusten. Wil je morgen douchen?;Prep-Sleep", "voice");
                    VoiceHandler.sendPost("", "response");
                } catch (Exception e) {
                    code = 500;
                    e.printStackTrace();
                }
                try {
                    VoiceHandler.sendPost("Welterusten. Wil je morgen douchen?;Prep-Sleep", "voice");
                    VoiceHandler.sendPost("", "response");
                } catch (Exception e) {
                    code = 500;
                    e.printStackTrace();
                }
        }
        httpExchange.sendResponseHeaders(code, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
