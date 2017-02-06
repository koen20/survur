package com.koenhabets.survur.server;

import com.koenhabets.survur.server.ScholicaApi.calendarScholica;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class ActionHandler implements HttpHandler {
    static boolean sleeping = false;
    static boolean inside = true;
    String response = "sent";
    private int code = 200;
    static int hour;
    static int minute;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        String[] parts = parm.split("=");
        String action = parts[1];
        calendarScholica.update();
        if (Objects.equals(action, "Wake-up")) {
            sleeping = false;
            try {
                double temp = TemperatureHandler.tempOutside;
                String nextSubject = calendarScholica.nextSubject;
                if (Objects.equals(nextSubject, "geen les")) {
                    VoiceHandler.sendPost("Goedemorgen Koen. Het is " + temp + " graden buiten. Je volgende afspraak is: " + CalendarHandler.getResponse(), "voice");
                } else {
                    VoiceHandler.sendPost("Goedemorgen Koen. Het is " + temp + " graden buiten. Je volgende afspraak is: " + CalendarHandler.getResponse() +
                            ". Je hebt dalijk: " + nextSubject, "voice");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Objects.equals(action, "Prep-Sleep")) {
            LcdHandler.printLcd("Welterusten", ".");
            sleeping = true;
            String weekDay;
            try {
                calendarScholica.update();
            } catch (Exception e) {
                e.printStackTrace();
            }
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
            Calendar cal = Calendar.getInstance();
            weekDay = dayFormat.format(cal.getTime());
            System.out.println(calendarScholica.count);
            if (!Objects.equals(weekDay, "Saturday") & !Objects.equals(weekDay, "Sunday") & calendarScholica.count < 500) {
                try {
                    if (calendarScholica.count == 1) {
                        hour = 8;
                        minute = 5;
                        CalendarHandler.setAlarm("08", "05");
                        VoiceHandler.sendPost("Je hebt het eerste uur vrij, het alarm gaat om 08:05", "voice");
                        LcdHandler.printLcd("Welterusten", "Alarm:08:05");
                    } else if (calendarScholica.count == 2) {
                        hour = 9;
                        minute = 10;
                        CalendarHandler.setAlarm("09", "10");
                        VoiceHandler.sendPost("Je hebt het eerste en tweede uur vrij, het alarm gaat om 09:10", "voice");
                        LcdHandler.printLcd("Welterusten", "Alarm:09:10");
                    } else {
                        hour = 7;
                        minute = 20;
                        CalendarHandler.setAlarm("07", "20");
                        VoiceHandler.sendPost("Het alarm gaat om 07:20", "voice");
                        LcdHandler.printLcd("Welterusten", "Alarm:07:20");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    VoiceHandler.sendPost("Welterusten.", "voice");
                } catch (Exception e) {
                    code = 500;
                    e.printStackTrace();
                }
            }
        } else if (Objects.equals(action, "Sleep")) {
            sleeping = true;
            LcdHandler.disableBacklight();
        } else if (Objects.equals(action, "Enter")) {
            if (!sleeping) {
                inside = true;
            }
        } else if (Objects.equals(action, "Leave")) {
            if (!sleeping) {
                inside = false;
                lightsHandler.resetLights();
            }
        }
        httpExchange.sendResponseHeaders(code, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
