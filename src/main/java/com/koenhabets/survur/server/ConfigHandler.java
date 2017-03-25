package com.koenhabets.survur.server;

import spark.Request;
import spark.Response;

import java.util.Objects;


public class ConfigHandler {
    static boolean alarmEnabled = true;
    static boolean motionEnabled = true;

    public String setConfig(Request request, Response response) {
        String parm = request.queryParams("config");
        String parm2 = request.queryParams("status");
        if (Objects.equals(parm, "alarm")) {
            if (Objects.equals(parm2, "true")) {
                alarmEnabled = true;
            } else {
                alarmEnabled = false;
            }
        } else if (Objects.equals(parm, "motion")) {
            if (Objects.equals(parm2, "true")) {
                motionEnabled = true;
            } else {
                motionEnabled = false;
            }
        }
        return "";
    }
}
