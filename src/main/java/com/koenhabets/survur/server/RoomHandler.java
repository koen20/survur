package com.koenhabets.survur.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class RoomHandler implements HttpHandler {
    static boolean insideRoom = false;
    String response;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        try {
            if (Objects.equals(parm, "enter")) {
                insideRoom = true;
                VoiceHandler.sendPost("Hallo", "voice");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
