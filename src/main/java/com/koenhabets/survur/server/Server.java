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
        ConfigHandler config = new ConfigHandler();
        TemperatureHandler temp = new TemperatureHandler();
        ActionHandler action = new ActionHandler();
        RoomHandler room = new RoomHandler();
        CalendarHandler calendar = new CalendarHandler();
        ResponseHandler response = new ResponseHandler();
        new LcdHandler();

        get("/info", info::getInfo);
        post("/lights", light::setLight);
        post("/wol", wol::wol);
        get("/wol", wol::wol);
        get("/sunset-sunrise", sunsetSunrise::getSunsetSunrise);
        post("/fish", fish::fishFeed);
        post("/config", config::setConfig);
        get("/temp", temp::getTemperature);
        post("/temp", temp::setTemperature);
        post("/action", action::action);
        get("/action", action::action);
        post("/room", room::action);
        post("/calendar", calendar::setCalendar);
        post("/response", response::response);

        calendarScholica.main();

        LightsHandler.resetLights();
    }
}
