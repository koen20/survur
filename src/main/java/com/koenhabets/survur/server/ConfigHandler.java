package com.koenhabets.survur.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;


public class ConfigHandler implements HttpHandler {
    static boolean alarmEnabled = true;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parmt = httpExchange.getRequestURI().getQuery();
        String[] parm = parmt.split("=");

        if (Objects.equals(parm[0], "alarm")) {
            if (Objects.equals(parm[1], "true")) {
                alarmEnabled = true;
            } else {
                alarmEnabled = false;
            }
        }

        String response = ":)";
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
