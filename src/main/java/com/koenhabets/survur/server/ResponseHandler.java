package com.koenhabets.survur.server;

import spark.Request;
import spark.Response;

import java.util.Objects;

public class ResponseHandler {
    static String lastAction;

    public String response(Request request, Response res) {
        //String action = request.queryParams("action");
        String response = request.queryParams("response");
        if (Objects.equals(lastAction, "enterLate")) {
            if (Objects.equals(response, "ja")) {
                ActionHandler.prepSleep();
            }
        }
        return "";
    }
}
