package com.koenhabets.survur.server;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Overwatch {
    static int comprank;
    static int win_rate;
    public Overwatch() {
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new update(), millisToNextDay(Calendar.getInstance()), 86400000);//1 day
    }

    public class update extends TimerTask {
        @Override
        public void run() {
            updateStats("koen-21591");
            insertDb(comprank, win_rate, "koen-21591");
        }
    }

    private void updateStats(String player){
        try {
            String url = "https://owapi.net/api/v3/u/" + player + "/stats";

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject jsonObject = new JSONObject(response.toString());
            JSONObject overallstats = jsonObject.getJSONObject("eu").getJSONObject("stats").getJSONObject("competitive").getJSONObject("overall_stats");
            comprank = overallstats.getInt("comprank");
            win_rate = overallstats.getInt("win_rate");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertDb(int sr, int win_rate, String player){
        try {
            Calendar cal = Calendar.getInstance();
            Connection conn = DriverManager.getConnection("jdbc:mariadb://192.168.2.24:3306/overwatch",
                    KeyHolder.getMysqlUsername(), KeyHolder.getMysqlPassword());
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO stats VALUES ('" + PowerData.getMysqlDateString(cal.getTimeInMillis()) +"', '" + sr + "', '" + win_rate + "', '" + player + "')");
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static long millisToNextDay(Calendar calendar) {
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        int millis = calendar.get(Calendar.MILLISECOND);
        int hoursToNextDay = 24 - hours;
        int minutesToNextHour = 60 - minutes;
        int secondsToNextHour = 60 - seconds;
        int millisToNextHour = 1000 - millis;
        return hoursToNextDay * 60 * 60 * 1000 + minutesToNextHour * 60 * 1000 + secondsToNextHour * 1000 + millisToNextHour;
    }
}
