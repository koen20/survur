package com.koenhabets.survur.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class TemperatureHandler implements HttpHandler {
    static double temp = 500;
    static double tempAvarage;
    static double[] tempArray = new double[5];
    static double livingRoomTemp;
    private final Timer updateTimer = new Timer();
    String response;

    public TemperatureHandler() {
        updateTimer.scheduleAtFixedRate(new UpdateTask(), 0,180 *1000);
    }

    public static double getTemp() {
        String d;
        ExecuteShellCommand com = new ExecuteShellCommand();
        d = com.executeCommand("bash /var/www/html/cgi-bin/temp.py");
        d = d.replace("\n", "");
        try {
            temp = Double.parseDouble(d);
        } catch (NumberFormatException e) {
            System.err.println("Error while parsing the temperature: " + e.getMessage());
            temp = -100;
        }
        avarageTemp();
        return temp;
    }

    public static double avarageTemp() {

        if (tempArray[1] == 0) {
            tempArray[0] = temp;
            tempArray[1] = temp;
            tempArray[2] = temp;
            tempArray[3] = temp;
            tempArray[4] = temp;
        }
        tempArray[4] = tempArray[3];
        tempArray[3] = tempArray[2];
        tempArray[2] = tempArray[1];
        tempArray[1] = tempArray[0];
        tempArray[0] = temp;
        tempAvarage = (tempArray[0] + tempArray[1] + tempArray[2] + tempArray[3] + tempArray[4]) / 5;
        return tempAvarage;
    }

    public static double getLivingRoomTemp() throws IOException {
        String url = "http://192.168.2.47";
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
        String text = response.toString();
        livingRoomTemp = Double.parseDouble(text);
        return livingRoomTemp;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        //String[] parts = parm.split("=");
        if (Objects.equals(parm, "graph")) {
            response = "[" + timer.tempData + "," + timer.tempTime + "," + timer.outsideTemp + "," + timer.tempDataPrecise + "," + timer.tempDataLivingRoom + "]";
            //} else if (Objects.equals(parts[0], "temp1")) {
            //livingRoomTemp = Double.parseDouble(parts[1]);
            //response = "Sent";
        } else {
            temp = getTemp();
            response = temp + "";
        }


        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private class UpdateTask extends TimerTask {

        @Override
        public void run() {
            getTemp();
        }
    }
}
