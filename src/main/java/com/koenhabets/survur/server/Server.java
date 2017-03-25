package com.koenhabets.survur.server;

import com.koenhabets.survur.server.ScholicaApi.calendarScholica;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Key;

public class Server {

    public static void main(String args[]) throws IOException {
        if (args.length > 0 && args[0].equals("log")) Log.enableLog(true);
        KeyHolder.init("config.json");
        int port = 9999;
        System.out.println("Starting server on port: " + port);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/sunset-sunrise", new SunSetHandler());
        server.createContext("/wol", new WakeOnLanHandler());
        server.createContext("/temp", new TemperatureHandler());
        server.createContext("/voice", new VoiceHandler());
        server.createContext("/action", new ActionHandler());
        server.createContext("/calendar", new CalendarHandler());
        server.createContext("/response", new ResponseHandler());
        server.createContext("/lcd", new LcdHandler());
        server.createContext("/info", new InfoHandler());
        server.createContext("/room", new RoomHandler());
        server.createContext("/lights", new lightsHandler());
        server.createContext("/config", new ConfigHandler());
        server.createContext("/fish", new FishHandler());
        server.start();
        timer.main();
        calendarScholica.main();

        lightsHandler.resetLights();
    }
}
