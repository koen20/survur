package com.koenhabets.survur.server;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Calendar;

public class PowerData {
    public static void addData(double energyUsage1, double energyUsage2, double energyProduction1, double energyProduction2, double gasUsage) {
        JSONArray jsonArray = readData();
        Calendar cal = Calendar.getInstance();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("energyUsage1", energyUsage1);
        jsonObject.put("energyUsage2", energyUsage2);
        jsonObject.put("energyProduction1", energyProduction1);
        jsonObject.put("energyProduction2", energyProduction2);
        jsonObject.put("gasUsage", gasUsage);
        jsonObject.put("time", cal.getTimeInMillis());
        //add time
        jsonArray.put(jsonObject);

        writeData(jsonArray);
    }

    public static JSONArray getDataTime(long startTime, long endTime) {
        JSONArray ja = readData();
        System.out.println("read" + ja.toString());
        JSONArray jsonArray = new JSONArray();
        System.out.println("created array");
        for (int i = 0; i < ja.length(); i++) {
            JSONObject item = ja.getJSONObject(i);
            long time = item.getLong("time") / 1000;
            if (time > startTime && time < endTime) {
                jsonArray.put(item);
            }
        }
        System.out.println("dada");
        return jsonArray;
    }

    private static JSONArray readData() {
        String result = "[]";
        try {
            File file1 = new File(ConfigHandler.directory + "powerData.json");
            result = Files.asCharSource(file1, Charsets.UTF_8).read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray ja = new JSONArray(result);

        return ja;
    }

    private static void writeData(JSONArray jsonArray) {
        try {
            File fileS = new File(ConfigHandler.directory + "powerData.json");
            Files.asCharSink(fileS, Charsets.UTF_8).write(jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
