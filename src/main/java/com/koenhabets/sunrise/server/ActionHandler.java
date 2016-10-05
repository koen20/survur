package com.koenhabets.sunrise.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Objects;

public class ActionHandler implements HttpHandler {
    String response = "sent";
    int code = 200;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        String[] parts = parm.split("=");
        String action = parts[1];
        if (Objects.equals(action, "Wake-up")){
            try {
                int temp = WeatherHandler.getTemp();
                VoiceHandler.sendPost("Goedemorgen Koen. Het is "+ temp + " graden buiten. Je volgende afspraak is: "+ CalendarHandler.getResponse(), "voice");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(Objects.equals(action, "Prep-Sleep")){
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            if(day == 4){
                try {
                    //// TODO: 10/5/2016 Controle of ik het eerste uur vrij heb 
                    CalendarHandler.setAlarm("08","15");
                    VoiceHandler.sendPost("Welterusten. Je heb het eerste uur vrij. Het alarm gaat om 08:15", "voice");
                } catch (Exception e) {
                    code = 500;
                    e.printStackTrace();
                }
            } else {
                try {
                    CalendarHandler.setAlarm("07","25");
                    VoiceHandler.sendPost("Welterusten. Het alarm gaat om 07:25", "voice");
                } catch (Exception e) {
                    code = 500;
                    e.printStackTrace();
                }
            }
        }
        httpExchange.sendResponseHeaders(code, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
