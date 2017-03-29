package com.koenhabets.survur.server;

import com.koenhabets.survur.server.ScholicaApi.calendarScholica;
import spark.Request;
import spark.Response;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ActionHandler {
    static boolean sleeping = false;
    static boolean inside = true;
    static int hour;
    static int minute;

    public String action(Request request, Response response) {
        String action = request.queryParams("action");
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
            prepSleep();
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
                LightsHandler.resetLights();
            }
        }
        return "";
    }
    static void prepSleep(){
        LcdHandler.printLcd("Welterusten", ".");
        sleeping = true;
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        Calendar cal = Calendar.getInstance();
        weekDay = dayFormat.format(cal.getTime());
        System.out.println(calendarScholica.count);
        if (!Objects.equals(weekDay, "Friday") && !Objects.equals(weekDay, "Saturday") && calendarScholica.count < 500 && ConfigHandler.alarmEnabled) {
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
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                minute = minute - 2;
                String dateStringOn = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":00";
                minute = minute + 7;
                String dateStringOff = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":00";
                System.out.println();
                setOff(dateStringOff);
                setOn(dateStringOn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                VoiceHandler.sendPost("Welterusten.", "voice");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        int hourC = cal.get(Calendar.HOUR);
        int minuteC = cal.get(Calendar.MINUTE) + 2;
        String dateString = year + "-" + month + "-" + day + " " + hourC + ":" + minuteC + ":00";
        try {
            setOff(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static void setOff(String dateString) throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormatter .parse(dateString);

        //Now create the time and schedule it
        Timer timer = new Timer();

        //Use this if you want to execute it once
        timer.schedule(new lightsOff(), date);
    }

    public static void setOn(String dateString) throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormatter .parse(dateString);

        //Now create the time and schedule it
        Timer timer = new Timer();

        //Use this if you want to execute it once
        timer.schedule(new lightsOn(), date);
    }

    private static class lightsOff extends TimerTask
    {

        public void run()
        {
            LightsHandler.Light("Boff");
        }
    }
    private static class lightsOn extends TimerTask{

        @Override
        public void run() {
            LightsHandler.Light("Bon");
        }
    }
}
