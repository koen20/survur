package com.koenhabets.sunrise.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

public class TemperatureHandler implements HttpHandler {
    static String response = "500";
    static int hour = 25;
    static int minute = 65;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        response = getTemp();

        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    public static String getTemp(){
        Calendar calendar = Calendar.getInstance();
        int hourc = calendar.get(Calendar.HOUR_OF_DAY);
        int minutec = calendar.get(Calendar.MINUTE);
        int minuted = minutec - minute;
        if(hourc != hour ||minuted >= 10){
            getTime();
            ExecuteShellCommand com = new ExecuteShellCommand();
            response = com.executeCommand("bash /var/www/html/cgi-bin/temp.py");
        } else {
        }


        return response;
    }
    public static void getTime(){
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }
}
