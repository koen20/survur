package com.koenhabets.sunrise.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONArray;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;
import java.util.Objects;

public class TemperatureHandler implements HttpHandler {
    static double temp = 500;
    String response;
    private static int hour = 25;
    private static int minute = 65;
    static double tempAvarage;
    static double[] tempArray = new double[5];
    static double livingRoomTemp;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        if(Objects.equals(parm, "graph")){
            response = "[" + timer.tempData + "," + timer.tempTime + "," + timer.outsideTemp + "," + timer.tempDataPrecise + "," + timer.tempDataLivingRoom + "]";
        }else{
            temp = getTemp();
            response = temp + "";
        }


        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static double getTemp() {
        Calendar calendar = Calendar.getInstance();
        int hourc = calendar.get(Calendar.HOUR_OF_DAY);
        int minutec = calendar.get(Calendar.MINUTE);
        int minuted = minutec - minute;
        if (hourc != hour || minuted >= 3) {
            getTime();
            String d;
            ExecuteShellCommand com = new ExecuteShellCommand();
            d = com.executeCommand("bash /var/www/html/cgi-bin/temp.py");
            d = d.replace("\n", "");
            temp = Double.parseDouble(d);
            avarageTemp();
        }

        return temp;
    }

    private static void getTime() {
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }
    public static double avarageTemp(){

        if(tempArray[1] == 0){
            tempArray[0] = getTemp();
            tempArray[1] = getTemp();
            tempArray[2] = getTemp();
            tempArray[3] = getTemp();
            tempArray[4] = getTemp();
        }
        tempArray[4] = tempArray[3];
        tempArray[3] = tempArray[2];
        tempArray[2] = tempArray[1];
        tempArray[1] = tempArray[0];
        tempArray[0] = getTemp();
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
}
