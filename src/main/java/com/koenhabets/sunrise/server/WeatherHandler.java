package com.koenhabets.sunrise.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by koenh on 8/11/2016.
 */
public class WeatherHandler implements HttpHandler {
    String data;
    String response;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        int temp = 400;
        System.out.println("Weather request received");
        try {
            data = sendWeatherPost();
        } catch (Exception e) {
            e.printStackTrace();
            response = "500";
            httpExchange.sendResponseHeaders(500, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(data);
            JSONObject jsonMain = jsonObject.getJSONObject("main");
            temp = jsonMain.getInt("temp");
        } catch (JSONException e) {
            e.printStackTrace();
            response = "500";
            httpExchange.sendResponseHeaders(500, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        response = temp + "";
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    public static String sendWeatherPost() throws Exception {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=Landgraaf,nl&appid=4f2475b85b4857b421da5d7cd87931d4&units=metric";
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
    public static int getTemp(){
        int temp = 400;
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

        return temp;
    }
}
