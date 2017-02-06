package com.koenhabets.survur.server;

import com.koenhabets.survur.server.ScholicaApi.calendarScholica;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;


public class InfoHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        JSONObject jo = new JSONObject();
        calendarScholica.update();
        try {
            jo.put("inside-temp", TemperatureHandler.temp);
            jo.put("outside-temp", TemperatureHandler.tempOutside);
            jo.put("sleeping", ActionHandler.sleeping);
            jo.put("inside", ActionHandler.inside);
            jo.put("next-appointment", CalendarHandler.getResponse());
            jo.put("nextSubject", calendarScholica.nextSubject);
            jo.put("vrij", calendarScholica.count);
            jo.put("light-A", lightsHandler.A);
            jo.put("light-B", lightsHandler.B);
            jo.put("light-C", lightsHandler.C);
            jo.put("livingRoomTemp", TemperatureHandler.livingRoomTemp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String response = jo.toString();
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
