package com.koenhabets.survur.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PowerData {
    static Connection conn;
    static JSONArray jsonArrayRecent = new JSONArray();//240

    public PowerData() {
        try {
            conn = DriverManager.getConnection("jdbc:mariadb://192.168.2.24:3306/sensors",
                    KeyHolder.getMysqlUsername(), KeyHolder.getMysqlPassword());
            Timer updateTimer = new Timer();
            updateTimer.scheduleAtFixedRate(new checkMysqlConnection(), 2000, 10000);
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new updateRecentPower(), 10000, 5000);
            Timer timer2 = new Timer();
            timer2.scheduleAtFixedRate(new updateNextDay(), Overwatch.millisToNextDay(Calendar.getInstance()) + 2400000, 86400000);
            //getMonthlyTotal();
            //getDailyTotal();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getMonthlyTotal() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2018);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        JSONArray jsonArray = getDataTime(1514796926, 1759910192);

        double energyUsage1 = 0;
        double energyUsage2 = 0;
        double energyProduction1 = 0;
        double energyProduction2 = 0;
        double gasUsage = 0;
        double benergyUsage1 = 0;
        double benergyUsage2 = 0;
        double benergyProduction1 = 0;
        double benergyProduction2 = 0;
        double bgasUsage = 0;
        long time2 = 0;
        for (int d = 0; d < 40; d++) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                Date t = getLongDateString(item.getString("timeString"));
                long time = t.getTime();

                if (time > cal.getTimeInMillis() && time < cal.getTimeInMillis() + 1800000) {
                    energyUsage1 = item.getDouble("energyUsage1");
                    energyUsage2 = item.getDouble("energyUsage2");
                    energyProduction1 = item.getDouble("energyProduction1");
                    energyProduction2 = item.getDouble("energyProduction2");
                    gasUsage = item.getDouble("gasUsage");
                }
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
                if (time > cal.getTimeInMillis() && time < cal.getTimeInMillis() + 1800000) {
                    time2 = time;
                    benergyUsage1 = item.getDouble("energyUsage1");
                    benergyUsage2 = item.getDouble("energyUsage2");
                    benergyProduction1 = item.getDouble("energyProduction1");
                    benergyProduction2 = item.getDouble("energyProduction2");
                    bgasUsage = item.getDouble("gasUsage");
                }
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
            }
            energyUsage1 = TemperatureHandler.round(benergyUsage1 - energyUsage1, 3);
            energyUsage2 = TemperatureHandler.round(benergyUsage2 - energyUsage2, 3);
            energyProduction1 = TemperatureHandler.round(benergyProduction1 - energyProduction1, 3);
            energyProduction2 = TemperatureHandler.round(benergyProduction2 - energyProduction2, 3);
            gasUsage = TemperatureHandler.round(bgasUsage - gasUsage, 3);
            if (time2 != 0) {
                try {
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate("INSERT INTO powerMonthly VALUES (DEFAULT, '" + getMysqlDateString(time2) + "', '" + energyUsage1 + "', '" + energyUsage2 + "'" +
                            ", '" + energyProduction1 + "', '" + energyProduction2 + "',  '" + gasUsage + "')");
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        }
    }

    public static void getDailyTotal() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);


        cal.add(Calendar.DAY_OF_MONTH, -1);

        JSONArray jsonArray = getDataTime(1570521392, 1759910192);

        double energyUsage1 = 0;
        double energyUsage2 = 0;
        double energyProduction1 = 0;
        double energyProduction2 = 0;
        double gasUsage = 0;
        double benergyUsage1 = 0;
        double benergyUsage2 = 0;
        double benergyProduction1 = 0;
        double benergyProduction2 = 0;
        double bgasUsage = 0;
        long time2 = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            Date t = getLongDateString(item.getString("timeString"));
            long time = t.getTime();

            if (time > cal.getTimeInMillis() && time < cal.getTimeInMillis() + 1800000) {
                energyUsage1 = item.getDouble("energyUsage1");
                energyUsage2 = item.getDouble("energyUsage2");
                energyProduction1 = item.getDouble("energyProduction1");
                energyProduction2 = item.getDouble("energyProduction2");
                gasUsage = item.getDouble("gasUsage");
            }
            if (time > cal.getTimeInMillis() + 86400000 && time < cal.getTimeInMillis() + 86400000 + 1800000) {
                time2 = time;
                benergyUsage1 = item.getDouble("energyUsage1");
                benergyUsage2 = item.getDouble("energyUsage2");
                benergyProduction1 = item.getDouble("energyProduction1");
                benergyProduction2 = item.getDouble("energyProduction2");
                bgasUsage = item.getDouble("gasUsage");
            }
        }
        energyUsage1 = TemperatureHandler.round(benergyUsage1 - energyUsage1, 3);
        energyUsage2 = TemperatureHandler.round(benergyUsage2 - energyUsage2, 3);
        energyProduction1 = TemperatureHandler.round(benergyProduction1 - energyProduction1, 3);
        energyProduction2 = TemperatureHandler.round(benergyProduction2 - energyProduction2, 3);
        gasUsage = TemperatureHandler.round(bgasUsage - gasUsage, 3);
        if (time2 != 0) {
            time2 = time2 - 2700000;
            try {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("INSERT INTO powerDaily VALUES (DEFAULT, '" + getMysqlDateString(time2) + "', '" + energyUsage1 + "', '" + energyUsage2 + "'" +
                        ", '" + energyProduction1 + "', '" + energyProduction2 + "',  '" + gasUsage + "')");
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
            if (jsonArrayRecent.length() >= 240) {
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

    private class updateNextDay extends TimerTask {
        @Override
        public void run() {
            getDailyTotal();
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
                jsonObject.put("timeString", rs.getTimestamp(1) + "");
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

    static Date getLongDateString(String date) {
        Date d = new Date();
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            d = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }
}
