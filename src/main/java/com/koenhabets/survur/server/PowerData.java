package com.koenhabets.survur.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PowerData {
    static Connection conn;
    static JSONArray jsonArrayRecent;//360

    public PowerData() {
        try {
            conn = DriverManager.getConnection("jdbc:mariadb://192.168.2.24:3306/sensors",
                    KeyHolder.getMysqlUsername(), KeyHolder.getMysqlPassword());
            Timer updateTimer = new Timer();
            updateTimer.scheduleAtFixedRate(new checkMysqlConnection(), 2000, 10000);
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new updateRecentPower(), 10000, 5000);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class updateRecentPower extends TimerTask {
        @Override
        public void run() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("date", new Date().getTime());
            jsonObject.put("energyUsage", PowerHandler.currentEnergyUsage);
            jsonObject.put("energyProduction", PowerHandler.currentEnergyProduction);
            jsonArrayRecent.put(jsonObject);
            if (jsonArrayRecent.length() >= 360){
                jsonArrayRecent.remove(0);
            }
        }
    }

    private class checkMysqlConnection extends TimerTask {
        @Override
        public void run() {
            try {
                if (!conn.isValid(2700)) {
                    conn.close();
                    conn = DriverManager.getConnection("jdbc:mariadb://192.168.2.24:3306/sensors",
                            KeyHolder.getMysqlUsername(), KeyHolder.getMysqlPassword());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addData(double energyUsage1, double energyUsage2, double energyProduction1, double energyProduction2, double gasUsage) {
        Calendar cal = Calendar.getInstance();
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO power VALUES ('" + getMysqlDateString(cal.getTimeInMillis()) + "', '" + energyUsage1 + "', '" + energyUsage2 + "'" +
                    ", '" + energyProduction1 + "', '" + energyProduction2 + "',  '" + gasUsage + "')");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray getMonthlyTotal(int months) {
        /*JSONArray ja = readData();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < ja.length(); i++) {
            for (int d = 0; d < months; ++d) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                JSONObject item = ja.getJSONObject(i);
                long time = item.getLong("time");
                if (time > cal.getTimeInMillis() && time < cal.getTimeInMillis() + 120000) {
                    jsonArray.put(item);
                }
            }
        }*/

        return new JSONArray();
    }

    public static JSONArray getDataTime(long startTime, long endTime) {
        JSONArray jsonArray = new JSONArray();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM power WHERE date BETWEEN '" + getMysqlDateString(startTime * 1000) + "' AND '"
                    + getMysqlDateString(endTime * 1000) + "'");
            while (rs.next()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("time", rs.getTimestamp(1));
                jsonObject.put("energyUsage1", rs.getDouble(2));
                jsonObject.put("energyUsage2", rs.getDouble(3));
                jsonObject.put("energyProduction1", rs.getDouble(4));
                jsonObject.put("energyProduction2", rs.getDouble(5));
                jsonObject.put("gasUsage", rs.getDouble(6));
                jsonArray.put(jsonObject);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return jsonArray;
    }

    static String getMysqlDateString(long milliseconds) {
        java.util.Date dt = new java.util.Date(milliseconds);

        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String currentTime = sdf.format(dt);
        return currentTime;
    }
}
