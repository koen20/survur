package com.koenhabets.sunrise.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    public static void main(String args[]) throws IOException {
        int port = 9999;
        System.out.println("Starting server on port: " + port);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/sunset-sunrise", new SunSetHandler());
        server.createContext("/wol", new WakeOnLanHandler());
        server.createContext("/weather", new WeatherHandler());
        server.createContext("/temp", new TemperatureHandler());
        server.createContext("/voice", new VoiceHandler());
        server.createContext("/action", new ActionHandler());
        server.start();
        System.out.println("Server started.");
    }
}
