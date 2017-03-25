package com.koenhabets.survur.server;

import com.koenhabets.survur.server.ScholicaApi.calendarScholica;
import spark.Spark;

import java.io.IOException;

import static spark.Spark.get;
import static spark.Spark.post;

public class Server {

    public static void main(String args[]) throws IOException {
        if (args.length > 0 && args[0].equals("log")) Log.enableLog(true);
        KeyHolder.init("config.json");

        Spark.port(9999);

        InfoHandler info = new InfoHandler();
        LightsHandler light = new LightsHandler();
        WakeOnLanHandler wol = new WakeOnLanHandler();
        SunSetHandler sunsetSunrise = new SunSetHandler();
        FishHandler fish = new FishHandler();

        get("/info", info::getInfo);
        post("/lights", light::setLight);
        post("/wol", wol::wol);
        get("/wol", wol::wol);
        get("/sunsest-sunrise", sunsetSunrise::getSunsetSunrise);
        post("/fish", fish::fishFeed);

        /*
        System.out.println("Starting server on port: " + port);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/temp", new TemperatureHandler());
        server.createContext("/voice", new VoiceHandler());
        server.createContext("/action", new ActionHandler());
        server.createContext("/calendar", new CalendarHandler());
        server.createContext("/response", new ResponseHandler());
        server.createContext("/lcd", new LcdHandler());
        server.createContext("/room", new RoomHandler());
        server.createContext("/config", new ConfigHandler());
        server.start();
        */
        timer.main();
        calendarScholica.main();

        LightsHandler.resetLights();
    }
}
