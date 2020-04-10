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


    public Overwatch() {
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new update(), millisToNextDay(Calendar.getInstance()), 86400000);//1 day
    }

    public class update extends TimerTask {
        @Override
        public void run() {
            OverwatchPlayerItem item = null;
            try {
                item = updateStats("koen-21591");
                insertDb(item);
            } catch (Exception e) {
                Timer updateTimer = new Timer();
                updateTimer.schedule(new update(), 1800000);//30 minutes
                e.printStackTrace();
            }
        }
    }

    private OverwatchPlayerItem updateStats(String player) throws IOException {
        OverwatchPlayerItem item = null;
        int comprank = 0;
        double win_rate = 0;
        int compGamesPlayed = 0;
        double quickTimePlayed = 0;
        double compTimePlayed = 0;
        int quickGamesWon = 0;
        int compGamesWon = 0;
        int comprankTank = 0;
        int comprankDps = 0;
        int comprankSupport = 0;
        int compGamesTied = 0;

        String url = "https://owapi.net/api/v3/u/" + player + "/blob";

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
        JSONObject stats = jsonObject.getJSONObject("eu").getJSONObject("stats");
        JSONObject compOverallstats = stats.getJSONObject("competitive").getJSONObject("overall_stats");
        JSONObject compGameStats = stats.getJSONObject("competitive").getJSONObject("game_stats");
        JSONObject quickGameStats = stats.getJSONObject("quickplay").getJSONObject("game_stats");
        try {
            if (!compOverallstats.isNull("tank_comprank")){
                comprankTank = compOverallstats.getInt("tank_comprank");
            }
            if (!compOverallstats.isNull("support_comprank")){
                comprankSupport = compOverallstats.getInt("support_comprank");
            }
            if (!compOverallstats.isNull("damage_comprank")){
                comprankDps = compOverallstats.getInt("damage_comprank");
            }
        } catch (Exception ignored){
        }
        try {
            win_rate = compOverallstats.getDouble("win_rate");
        } catch (Exception ignored) {
        }
        try {
            compGamesPlayed = compGameStats.getInt("games_played");
            compGamesWon = compGameStats.getInt("games_won");
            compTimePlayed = compGameStats.getDouble("time_played");

            quickGamesWon = quickGameStats.getInt("games_won");
            quickTimePlayed = quickGameStats.getDouble("time_played");

            compGamesTied = compGameStats.getInt("games_tied");
        } catch (Exception  e) {
            e.printStackTrace();
        }
        if (win_rate == 0){
            win_rate = compGamesWon / (compGamesPlayed - compGamesTied);
        }
        item = new OverwatchPlayerItem(player, comprankSupport, comprankTank, comprankDps, win_rate, compGamesPlayed, quickTimePlayed, compTimePlayed, quickGamesWon, compGamesWon);

        return item;
    }

    private void insertDb(OverwatchPlayerItem item) {
        try {
            Calendar cal = Calendar.getInstance();
            Connection conn = DriverManager.getConnection("jdbc:mariadb://192.168.2.24:3306/overwatch",
                    KeyHolder.getMysqlUsername(), KeyHolder.getMysqlPassword());
            Statement stmt = conn.createStatement();
            if (item.getDamageComprank() == 0){
                stmt.executeUpdate("INSERT INTO stats VALUES ('" + PowerData.getMysqlDateString(cal.getTimeInMillis()) + "', NULL" +
                        ", '" + item.getTankComprank() + "', NULL, '" + item.getSupportComprank() + "', '" + item.getCompWinrate()
                        + "', '" + item.getCompGamesPlayed() + "', '" + item.getQuickTimePlayed() + "', '" + item.getCompTimePlayed() +
                        "', '" + item.getQuickGamesWon() + "', '" + item.getCompGamesWon() + "', '" + item.getPlayer() + "')");
            } else {
                stmt.executeUpdate("INSERT INTO stats VALUES ('" + PowerData.getMysqlDateString(cal.getTimeInMillis()) + "', NULL" +
                        ", '" + item.getTankComprank() + "', '" + item.getDamageComprank() + "', '" + item.getSupportComprank() + "', '" + item.getCompWinrate()
                        + "', '" + item.getCompGamesPlayed() + "', '" + item.getQuickTimePlayed() + "', '" + item.getCompTimePlayed() +
                        "', '" + item.getQuickGamesWon() + "', '" + item.getCompGamesWon() + "', '" + item.getPlayer() + "')");
            }
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static long millisToNextDay(Calendar calendar) {
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        int millis = calendar.get(Calendar.MILLISECOND);
        int hoursToNextDay = 23 - hours;
        int minutesToNextHour = 60 - minutes;
        int secondsToNextHour = 60 - seconds;
        int millisToNextHour = 1000 - millis;
        return hoursToNextDay * 60 * 60 * 1000 + minutesToNextHour * 60 * 1000 + secondsToNextHour * 1000 + millisToNextHour;
    }
}
