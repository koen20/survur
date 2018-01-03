package com.koenhabets.survur.server;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Calendar;

public class PowerData {
    public static void addData(double energyUsage1, double energyUsage2, double energyProduction1, double energyProduction2, double gasUsage){
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
        jsonArray.add(jsonObject);

        writeData(jsonArray);
    }

    private static JSONArray readData(){
        String result = "[]";
        try {
            File file1 = new File(ConfigHandler.directory + "PowerData.json");
            result = Files.asCharSource(file1, Charsets.UTF_8).read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONParser parser = new JSONParser();
        JSONArray ja = new JSONArray();
        try {
            Object obj = parser.parse(result);
            ja = (JSONArray) obj;
        } catch (ParseException e) {
            //e.printStackTrace();
        }
        return ja;
    }
    private static void writeData(JSONArray jsonArray){
        try {
            File fileS = new File(ConfigHandler.directory + "powerData.json");
            Files.asCharSink(fileS, Charsets.UTF_8).write(jsonArray.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
