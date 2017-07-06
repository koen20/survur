package com.koenhabets.survur.server.ScholicaApi;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class calendarScholica {
    static String schedule;
    public static int count;
    public static String nextSubject;
    static int day;

    public calendarScholica(){
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new update(), 0, 30 * 60 * 1000);
    }

    public static String getCalendar(int day) throws Exception {
        String url = "https://api.scholica.com/2.0/communities/1/calendar/schedule";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        //con.setRequestProperty("User-Agent", USER_AGENT);
        //con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "token=" + requestToken.requestToken + "&time=" + getStartOfDayInMillis(22) / 1000;

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
        schedule = response.toString();
        return schedule;
    }

    public static void checkSchedule() throws JSONException {
        count = 0;
        try {
            requestToken.requestToken();
            Calendar cal = Calendar.getInstance();
            int day = cal.get(Calendar.DAY_OF_MONTH);
            day++;
            if (day > 31) {
                day = 1;
            }
            getCalendar(day);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject(schedule);
        JSONObject jsonMain = jsonObject.getJSONObject("result");
        JSONArray jsonArray;
        try {
            jsonArray = jsonMain.getJSONArray("items");
            JSONObject vak = jsonArray.getJSONObject(0);
            String title;
            String lokaal;
            String uur = "0";
            title = vak.getString("title");
            lokaal = vak.getString("subtitle");
            String first = title.substring(0, 1);
            if (!Objects.equals(first, "1") || Objects.equals(lokaal, "Vervallen")) {
                count++;
                uur = "3";
            }
            vak = jsonArray.getJSONObject(1);
            title = vak.getString("title");
            first = title.substring(0, 1);
            if (!Objects.equals(first, uur) && count != 0 || Objects.equals(lokaal, "Vervallen")) {
                count++;
            }
        } catch (JSONException e){
            count = 501;
        }

    }

    public static class update extends TimerTask {
        @Override
        public void run() {
            Calendar calendarc = Calendar.getInstance();
            int dayc = calendarc.get(Calendar.DAY_OF_MONTH);
            if (dayc != day) {
                try {
                    requestToken.requestToken();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                nextSubject = getSubject();
                try {
                    checkSchedule();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getTime();
            }
        }
    }


    public static String getSubject() {

        try {
            requestToken.requestToken();
            Calendar cal = Calendar.getInstance();
            int day;
            if (cal.get(Calendar.HOUR_OF_DAY) >= 16) {
                day = cal.get(Calendar.DAY_OF_MONTH) + 1;
            } else {
                day = cal.get(Calendar.DAY_OF_MONTH);
            }

            schedule = getCalendar(day);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = null;
        String title = "ddgeen les";
        try {
            jsonObject = new JSONObject(schedule);

            JSONObject jsonMain = jsonObject.getJSONObject("result");
            JSONArray jsonArray = jsonMain.getJSONArray("items");

            JSONObject vak = jsonArray.getJSONObject(0);

            title = vak.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return title.substring(2);
    }

    public static long getStartOfDayInMillis(int currentDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, currentDay);
        return calendar.getTimeInMillis();
    }

    public static void getTime() {
        Calendar cale = Calendar.getInstance();
        day = cale.get(Calendar.DAY_OF_MONTH);
    }
}
