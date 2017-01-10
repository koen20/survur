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
    static int hour = 25;
    static int minute = 65;
    static double temp = 500;
    String response;

    public static double getTemp() {
        Calendar calendar = Calendar.getInstance();
        int hourc = calendar.get(Calendar.HOUR_OF_DAY);
        int minutec = calendar.get(Calendar.MINUTE);
        int minuted = minutec - minute;
        if (hourc != hour || minuted >= 5) {
            getTime();
            String d;
            ExecuteShellCommand com = new ExecuteShellCommand();
            d = com.executeCommand("bash /home/pi/tempOutside");
            d = d.replace("\n", "");
            temp = Double.parseDouble(d);
        }
        return temp;
    }

    public static void getTime() {
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        response = getTemp() + "";
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
