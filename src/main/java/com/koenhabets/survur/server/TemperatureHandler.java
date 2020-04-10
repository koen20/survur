package com.koenhabets.survur.server;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class TemperatureHandler {
    static double tempInside = 20;
    static double livingRoomTemp = 20;
    static double tempOutside;
    String response;

    public TemperatureHandler() {
        Timer updateTimer = new Timer();
        Timer updateTimer2 = new Timer();
        getTempInside();
        getTempOutside();
        updateTimer.scheduleAtFixedRate(new UpdateTask(), 0, 4 * 60 * 1000);
        updateTimer2.scheduleAtFixedRate(new updateDb(), 17000, 900000);
    }

    private class updateDb extends TimerTask {

        @Override
        public void run() {
            getTempInside();
            getTempOutside();
            insertTempDb(tempInside, tempOutside);
        }
    }

    private void insertTempDb(double temp, double tempOutside) {
        try {
            Calendar cal = Calendar.getInstance();
            Statement stmt = PowerData.conn.createStatement();
            stmt.executeUpdate("INSERT INTO temperature VALUES (DEFAULT, '" + PowerData.getMysqlDateString(cal.getTimeInMillis()) + "', '" + temp + "', 'inside')");
            stmt.executeUpdate("INSERT INTO temperature VALUES (DEFAULT, '" + PowerData.getMysqlDateString(cal.getTimeInMillis()) + "', '" + tempOutside + "', 'outside')");
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

    private String getTemp(String sensor){
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://home.koenhabets.nl/api/states/" + sensor).newBuilder();

        String url = urlBuilder.build().toString();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhYzA5MjZiNGMxY2Q0M2EzYTRjN2Y4MzIxZmZmMmY0OCIsImlhdCI6MTU2MjYxMzkzNCwiZXhwIjoxODc3OTczOTM0fQ.DTCfyUwL1ULiaz1YIfCTllRMfGO6Kp0DRr5mC1LOEHQ")
                .header("Content-Type", "application/json")
                .build();
        okhttp3.Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String res = null;
        try {
            res = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private double getTempInside() {
        /*
        String d;
        ExecuteShellCommand com = new ExecuteShellCommand();
        d = com.executeCommand("bash /home/pi/scripts/temp.py");
        d = d.replace("\n", "");
        try {
            tempInside = round(Double.parseDouble(d), 1);
        } catch (NumberFormatException e) {
            System.err.println("Error while parsing the temperature: " + e.getMessage());
            tempInside = 20;
        }
        */
        try {
            JSONObject jsonObject = new JSONObject(getTemp("sensor.bme280_sensor_temperature"));
            tempInside = jsonObject.getDouble("state");
        } catch (Exception e){
            e.printStackTrace();
        }
        return tempInside;
    }

    private void getTempOutside() {
        try {
            JSONObject jsonObject = new JSONObject(getTemp("sensor.outside_temperature"));
            tempOutside = jsonObject.getDouble("state");
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", tempOutside);
            String url = "https://api.opensensemap.org/boxes/5c41fb5b1b7ca8001989ddc8/5c41fb5b1b7ca8001989ddc9";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            String urlParameters = "\n" + jsonObject.toString();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTemperature(Request request, Response res) {
        response = "";
        String location = request.queryParams("location");
        if (Objects.equals(location, "outside")) {
            response = tempOutside + "";
        } else if (Objects.equals(location, "inside")) {
            response = tempInside + "";
        }
        return response;
    }


    private class UpdateTask extends TimerTask {

        @Override
        public void run() {
            getTempInside();
            getTempOutside();
            try {
                WebSocketHandler.updateAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
