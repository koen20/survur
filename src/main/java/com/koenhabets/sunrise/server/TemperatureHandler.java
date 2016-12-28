package com.koenhabets.sunrise.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Objects;

public class TemperatureHandler implements HttpHandler {
    static double temp = 500;
    String response;
    private static int hour = 25;
    private static int minute = 65;
    static double tempAvarage;
    static double[] tempArray = new double[3];

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        if(Objects.equals(parm, "graph")){
            response = "[" + timer.tempData + "," + timer.tempTime + "," + timer.outsideTemp + "," + timer.tempDataPrecise +"]";
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
        if (hourc != hour || minuted >= 5) {
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
        }
        tempArray[2] = tempArray[1];
        tempArray[1] = tempArray[0];
        tempArray[0] = getTemp();
        tempAvarage = (tempArray[0] + tempArray[1] + tempArray[2]) / 3;
        return tempAvarage;
    }
}
