package com.koenhabets.survur.server;

import com.koenhabets.survur.server.ScholicaApi.calendarScholica;
import spark.Request;
import spark.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ActionHandler {
    static boolean sleeping = false;
    static boolean inside = true;
    static int hour;
    static int minute;

    static void prepSleep() {
        LcdHandler.printLcd("Welterusten", ".");
        sleeping = true;
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        weekDay = dayFormat.format(cal.getTime());
        if (!Objects.equals(weekDay, "Friday") && !Objects.equals(weekDay, "Saturday") && calendarScholica.count < 500 && ConfigHandler.alarmEnabled) {
            try {
                if (calendarScholica.count == 1) {
                    hour = 8;
                    minute = 5;
                    CalendarHandler.setAlarm("08", "05");
                    LcdHandler.printLcd("Welterusten", "Alarm:08:05");
                } else if (calendarScholica.count == 2) {
                    hour = 9;
                    minute = 10;
                    CalendarHandler.setAlarm("09", "10");
                    LcdHandler.printLcd("Welterusten", "Alarm:09:10");
                } else {
                    hour = 7;
                    minute = 20;
                    CalendarHandler.setAlarm("07", "20");
                    LcdHandler.printLcd("Welterusten", "Alarm:07:20");
                }
                Calendar calOn = Calendar.getInstance();
                Calendar calOff = Calendar.getInstance();
                calOn.set(Calendar.DAY_OF_MONTH, day + 1);
                calOff.set(Calendar.DAY_OF_MONTH, day + 1);
                calOn.set(Calendar.HOUR, hour);
                calOn.set(Calendar.MINUTE, minute - 1);
                calOff.set(Calendar.HOUR, hour);
                calOff.set(Calendar.MINUTE, minute + 5);
                if (inside) {
                    WebSocket2.voice("Het alarm gaat om " + hour + ":" + minute);
                    LcdHandler.printLcd("Welterusten", "Alarm:" + hour + ":" + minute);
                    setOff(calOff.getTime());
                    setOn(calOn.getTime());
                }
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
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + 3);
        try {
            setOff(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static void setOff(Date date) throws ParseException {
        Timer timer = new Timer();
        timer.schedule(new lightsOff(), date);
    }

    public static void setOn(Date date) throws ParseException {
        Timer timer = new Timer();
        timer.schedule(new lightsOn(), date);
    }

    public String action(Request request, Response response) {
        String action = request.queryParams("action");
        if (Objects.equals(action, "Wake-up")) {
            sleeping = false;
            try {
                double temp = TemperatureHandler.tempOutside;
                String nextSubject = calendarScholica.nextSubject;
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

    private static class lightsOff extends TimerTask {

        public void run() {
            LightsHandler.resetLights();
        }
    }

    private static class lightsOn extends TimerTask {

        @Override
        public void run() {
            LightsHandler.Light("Bon");
        }
    }
}
