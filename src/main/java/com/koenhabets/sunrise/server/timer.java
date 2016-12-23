package com.koenhabets.sunrise.server;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class timer extends TimerTask {
    static String tempTime;
    static String tempData;
    static String outsideTemp;
    int d = 0;
    int counter = 500;
    int counter2 = 999;

    public static void main() {
        TimerTask timerTask = new timer();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 10 * 1000);
    }

    @Override
    public void run() {
        counter++;
        counter2++;
        if (ActionHandler.sleeping && counter >= 499) {
            counter = 0;
        }
        if (!ActionHandler.inside && counter >= 499) {
            counter = 0;
        } else if (ActionHandler.inside && !ActionHandler.sleeping) {
            counter = 500;
        }
        if (!ActionHandler.sleeping && ActionHandler.inside) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            String temp = TemperatureHandler.getTemp();
            int outsideTemp = WeatherHandler.getTemp();
            if (d == 0) {
                LcdHandler.printLcd(hour + ":" + minute, "Binnen:" + temp);
                d = 1;
            } else {
                LcdHandler.printLcd(hour + ":" + minute, "Buiten:" + outsideTemp);
                d = 0;
            }
        }

        if (counter == 6) {
            LcdHandler.disableBacklight();
        }

        if (counter2 > 120) {
            String temp = TemperatureHandler.getTemp();
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
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
            if (ja.size() > 100) {
                ja.remove(0);
            }
            ja.add(temp);
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
            if (ja.size() > 100) {
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
            if (ja.size() > 100) {
                ja.remove(0);
            }
            ja.add(WeatherHandler.getTemp());
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

            counter2 = 0;
        }
    }
}
