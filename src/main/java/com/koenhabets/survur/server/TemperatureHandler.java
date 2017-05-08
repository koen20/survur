package com.koenhabets.survur.server;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import spark.Request;
import spark.Response;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class TemperatureHandler {
    static double temp = 20;
    static double tempAvarageInside = 20;
    static double tempAvarageOutside = 12;
    static double livingRoomTemp = 20;
    static double tempOutside;
    static String vdd;
    private static double[] tempArray = new double[5];
    private static double[] tempArrayOutside = new double[5];
    String response;
    private String tempTime;
    private String tempData;
    private String vddData;
    private String outsideTemp;
    private String tempDataLivingRoom;
    private int tempArrayLength = 160;

    public TemperatureHandler() {
        Timer updateTimerGraph = new Timer();
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new UpdateTask(), 0, 180 * 1000);
        updateTimerGraph.scheduleAtFixedRate(new UpdateTaskGraph(), 1000, 20 * 60 * 1000);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private double getTemp() {
        String d;
        ExecuteShellCommand com = new ExecuteShellCommand();
        d = com.executeCommand("bash /home/pi/scripts/temp.py");
        d = d.replace("\n", "");
        try {
            temp = Double.parseDouble(d);
        } catch (NumberFormatException e) {
            System.err.println("Error while parsing the temperature: " + e.getMessage());
            temp = 20;
        }
        return temp;
    }

    private double avarageTemp() {

        if (tempArray[1] == 0) {
            tempArray[0] = temp;
            tempArray[1] = temp;
            tempArray[2] = temp;
            tempArray[3] = temp;
            tempArray[4] = temp;
        }
        tempArray[4] = tempArray[3];
        tempArray[3] = tempArray[2];
        tempArray[2] = tempArray[1];
        tempArray[1] = tempArray[0];
        tempArray[0] = temp;
        tempAvarageInside = (tempArray[0] + tempArray[1] + tempArray[2] + tempArray[3] + tempArray[4]) / 5;
        tempAvarageInside = round(tempAvarageInside, 2);
        return tempAvarageInside;
    }

    public String getTemperature(Request request, Response res) {
        response = "";
        String location = request.queryParams("location");
        if (Objects.equals(location, "graph")) {
            response = "[" + tempData + "," + tempTime + "," + outsideTemp + "," + tempDataLivingRoom + "," + vddData + "]";
        } else if (Objects.equals(location, "outside")) {
            response = tempOutside + "";
        } else if (Objects.equals(location, "inside")) {
            response = temp + "";
        }
        return response;
    }

    public String setTemperature(Request request, Response res) {
        vdd = request.queryParams("vdd");
        livingRoomTemp = Double.parseDouble(request.queryParams("temperature"));
        return "Sent";
    }

    private void getTempOutside() {
        String d;
        ExecuteShellCommand com = new ExecuteShellCommand();
        d = com.executeCommand("bash /home/pi/scripts/tempOutside");
        d = d.replace("\n", "");
        tempOutside = Double.parseDouble(d);
    }

    private double avarageTempOutside() {
        if (tempArrayOutside[1] == 0) {
            tempArrayOutside[0] = tempOutside;
            tempArrayOutside[1] = tempOutside;
            tempArrayOutside[2] = tempOutside;
        }
        tempArrayOutside[2] = tempArrayOutside[1];
        tempArrayOutside[1] = tempArrayOutside[0];
        tempArrayOutside[0] = tempOutside;
        tempAvarageOutside = (tempArrayOutside[0] + tempArrayOutside[1] + tempArrayOutside[2]) / 3;
        tempAvarageOutside = round(tempAvarageOutside, 2);
        return tempAvarageOutside;
    }


    private class UpdateTask extends TimerTask {

        @Override
        public void run() {
            getTemp();
            getTempOutside();
            avarageTemp();
            avarageTempOutside();
            try {
                WebSocketHandler.updateAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class UpdateTaskGraph extends TimerTask {

        @Override
        public void run() {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            //TEMP moving avarage//////////////////////
            String result = null;
            try {
                File file1 = new File("temp.json");
                result = Files.toString(file1, Charsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONParser parser = new JSONParser();
            JSONArray ja;
            Object objt = null;
            try {
                objt = parser.parse(result);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ja = (JSONArray) objt;
            if (ja.size() > tempArrayLength) {
                ja.remove(0);
            }
            ja.add(tempAvarageInside);

            try {
                File fileS = new File("temp.json");
                Files.write(ja.toJSONString(), fileS, Charsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            tempTime = ja.toJSONString();


            //TEMP time//////////////////////
            parser = new JSONParser();
            ja = new JSONArray();
            result = null;
            try {
                File file = new File("time.json");
                result = Files.toString(file, Charsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Object obj = parser.parse(result);
                ja = (JSONArray) obj;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (ja.size() > tempArrayLength) {
                ja.remove(0);
            }
            ja.add(hour + ":" + minute);

            try {
                File file = new File("time.json");
                Files.write(ja.toJSONString(), file, Charsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }

            tempData = ja.toJSONString();


            //outside temp//////////////////////
            parser = new JSONParser();
            ja = new JSONArray();
            result = null;
            try {
                File file = new File("tempOutside.json");
                result = Files.toString(file, Charsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Object obj = parser.parse(result);
                ja = (JSONArray) obj;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (ja.size() > tempArrayLength) {
                ja.remove(0);
            }
            ja.add(tempAvarageOutside);

            try {
                File file = new File("tempOutside.json");
                Files.write(ja.toJSONString(), file, Charsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            outsideTemp = ja.toJSONString();


            //living room temp//////////////////////
            parser = new JSONParser();
            ja = new JSONArray();
            result = null;
            try {
                File file = new File("livingRoomTemp.json");
                result = Files.toString(file, Charsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Object obj = parser.parse(result);
                ja = (JSONArray) obj;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (ja.size() > tempArrayLength) {
                ja.remove(0);
            }
            ja.add(livingRoomTemp);
            try {
                File file = new File("livingRoomTemp.json");
                Files.write(ja.toJSONString(), file, Charsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            tempDataLivingRoom = ja.toJSONString();


            //vdd//////////////////////
            parser = new JSONParser();
            ja = new JSONArray();
            result = null;
            try {
                File file = new File("vdd.json");
                result = Files.toString(file, Charsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Object obj = parser.parse(result);
                ja = (JSONArray) obj;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (ja.size() > tempArrayLength) {
                ja.remove(0);
            }
            ja.add(vdd);
            try {
                File file = new File("vdd.json");
                Files.write(ja.toJSONString(), file, Charsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            vddData = ja.toJSONString();
        }
    }
}
