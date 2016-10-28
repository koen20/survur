package com.koenhabets.sunrise.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class LcdHandler implements HttpHandler {
    int code = 200;
    String response = "Sent";
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        String[] parts = parm.split(";");
        printLcd(parts[0], parts[1]);

        httpExchange.sendResponseHeaders(code, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    public static void printLcd(String text, String text2){
        ExecuteShellCommand com = new ExecuteShellCommand();
        String response = com.executeCommand("python /home/pi/lcd2/text.py \"" + text + "\"" + "\"" + text2 + "\"");
    }
    public static void disableBacklight(){
        ExecuteShellCommand com = new ExecuteShellCommand();
        String response = com.executeCommand("python /home/pi/lcd2/disablelight.py");
    }
}
