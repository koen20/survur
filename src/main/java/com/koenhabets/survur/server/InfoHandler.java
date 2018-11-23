package com.koenhabets.survur.server;

import com.koenhabets.survur.server.ZermeloApi.calendarZermelo;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

public class InfoHandler {

    public String getInfo(Request request, Response response) {
        JSONObject jo = getJsonInfo();
        return jo.toString();
    }

    static JSONObject getJsonInfo() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("inside-temp", TemperatureHandler.tempInside);
            jo.put("outside-temp", TemperatureHandler.tempOutside);
            jo.put("sleeping", SleepHandler.sleeping);
            jo.put("inside", SleepHandler.inside);
            jo.put("insideRoom", RoomHandler.insideRoom);
            jo.put("next-appointment", CalendarHandler.getResponse());
            jo.put("nextSubject", calendarZermelo.nextSubject);
            jo.put("vrij", calendarZermelo.count);
            jo.put("light-A", LightsHandler.A);
            jo.put("light-B", LightsHandler.B);
            jo.put("light-C", LightsHandler.C);
            jo.put("livingRoomTemp", TemperatureHandler.livingRoomTemp);
            jo.put("alarmEnabled", ConfigHandler.alarmEnabled);
            jo.put("motionEnabled", ConfigHandler.motionEnabled);
            jo.put("lastMovement", RoomHandler.lastMovement);
            jo.put("pcOn", WakeOnLanHandler.pcOn);
            jo.put("feedInterval", ConfigHandler.feedInterval);
            jo.put("currentEnergyUsage", PowerHandler.currentEnergyUsage);
            jo.put("currentEnergyProduction", PowerHandler.currentEnergyProduction);
            jo.put("gasUsage", PowerHandler.gasUsage);
            jo.put("energyUsage1", PowerHandler.energyUsage1);
            jo.put("energyUsage2", PowerHandler.energyUsage2);
            jo.put("energyProduction1", PowerHandler.energyProduction1);
            jo.put("energyProduction2", PowerHandler.energyProduction2);
            jo.put("location", Mqtt.location);
            jo.put("ledStrip", LightsHandler.ledStrip);
            jo.put("espLed", LightsHandler.espLed);
            jo.put("ledRed", LightsHandler.ledRed);
            jo.put("ledGreen", LightsHandler.ledGreen);
            jo.put("ledBlue", LightsHandler.ledBlue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }
}