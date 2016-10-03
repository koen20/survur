package com.koenhabets.sunrise.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

public class TemperatureHandler implements HttpHandler {
    String response = "500";
    int hour = 25;
    int minute = 65;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Temp request received");
        Calendar calendar = Calendar.getInstance();
        int hourc = calendar.get(Calendar.HOUR_OF_DAY);
        int minutec = calendar.get(Calendar.MINUTE);
        if(hourc != hour || minutec != minute){
            getTime();
            response = getTemp();
        } else {
        }

        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    public static String getTemp(){
        System.out.println("Getting inside temp");
        ExecuteShellCommand com = new ExecuteShellCommand();
        String response = com.executeCommand("bash /var/www/html/cgi-bin/temp.py");
        return response;
    }
    public void getTime(){
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }
}
