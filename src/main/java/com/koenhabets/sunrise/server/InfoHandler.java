package com.koenhabets.sunrise.server;

import com.koenhabets.sunrise.server.ScholicaApi.calendarScholica;
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
            jo.put("inside-temp", TemperatureHandler.getTemp());
            jo.put("outside-temp", WeatherHandler.getTemp());
            jo.put("sleeping", ActionHandler.sleeping);
            jo.put("inside", ActionHandler.inside);
            jo.put("next-appointment", CalendarHandler.getResponse());
            jo.put("nextSubject", calendarScholica.nextSubject);
            jo.put("vrij", calendarScholica.count);
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
