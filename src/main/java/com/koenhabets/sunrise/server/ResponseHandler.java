package com.koenhabets.sunrise.server;

import com.koenhabets.sunrise.server.ScholicaApi.calendarScholica;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
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
            calendarScholica.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Objects.equals(parts2[1], "Prep-Sleep")) {
            if (Objects.equals(parts2[0], "ja")) {
                try {
                    if (calendarScholica.count == 1) {
                        CalendarHandler.setAlarm("08", "05");
                        VoiceHandler.sendPost("Oke je hebt het eerste uur vrij, het alarm gaat om 08:05", "voice");
                        LcdHandler.printLcd("Welterusten", "Alarm:08:05");
                    } else if (calendarScholica.count == 2) {
                        CalendarHandler.setAlarm("09", "10");
                        VoiceHandler.sendPost("Oke je hebt het eerste en tweede uur vrij, het alarm gaat om 09:10", "voice");
                        LcdHandler.printLcd("Welterusten", "Alarm:09:10");
                    } else {
                        CalendarHandler.setAlarm("07", "20");
                        VoiceHandler.sendPost("Oke, het alarm gaat om 07:20", "voice");
                        LcdHandler.printLcd("Welterusten", "Alarm:07:20");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    if (calendarScholica.count == 1) {
                        CalendarHandler.setAlarm("08", "15");
                        VoiceHandler.sendPost("Oke je hebt het eerste uur vrij, het alarm gaat om 08:15", "voice");
                        LcdHandler.printLcd("Welterusten", "Alarm:08:15");
                    } else if (calendarScholica.count == 2) {
                        CalendarHandler.setAlarm("09", "15");
                        VoiceHandler.sendPost("Oke je hebt het eerste en tweede uur vrij, het alarm gaat om 09:15", "voice");
                        LcdHandler.printLcd("Welterusten", "Alarm:09:15");
                    } else {
                        CalendarHandler.setAlarm("07", "25");
                        VoiceHandler.sendPost("Oke, het alarm gaat om 07:25", "voice");
                        LcdHandler.printLcd("Welterusten", "Alarm:07:25");
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
