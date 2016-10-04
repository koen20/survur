package com.koenhabets.sunrise.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class ActionHandler implements HttpHandler {
    String response = "sent";
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        String[] parts = parm.split("=");
        String action = parts[1];
        if (Objects.equals(action, "Wake-up")){
            try {
                int temp = WeatherHandler.getTemp();
                VoiceHandler.sendPost("Goedemorgen Koen. Het is "+ temp + " graden buiten.", "voice");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
