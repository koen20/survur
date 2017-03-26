package com.koenhabets.survur.server;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import spark.Request;
import spark.Response;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class FishHandler {
    static int food = 150;

    public FishHandler() throws IOException {
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new UpdateTask(), 0, 30 * 60 * 1000);
        readFood();
    }

    public String fishFeed(Request request, Response response) throws IOException {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (food < 150 && hour < 20 && hour > 6) {
            try {
                feedFish();
            } catch (Exception e) {
                e.printStackTrace();
            }
            food = food + 80;
        }
        saveFood();
        return food + "";
    }

    private void saveFood() throws IOException {
        File file = new File("food.txt");
        Files.write(food + "", file, Charsets.UTF_8);
    }

    private void readFood() throws IOException {
        File file = new File("food.txt");
        String result = Files.toString(file, Charsets.UTF_8);
        food = Integer.parseInt(result);
    }

    private void feedFish() throws Exception {
        String url = "http://192.168.2.47";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("GET");
        //con.setRequestProperty("User-Agent", USER_AGENT);
        //con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
    }

    private class UpdateTask extends TimerTask {

        @Override
        public void run() {
            food = food - 4;
            try {
                saveFood();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
