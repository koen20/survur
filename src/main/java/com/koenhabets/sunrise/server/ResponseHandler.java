package com.koenhabets.sunrise.server;

import com.koenhabets.sunrise.server.ScholicaApi.calendarScholica;
import com.koenhabets.sunrise.server.ScholicaApi.requestToken;
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
        try {
            requestToken.requestToken();
            calendarScholica.checkSchedule();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Objects.equals(parts2[1], "Prep-Sleep")) {
            if (Objects.equals(parts2[0], "ja")) {
                try {
                    if (calendarScholica.count == 1) {
                        CalendarHandler.setAlarm("08", "05");
                        VoiceHandler.sendPost("Oke, het alarm gaat om 08:05", "voice");
                    }else if (calendarScholica.count == 2) {
                        CalendarHandler.setAlarm("09", "10");
                        VoiceHandler.sendPost("Oke, het alarm gaat om 08:10", "voice");
                    } else {
                        CalendarHandler.setAlarm("07", "20");
                        VoiceHandler.sendPost("Oke, het alarm gaat om 07:20", "voice");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    if (calendarScholica.count == 1) {
                        CalendarHandler.setAlarm("08", "15");
                        VoiceHandler.sendPost("Oke, het alarm gaat om 08:15", "voice");
                    } else if (calendarScholica.count == 2) {
                        CalendarHandler.setAlarm("09", "15");
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
