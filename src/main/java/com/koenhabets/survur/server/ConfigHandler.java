package com.koenhabets.survur.server;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class ConfigHandler {
    static boolean alarmEnabled = true;
    static boolean motionEnabled = true;

    public ConfigHandler() {
        try {
            readSavedConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String setConfig(Request request, Response response) throws IOException {
        String parm = request.queryParams("config");
        String parm2 = request.queryParams("status");
        if (Objects.equals(parm, "alarm")) {
            if (Objects.equals(parm2, "true")) {
                alarmEnabled = true;
            } else {
                alarmEnabled = false;
            }
            saveConfig();
        } else if (Objects.equals(parm, "motion")) {
            if (Objects.equals(parm2, "true")) {
                motionEnabled = true;
            } else {
                motionEnabled = false;
            }
            saveConfig();
        }
        return "";
    }

    private void readSavedConfig() throws IOException {
        File file = new File("config.txt");
        String result = Files.toString(file, Charsets.UTF_8);
        JSONObject jo = new JSONObject(result);
        alarmEnabled = jo.getBoolean("alarm");
        motionEnabled = jo.getBoolean("motion");
    }

    private void saveConfig() throws IOException {
        JSONObject jo = new JSONObject();
        jo.put("alarm", alarmEnabled);
        jo.put("motion", motionEnabled);
        File file = new File("config.txt");
        Files.write(jo.toString(), file, Charsets.UTF_8);
    }
}
