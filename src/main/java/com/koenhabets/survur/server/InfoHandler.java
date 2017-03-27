package com.koenhabets.survur.server;

import com.koenhabets.survur.server.ScholicaApi.calendarScholica;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

public class InfoHandler {

    public String getInfo(Request request, Response response){
        JSONObject jo = new JSONObject();
        try {
            jo.put("inside-temp", TemperatureHandler.temp);
            jo.put("outside-temp", TemperatureHandler.tempOutside);
            jo.put("sleeping", ActionHandler.sleeping);
            jo.put("inside", ActionHandler.inside);
            jo.put("insideRoom", RoomHandler.insideRoom);
            jo.put("next-appointment", CalendarHandler.getResponse());
            jo.put("nextSubject", calendarScholica.nextSubject);
            jo.put("vrij", calendarScholica.count);
            jo.put("light-A", LightsHandler.A);
            jo.put("light-B", LightsHandler.B);
            jo.put("light-C", LightsHandler.C);
            jo.put("livingRoomTemp", TemperatureHandler.livingRoomTemp);
            jo.put("alarmEnabled", ConfigHandler.alarmEnabled);
            jo.put("motionEnabled", ConfigHandler.motionEnabled);
            jo.put("fishFood", FishHandler.food);
            jo.put("lastMovement", RoomHandler.lastMovement);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo.toString();
    }
}