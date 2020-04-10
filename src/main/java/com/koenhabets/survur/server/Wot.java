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
import java.util.*;

import static com.koenhabets.survur.server.Overwatch.millisToNextDay;
import static com.koenhabets.survur.server.TemperatureHandler.round;

public class Wot {
    //

    public Wot() {
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new update(), millisToNextDay(Calendar.getInstance()) + 3600000, 86400000);//1 day
    }

    public class update extends TimerTask {
        @Override
        public void run() {

            try {
                updatePlayerStats("503704612");
                updatePlayerStats("503703575");
            } catch (Exception e) {
                Timer updateTimer = new Timer();
                updateTimer.schedule(new update(), 1800000);//30 minutes
                e.printStackTrace();
            }
        }
    }

    private void updatePlayerStats(String player) throws IOException {
        WotPlayerItem item = null;
        item = updateStats(player);
        insertDb(item);
    }

    private WotPlayerItem updateStats(String player) throws IOException {
        WotPlayerItem item = null;
        String nickname;
        int globalRating;
        int battles;
        int losses;
        int wins;

        String url = "https://api.worldoftanks.eu/wot/account/info/?application_id=7ffbba9a2a2822a5eeb4f3a45b9eb546&account_id=" + player;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONObject jsonObject = new JSONObject(response.toString()).getJSONObject("data").getJSONObject(player);
        JSONObject statistics = jsonObject.getJSONObject("statistics").getJSONObject("all");

        nickname = jsonObject.getString("nickname");
        globalRating = jsonObject.getInt("global_rating");
        battles = statistics.getInt("battles");
        losses = statistics.getInt("losses");
        wins = statistics.getInt("wins");


        item = new WotPlayerItem(nickname, globalRating, battles, losses, wins);

        return item;
    }

    private void insertDb(WotPlayerItem item) {
        try {
            Calendar cal = Calendar.getInstance();
            Connection conn = DriverManager.getConnection("jdbc:mariadb://192.168.2.24:3306/overwatch",
                    KeyHolder.getMysqlUsername(), KeyHolder.getMysqlPassword());
            Statement stmt = conn.createStatement();

            double winrate = round((Double.parseDouble(item.getWins() + "") / Double.parseDouble(item.getBattles() + "")) * 100, 2);

            stmt.executeUpdate("INSERT INTO wot VALUES (DEFAULT, '" + PowerData.getMysqlDateString(cal.getTimeInMillis()) +
                    "', '" + item.getGlobalRating() + "', '" + item.getBattles() + "', '" + item.getLosses() + "', '" + item.getWins()
                    + "', '" + winrate + "', '" + item.getPlayer() + "')");

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
