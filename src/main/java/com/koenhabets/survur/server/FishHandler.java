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
            try {
                feedFish();
                miliseconds = cal.getTimeInMillis();
                lastFed = day + "-" + month + " " + hour + ":" + minute;
                WebSocketHandler.updateAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        saveFood();
        return lastFed;
    }

    private void saveFood() throws IOException {
        File file = new File("food.txt");
        Files.write(Long.toString(miliseconds) + ";" + lastFed, file, Charsets.UTF_8);
    }

    private void readFood() throws IOException {
        String parts[] = {};
        try {
            File file = new File("food.txt");
            parts = Files.toString(file, Charsets.UTF_8).split(";");
        } catch (FileNotFoundException ignored){

        }
        try {
            miliseconds = Long.parseLong(parts[0]);
            lastFed = parts[1];
        } catch (ArrayIndexOutOfBoundsException ignored){
        }
    }

    private void feedFish() throws Exception {
        String url = "http://192.168.2.47";
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
