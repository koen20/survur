package com.koenhabets.sunrise.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Objects;

public class ResponseHandler implements HttpHandler {
    int code = 200;
    String response = "Sent";
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        String[] parts = parm.split("=");
        String[] parts2 = parts[1].split(";");
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if(Objects.equals(parts2[1], "Prep-Sleep")){
            if(Objects.equals(parts2[0], "ja")){
                try {
                    if(day == 4){
                        CalendarHandler.setAlarm("08","05");
                        VoiceHandler.sendPost("Oke, het alarm gaat om 08:05", "voice");
                    } else {
                        CalendarHandler.setAlarm("07","20");
                        VoiceHandler.sendPost("Oke, het alarm gaat om 07:20", "voice");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    if (day == 4) {
                        CalendarHandler.setAlarm("08", "15");
                        VoiceHandler.sendPost("Oke, het alarm gaat om 08:15", "voice");
                    } else {
                        CalendarHandler.setAlarm("07", "25");
                        VoiceHandler.sendPost("Oke, het alarm gaat om 07:25", "voice");
                    }
                } catch (Exception e) {
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
