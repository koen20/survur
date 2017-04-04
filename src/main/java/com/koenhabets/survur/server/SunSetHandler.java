package com.koenhabets.survur.server;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import spark.Request;
import spark.Response;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class SunSetHandler {

    private static final Location LOCATION = new Location("50.903819", "6.029882");
    private static final String TIMEZONE = "Nederland/Amsterdam";
    public static int sunsetHour;
    public static int sunsetMinute;
    public static int sunriseHour;
    public static int sunriseMinute;
    private String result;

    public SunSetHandler() {
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new UpdateTask(), 0, 12 * 60 * 60 * 1000);
    }


    private String parseSunriseSunset() throws NumberFormatException, IndexOutOfBoundsException {
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(LOCATION, TIMEZONE);
        String sunriseO = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String sunsetO = calculator.getOfficialSunsetForDate(Calendar.getInstance());

        String[] parts = sunsetO.split(":");
        String part1 = parts[0];
        String part2 = parts[1];
        sunsetHour = Integer.parseInt(part1) + 1;
        sunsetMinute = Integer.parseInt(part2);
        result = sunsetHour + "." + sunsetMinute;

        String[] Spart = sunriseO.split(":");
        String Spart1 = Spart[0];
        String Spart2 = Spart[1];
        sunriseHour = Integer.parseInt(Spart1) + 1;
        sunriseMinute = Integer.parseInt(Spart2);
        result += ";" + sunriseHour + "." + sunriseMinute;

        return result;
    }

    public String getSunsetSunrise(Request request, Response response) {
        return result;
    }

    private class UpdateTask extends TimerTask {
        @Override
        public void run() {
            parseSunriseSunset();
        }
    }
}