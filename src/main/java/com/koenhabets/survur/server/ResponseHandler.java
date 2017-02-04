package com.koenhabets.survur.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class ResponseHandler implements HttpHandler {
    int code = 200;
    String response = "Sent";

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        String[] parts = parm.split("=");
        String[] parts2 = parts[1].split(";");

        if (Objects.equals(parts2[1], "Prep-Sleep")) {
            if (Objects.equals(parts2[0], "ja")) {
            }
        }

        httpExchange.sendResponseHeaders(code, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
