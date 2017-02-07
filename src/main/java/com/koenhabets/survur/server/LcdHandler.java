package com.koenhabets.survur.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class LcdHandler implements HttpHandler {
    String response = "Sent";
    private int code = 200;
    private int d = 0;
    private int counter = 500;

    public LcdHandler() {
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new UpdateTask(), 0, 10 * 1000);
    }

    public static void printLcd(String text, String text2) {
        ExecuteShellCommand com = new ExecuteShellCommand();
        //String response = com.executeCommand("python /home/pi/lcd2/text.py \"" + text + "\" " + "\"" + text2 + "\"");
        com.executeCommand("python /home/pi/lcd2/text.py " + text + " " + text2);
    }

    public static void disableBacklight() {
        ExecuteShellCommand com = new ExecuteShellCommand();
        com.executeCommand("python /home/pi/lcd2/disablelight.py");
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        String[] parts = parm.split(";");
        printLcd(parts[0], parts[1]);

        httpExchange.sendResponseHeaders(code, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private class UpdateTask extends TimerTask {
        @Override
        public void run() {
            double temp = TemperatureHandler.temp;
            double tempOutside = TemperatureHandler.tempOutside;
            counter++;
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
                if (d == 0) {
                    printLcd(hour + ":" + minute, "Binnen:" + temp);
                    d = 1;
                } else {
                    printLcd(hour + ":" + minute, "Buiten:" + tempOutside);
                    d = 0;
                }
            }

            if (counter == 6) {
                disableBacklight();
            }
        }
    }
}
