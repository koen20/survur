package com.koenhabets.survur.server;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;


public class SunSetHandler implements HttpHandler {

    private static final Location LOCATION = new Location("50.903819", "6.029882");
    private static final String TIMEZONE = "Nederland/Amsterdam";

    @Override
    public void handle(HttpExchange he) throws IOException {

        String response = "";
        int result = 0;

        try {
            response = parseSunriseSunset();
            result = 200;
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            result = 500;
        } finally {
            he.sendResponseHeaders(result, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private String parseSunriseSunset() throws NumberFormatException, IndexOutOfBoundsException {
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(LOCATION, TIMEZONE);
        String sunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String sunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());

        String result;
        String[] parts = sunset.split(":");
        String part1 = parts[0];
        String part2 = parts[1];
        int sunset1 = Integer.parseInt(part1) + 1;
        int sunset2 = Integer.parseInt(part2);
        result = sunset1 + "." + sunset2;

        String[] Spart = sunrise.split(":");
        String Spart1 = Spart[0];
        String Spart2 = Spart[1];
        int sunrise1 = Integer.parseInt(Spart1) + 1;
        int sunrise2 = Integer.parseInt(Spart2);
        result += ";" + sunrise1 + "." + sunrise2;

        return result;
    }
}