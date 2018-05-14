package com.koenhabets.survur.server;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.sql.*;
import java.util.Calendar;

public class PowerData {
    static Connection conn;

    public PowerData() {
        try {
            conn = DriverManager.getConnection("jdbc:mariadb://192.168.2.24:3306/sensors?autoReconnect=true",
                    KeyHolder.getMysqlUsername(), KeyHolder.getMysqlPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addData(double energyUsage1, double energyUsage2, double energyProduction1, double energyProduction2, double gasUsage) {
        Calendar cal = Calendar.getInstance();
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT sensors values (" + getMysqlDateString(cal.getTimeInMillis()) + ", '" + energyUsage1 + "', '" + energyUsage2 + "'" +
                    ", '" + energyProduction1 + "', '" + energyProduction2 + "',  '" + gasUsage + "')");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray getMonthlyTotal(int months) {
        JSONArray ja = readData();
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
        }

        return jsonArray;
    }

    public static JSONArray getDataTime(long startTime, long endTime) {
        JSONArray jsonArray = new JSONArray();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM sensors WHERE date BETWEEN CONVERT(datetime, '" + getMysqlDateString(startTime) +"') AND " +
                    "CONVERT(datetime, '" + getMysqlDateString(endTime) +"'");
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

        } catch (SQLException e) {
            e.printStackTrace();
        }



        return jsonArray;
    }

    private static JSONArray readData() {
        String result = "[]";
        try {
            File file1 = new File(ConfigHandler.directory + "powerData.json");
            result = Files.asCharSource(file1, Charsets.UTF_8).read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray ja = new JSONArray(result);

        return ja;
    }

    private static void writeData(JSONArray jsonArray) {
        try {
            File fileS = new File(ConfigHandler.directory + "powerData.json");
            Files.asCharSink(fileS, Charsets.UTF_8).write(jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getMysqlDateString(long milliseconds) {
        java.util.Date dt = new java.util.Date(milliseconds);

        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String currentTime = sdf.format(dt);
        return currentTime;
    }
}
