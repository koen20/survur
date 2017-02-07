package com.koenhabets.survur.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class TemperatureHandler implements HttpHandler {
    static double temp = 500;
    static double tempAvarageInside;
    static double tempAvarageOutside;
    static double livingRoomTemp;
    static double tempOutside;
    private static double[] tempArray = new double[5];
    private static double[] tempArrayOutside = new double[5];
    String response;

    private String tempTime;
    private String tempData;
    private String outsideTemp;
    private String tempDataLivingRoom;
    private int tempArrayLength = 160;

    public TemperatureHandler() {
        Timer updateTimerGraph = new Timer();
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new UpdateTask(), 0, 180 * 1000);
        updateTimerGraph.scheduleAtFixedRate(new UpdateTaskGraph(), 0, 20 * 60 * 1000);
    }

    private double getTemp() {
        String d;
        ExecuteShellCommand com = new ExecuteShellCommand();
        d = com.executeCommand("bash /var/www/html/cgi-bin/temp.py");
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
        return tempAvarageInside;
    }

    private double getLivingRoomTemp() throws IOException {
        String url = "http://192.168.2.47";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        //con.setRequestProperty("User-Agent", USER_AGENT);
        //con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String text = response.toString();
        livingRoomTemp = Double.parseDouble(text);
        return livingRoomTemp;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        if (Objects.equals(parm, "graph")) {
            response = "[" + tempData + "," + tempTime + "," + outsideTemp + "," + tempDataLivingRoom + "]";
        } else if (Objects.equals(parm, "tempOutside")) {
            response = tempOutside + "";
        } else {
            temp = getTemp();
            response = temp + "";
        }


        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private double getTempOutside() {
        String d;
        ExecuteShellCommand com = new ExecuteShellCommand();
        d = com.executeCommand("bash /home/pi/tempOutside");
        d = d.replace("\n", "");
        tempOutside = Double.parseDouble(d);
        return tempOutside;
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
        return tempAvarageOutside;
    }


    private class UpdateTask extends TimerTask {

        @Override
        public void run() {
            getTemp();
            getTempOutside();
            avarageTemp();
            avarageTempOutside();
        }
    }

    private class UpdateTaskGraph extends TimerTask {

        @Override
        public void run() {
            try {
                getLivingRoomTemp();
            } catch (IOException e) {
                livingRoomTemp = 20;
            }

            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);

            //TEMP moving avarage//////////////////////
            JSONParser parser = new JSONParser();
            JSONArray ja = new JSONArray();
            try {

                Object obj = parser.parse(new FileReader("temp.json"));

                ja = (JSONArray) obj;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //System.out.println("Stored: " + ja.toString());
            if (ja.size() > tempArrayLength) {
                ja.remove(0);
            }
            ja.add(tempAvarageInside);
            //System.out.println("Saving: " + ja.toString());
            try {

                FileWriter file = new FileWriter("temp.json");
                file.write(ja.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            tempTime = ja.toJSONString();

            //TEMP time//////////////////////
            parser = new JSONParser();
            ja = new JSONArray();
            try {

                Object obj = parser.parse(new FileReader("time.json"));

                ja = (JSONArray) obj;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //System.out.println("Stored: " + ja.toString());
            if (ja.size() > tempArrayLength) {
                ja.remove(0);
            }
            ja.add(hour + ":" + minute);
            //System.out.println("Saving: " + ja.toString());
            try {

                FileWriter file = new FileWriter("time.json");
                file.write(ja.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            tempData = ja.toJSONString();


            //outside temp//////////////////////
            parser = new JSONParser();
            ja = new JSONArray();
            try {

                Object obj = parser.parse(new FileReader("tempOutside.json"));

                ja = (JSONArray) obj;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //System.out.println("Stored: " + ja.toString());
            if (ja.size() > tempArrayLength) {
                ja.remove(0);
            }
            ja.add(tempAvarageOutside);
            //System.out.println("Saving: " + ja.toString());
            try {

                FileWriter file = new FileWriter("tempOutside.json");
                file.write(ja.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            outsideTemp = ja.toJSONString();


            //living room temp//////////////////////
            parser = new JSONParser();
            ja = new JSONArray();
            try {

                Object obj = parser.parse(new FileReader("livingRoomTemp.json"));

                ja = (JSONArray) obj;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //System.out.println("Stored: " + ja.toString());
            if (ja.size() > tempArrayLength) {
                ja.remove(0);
            }
            ja.add(livingRoomTemp);
            //System.out.println("Saving: " + ja.toString());
            try {

                FileWriter file = new FileWriter("livingRoomTemp.json");
                file.write(ja.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            tempDataLivingRoom = ja.toJSONString();
        }
    }
}
