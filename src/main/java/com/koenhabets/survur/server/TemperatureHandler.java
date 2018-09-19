package com.koenhabets.survur.server;

import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class TemperatureHandler {
    static double temp = 20;
    static double livingRoomTemp = 20;
    static double tempOutside;
    String response;

    public TemperatureHandler() {
        Timer updateTimer = new Timer();
        Timer updateTimer2 = new Timer();
        updateTimer.scheduleAtFixedRate(new UpdateTask(), 0, 120 * 1000);
        updateTimer2.scheduleAtFixedRate(new updateDb(), 17000, 900000);

    }

    private class updateDb extends TimerTask {

        @Override
        public void run() {
            insertTempDb(temp,tempOutside);
        }
    }

    private void insertTempDb(double temp, double tempOutside){
        try {
            Calendar cal = Calendar.getInstance();
            Statement stmt = PowerData.conn.createStatement();
            double dif = round(temp - tempOutside, 2);
            stmt.executeUpdate("INSERT INTO temperature VALUES ('" + PowerData.getMysqlDateString(cal.getTimeInMillis()) + "', '" + temp + "', '" + tempOutside + "', '" + dif + "')");
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private double getTemp() {
        String d;
        ExecuteShellCommand com = new ExecuteShellCommand();
        d = com.executeCommand("bash /home/pi/scripts/temp.py");
        d = d.replace("\n", "");
        try {
            temp = round(Double.parseDouble(d), 2);
        } catch (NumberFormatException e) {
            System.err.println("Error while parsing the temperature: " + e.getMessage());
            temp = 20;
        }
        return temp;
    }

    private void getTempOutside() {
        String d;
        ExecuteShellCommand com = new ExecuteShellCommand();
        d = com.executeCommand("bash /home/pi/scripts/tempOutside");
        d = d.replace("\n", "");
        try {
            tempOutside = round(Double.parseDouble(d), 2);
        } catch (NumberFormatException ignored) {
        }
    }

    public String getTemperature(Request request, Response res) {
        response = "";
        String location = request.queryParams("location");
        if (Objects.equals(location, "outside")) {
            response = tempOutside + "";
        } else if (Objects.equals(location, "inside")) {
            response = temp + "";
        }
        return response;
    }


    private class UpdateTask extends TimerTask {

        @Override
        public void run() {
            getTemp();
            getTempOutside();
            try {
                WebSocketHandler.updateAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
