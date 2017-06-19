package com.koenhabets.survur.server;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import spark.Request;
import spark.Response;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class FishHandler {
    private long miliseconds;
    static String lastFed = "-";
    static int daysLeft;

    public FishHandler() throws IOException {
        readFood();
    }

    public String fishFeed(Request request, Response response) throws IOException {
        Calendar cal = Calendar.getInstance();
        long Cmiliseconds = cal.getTimeInMillis();
        long milisecondsDif = Cmiliseconds - miliseconds;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        if (milisecondsDif > 3600000 * ConfigHandler.feedInterval && hour < 20 && hour > 6) {
            if (daysLeft > 0) {
                try {
                    if(daysLeft == 4){
                        feedFish(110);
                    } else if (daysLeft == 3){
                        feedFish(85);
                    } else if (daysLeft == 2){
                        feedFish(20);
                    } else if (daysLeft == 1) {
                        feedFish(0);
                    }

                    miliseconds = cal.getTimeInMillis();
                    lastFed = day + "-" + month + " " + hour + ":" + minute;
                    daysLeft = daysLeft - 1;
                    WebSocketHandler.updateAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    feedFish(110);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        saveFood();
        return lastFed;
    }

    public String refillFood(Request request, Response response) {
        daysLeft = 4;
        return ":)";
    }

    private void saveFood() throws IOException {
        File file = new File("food.txt");
        Files.write(Long.toString(miliseconds) + ";" + lastFed + ";" + daysLeft, file, Charsets.UTF_8);
    }

    private void readFood() throws IOException {
        String parts[] = {};
        try {
            File file = new File("food.txt");
            parts = Files.toString(file, Charsets.UTF_8).split(";");
        } catch (FileNotFoundException ignored) {

        }
        try {
            miliseconds = Long.parseLong(parts[0]);
            lastFed = parts[1];
            daysLeft = Integer.parseInt(parts[2]);
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    private void feedFish(int position) throws Exception {
        String url = "http://192.168.2.47?position=" + position;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        String urlParameters = "";

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
}
