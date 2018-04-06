package com.koenhabets.survur.server;

import com.koenhabets.survur.server.ZermeloApi.calendarZermelo;
import spark.Request;
import spark.Response;

import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class RoomHandler {
    static boolean insideRoom = false;
    static String lastMovement;
    private int countIn = 0;
    private int countOut = 0;
    private static long milisecondsLast = 0;

    public RoomHandler() {
        //Timer updateTimer = new Timer();
        //updateTimer.scheduleAtFixedRate(new CheckWifi(), 0, 7 * 60 * 1000);

        Timer updateTimerRoom = new Timer();
        updateTimerRoom.scheduleAtFixedRate(new UpdateInside(), 0, 10 * 1000);
    }

    public String action(Request request, Response response) {
        String parm = request.queryParams("action");
        /*try {
            if (Objects.equals(parm, "enter") & ConfigHandler.motionEnabled) {
                Calendar cal = Calendar.getInstance();
                int Cday = cal.get(Calendar.DAY_OF_MONTH);
                int Cminute = cal.get(Calendar.MINUTE);
                int Chour = cal.get(Calendar.HOUR_OF_DAY);
                lastMovement = Chour + ":" + Cminute + " day:" + Cday;
                long Cmiliseconds = cal.getTimeInMillis();
                long milisecondsDif = Cmiliseconds - miliseconds;
                if (milisecondsDif < 30 * 1000) {
                    if (!insideRoom && !ActionHandler.sleeping && ActionHandler.inside) {
                        if (Chour > SunSetHandler.sunsetHour) {
                            LightsHandler.Light("Aon");
                            LightsHandler.Light("Bon");
                        }
                        if (calendarZermelo.count < 5 && ConfigHandler.alarmEnabled) {
                            if (Chour == 21 && Cminute > 40 || Chour == 22 || Chour == 23) {
                                WebSocket2.voiceListen("Ga je nu slapen?");
                                ResponseHandler.lastAction = "enterLate";
                            }
                        }
                        if (calendarZermelo.count > 499) {
                            if (Chour >= 22 && Cminute > 25 || Chour < 4) {
                                WebSocket2.voiceListen("Ga je nu slapen?");
                                ResponseHandler.lastAction = "enterLate";
                            }
                        }
                    }
                    insideRoom = true;

                }
                miliseconds = cal.getTimeInMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return "";
    }

    public static void enterRoom() {
        Calendar calNow = Calendar.getInstance();
        if (!insideRoom) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, SunSetHandler.sunsetHour);
            cal.set(Calendar.MINUTE, SunSetHandler.sunsetMinute);
            if (calNow.getTimeInMillis() > cal.getTimeInMillis() || calNow.get(Calendar.HOUR_OF_DAY) < 3) {
                if (!ActionHandler.sleeping) {
                    LightsHandler.Light("Bon");
                }
            }
        }
        milisecondsLast = calNow.getTimeInMillis();
        insideRoom = true;
    }

    private class CheckWifi extends TimerTask {
        @Override
        public void run() {
            ExecuteShellCommand com = new ExecuteShellCommand();
            String d = com.executeCommand("bash /home/pi/scripts/wifiScan");
            if (Objects.equals(d, "")) {
                countIn = 0;
                countOut++;
                if (countOut == 2 && !ActionHandler.sleeping) {
                    ActionHandler.inside = false;
                    LightsHandler.resetLights();
                }
            } else {
                countOut = 0;
                countIn++;
                if (countIn == 2) {
                    ActionHandler.inside = true;
                }
            }
        }
    }

    private class UpdateInside extends TimerTask {
        @Override
        public void run() {
            if (ConfigHandler.motionEnabled && !ActionHandler.sleeping) {
                Calendar cal = Calendar.getInstance();
                long Cmiliseconds = cal.getTimeInMillis();
                long milisecondsDif = Cmiliseconds - milisecondsLast;
                if (insideRoom) {
                    if (milisecondsDif > 120 * 1000) {
                        insideRoom = false;
                        LightsHandler.resetLights();
                    }
                }
            }
        }
    }
}
