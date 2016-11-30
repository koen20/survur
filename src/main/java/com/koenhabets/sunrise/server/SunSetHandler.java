package com.koenhabets.sunrise.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class SunSetHandler implements HttpHandler {
    private final String USER_AGENT = "Mozilla/5.0";
    String sunset;
    String sunrise;

    @Override
    public void handle(HttpExchange he) throws IOException {
        String response = null;
        try {
            response = sendPost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonMain = jsonObject.getJSONObject("results");
            sunset = jsonMain.getString("sunset");
            sunrise = jsonMain.getString("sunrise");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String[] parts = sunset.split(":");
        String part1 = parts[0];
        String part2 = parts[1];
        int sunset1 = Integer.parseInt(part1) + 13;
        int sunset2 = Integer.parseInt(part2);
        response = sunset1 + "." + sunset2;

        String[] Spart = sunrise.split(":");
        String Spart1 = Spart[0];
        String Spart2 = Spart[1];
        int sunrise1 = Integer.parseInt(Spart1) + 1;
        int sunrise2 = Integer.parseInt(Spart2);
        response += ";" + sunrise1 + "." + sunrise2;
        response += ";200";

        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String sendPost() throws Exception {

        String url = "http://api.sunrise-sunset.org/json?lat=50.8927646&lng=6.02240852&date=today";
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
}