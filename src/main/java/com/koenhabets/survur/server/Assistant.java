package com.koenhabets.survur.server;

import org.json.JSONObject;
import spark.Request;
import spark.Response;

public class Assistant {
    static String tada;

    public String action(Request req, Response res){
        tada = "Turning on light";
        JSONObject jsonObject = new JSONObject(req.body());
        JSONObject jsonObjectResult = jsonObject.getJSONObject("result");
        if(jsonObjectResult.getString("action").equals("turn_on_light")){
            JSONObject jsonObjectParam = jsonObjectResult.getJSONObject("parameters");
            String light = jsonObjectParam.getString("light");
            if(light.equals("1")){
                light = "A";
            }
            if(light.equals("2")){
                light = "B";
            }
            if(light.equals("3")){
                light = "C";
            }
            LightsHandler.Light(light + "on");
            tada = "Turning on light " + light;
        }
        if(jsonObjectResult.getString("action").equals("turn_off_light")){
            JSONObject jsonObjectParam = jsonObjectResult.getJSONObject("parameters");
            String light = jsonObjectParam.getString("light");
            if(light.equals("1")){
                light = "A";
            }
            if(light.equals("2")){
                light = "B";
            }
            if(light.equals("3")){
                light = "C";
            }
            LightsHandler.Light(light + "off");
            tada = "Turning off light " + light;
        }
        if(jsonObjectResult.getString("action").equals("turn_on_all_lights")){
            LightsHandler.Light("Aon");
            LightsHandler.Light("Bon");
        }
        if(jsonObjectResult.getString("action").equals("turn_off_all_lights")){
            LightsHandler.Light("Aoff");
            LightsHandler.Light("Boff");
            tada = "Turning off the lights";
        }

        JSONObject jo = new JSONObject();
        jo.put("speech", tada);
        jo.put("displayText", tada);
        jo.put("source", "Survur");
        res.header("Content-Type", "application/json");

        return jo.toString();
    }
}