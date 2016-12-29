package com.koenhabets.sunrise.server;

import com.koenhabets.sunrise.server.ScholicaApi.calendarScholica;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class ActionHandler implements HttpHandler {
    String response = "sent";
    private int code = 200;
    static boolean sleeping = false;
    static boolean inside = true;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        String[] parts = parm.split("=");
        String action = parts[1];
        if (Objects.equals(action, "Wake-up")) {
            sleeping = false;
            try {
                int temp = WeatherHandler.getTemp();
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
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
            Calendar cal = Calendar.getInstance();
            weekDay = dayFormat.format(cal.getTime());
            if (!Objects.equals(weekDay, "Saturday") & !Objects.equals(weekDay, "Sunday") & calendarScholica.count < 500) {
                try {
                    VoiceHandler.sendPost("Welterusten. Wil je morgen douchen?;Prep-Sleep", "voice");
                    VoiceHandler.sendPost("", "response");
                } catch (Exception e) {
                    code = 500;
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
            }
        }
        httpExchange.sendResponseHeaders(code, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
