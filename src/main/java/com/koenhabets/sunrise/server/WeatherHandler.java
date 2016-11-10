package com.koenhabets.sunrise.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class WeatherHandler implements HttpHandler {
    String response;
    static int hour = 25;
    static int minute = 65;
    static int temp = 500;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        response = getTemp() + "";
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static String sendWeatherPost() throws Exception {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=Landgraaf,nl&appid=" + KeyHolder.getWeatherKey() + "&units=metric";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
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

        return response.toString();
    }

    public static int getTemp() {
        Calendar calendar = Calendar.getInstance();
        int hourc = calendar.get(Calendar.HOUR_OF_DAY);
        int minutec = calendar.get(Calendar.MINUTE);
        int minuted = minutec - minute;
        if (hourc != hour || minuted >= 20) {
            getTime();
            try {
                try {
                    JSONObject jsonObject = null;
                    String d = null;
                    try {
                        d = sendWeatherPost();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    jsonObject = new JSONObject(d);
                    JSONObject jsonMain = null;
                    jsonMain = jsonObject.getJSONObject("main");
                    temp = jsonMain.getInt("temp");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }
        return temp;
    }

    public static void getTime() {
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }
}
