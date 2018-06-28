package com.koenhabets.survur.server;

import spark.Request;
import spark.Response;

import java.util.*;

public class SleepHandler {
    static boolean sleeping = false;
    static boolean inside = true;

    public SleepHandler() {

    }

    public String action(Request request, Response response) {
        String action = request.queryParams("action");
        if (Objects.equals(action, "Wake-up")) {
            sleeping = false;
        } else if (Objects.equals(action, "Sleep")) {
            sleeping = true;
            LcdHandler.disableBacklight();
        }
        return "";
    }
}
