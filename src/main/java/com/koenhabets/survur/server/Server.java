package com.koenhabets.survur.server;

import spark.Spark;

import java.io.IOException;

import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;
import static spark.Spark.webSocket;

public class Server {

    public static void main(String args[]) throws IOException {
        if (args.length > 0 && args[0].equals("log")) Log.enableLog(true);
        KeyHolder.init(ConfigHandler.directory + "configServer.json");

        Spark.port(9999);

        InfoHandler info = new InfoHandler();
        //LightsHandler light = new LightsHandler();
        WakeOnLanHandler wol = new WakeOnLanHandler();
        SunSetHandler sunsetSunrise = new SunSetHandler();
        ConfigHandler config = new ConfigHandler();
        TemperatureHandler temp = new TemperatureHandler();
        //SleepHandler action = new SleepHandler();
        //new RoomHandler();
        //CalendarHandler calendar = new CalendarHandler();
        PowerHandler power = new PowerHandler();
        //Assistant assistant = new Assistant();
        new LcdHandler();
        //new calendarZermelo();
        //new Sonarr();
        //new Mqtt();
        new PowerData();
        new Overwatch();
        new Wot();

        webSocket("/ws", WebSocketHandler.class);
        //get("/info", info::getInfo);
        //post("/lights", light::setLight);
        //post("/setledstrip", light::setLed);
        post("/wol", wol::wol);
        get("/wol", wol::wol);
        get("/sunset-sunrise", sunsetSunrise::getSunsetSunrise);
        post("/config", config::setConfig);
        get("/temp", temp::getTemperature);
        //post("/action", action::action);
        //get("/action", action::action);
        //post("/calendar", calendar::setCalendar);
        path("/wol", () -> {
            post("/wake", wol::wol);
            get("/wake", wol::wol);
        });
        post("/energy", power::setEnergy);
        get("/energy", power::getData);
        //post("/assistant", assistant::action);

        LightsHandler.resetLights();
    }
}

