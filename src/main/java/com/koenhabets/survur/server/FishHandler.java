package com.koenhabets.survur.server;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class FishHandler implements HttpHandler {
    private String response;
    static int food = 150;

    public FishHandler() throws IOException {
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new UpdateTask(), 0, 30 * 60 * 1000);
        readFood();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();

        if(Objects.equals(parm,"feed")){
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if(food < 150 && hour < 20 && hour > 6){
                food = food + 80;
            }
        }
        response = food + "";
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
        saveFood();
    }

    private class UpdateTask extends TimerTask{

        @Override
        public void run() {
            food = food - 5;
            try {
                saveFood();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFood() throws IOException {
        File file = new File("food.txt");
        Files.write(food + "", file, Charsets.UTF_8);
    }
    private void readFood() throws IOException {
        File file = new File("food.txt");
        String result = Files.toString(file, Charsets.UTF_8);
        food = Integer.parseInt(result);
    }

}
