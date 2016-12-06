package com.koenhabets.sunrise.server;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.util.Calendar;


public class SunSetHandler implements HttpHandler {
    String sunset;
    String sunrise;
    String response;

    @Override
    public void handle(HttpExchange he) throws IOException {
        updateSunriseSunset();
        String[] parts = sunset.split(":");
        String part1 = parts[0];
        String part2 = parts[1];
        int sunset1 = Integer.parseInt(part1) + 1;
        int sunset2 = Integer.parseInt(part2);
        response = sunset1 + "." + sunset2;

        String[] Spart = sunrise.split(":");
        String Spart1 = Spart[0];
        String Spart2 = Spart[1];
        int sunrise1 = Integer.parseInt(Spart1) + 1;
        int sunrise2 = Integer.parseInt(Spart2);
        response += ";" + sunrise1 + "." + sunrise2;

        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void updateSunriseSunset() {
        Location location = new Location("50.903819", "6.029882");
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, "Nederland/Amsterdam");
        sunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        sunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());
    }
}