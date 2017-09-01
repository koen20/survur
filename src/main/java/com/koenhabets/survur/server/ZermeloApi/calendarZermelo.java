package com.koenhabets.survur.server.ZermeloApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class calendarZermelo {
    public static int count = 500;
    public static String nextSubject;
    private List<TimeTableItem> timeTableItem = new ArrayList<>();

    public calendarZermelo() {
        parseResponse(getAppointments(getStartTime(1), getEndTime(1)));
        getFirstHour(timeTableItem);
    }
    String getAppointments(long startTime, long endTime){
        StringBuffer response = new StringBuffer();

        try {
            String url = "https://bernardinuscollege.zportal.nl/api/v3/appointments";
            URL obj = new URL(url);
            HttpURLConnection con = null;
            con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("GET");
            //con.setRequestProperty("User-Agent", USER_AGENT);
            //con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String urlParameters = "user=~me&access_token=" + "" + "&start=" + startTime + "&end=" + endTime;
            System.out.println(urlParameters);

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(response.toString());
        return response.toString();
    }

    private void parseResponse(String response) {
        timeTableItem.clear();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonResp = jsonObject.getJSONObject("response");
            JSONArray jsonArray = jsonResp.getJSONArray("data");

            int lastHour = 0;

            for (int w = 1; w < 12; w++) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject lesson = jsonArray.getJSONObject(i);
                        if (lesson.getInt("startTimeSlot") == w) {
                            if (lastHour != lesson.getInt("startTimeSlot")) {
                                JSONArray subjects = lesson.getJSONArray("subjects");
                                JSONArray locations = lesson.getJSONArray("locations");
                                TimeTableItem item = new TimeTableItem(subjects.getString(0), locations.getInt(0), lesson.getInt("startTimeSlot"), lesson.getBoolean("cancelled"));
                                timeTableItem.add(item);
                            }
                            lastHour = lesson.getInt("startTimeSlot");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getFirstHour(List<TimeTableItem> timeTableItems){
        try {
            TimeTableItem item = timeTableItems.get(0);
            if (item.getHour() == 1 && !item.isCancelled()) {
                count = 0;
            } else if (item.getHour() == 2 && !item.isCancelled()) {
                count = 1;
            } else if (item.getHour() == 3 && !item.isCancelled()) {
                count = 2;
            } else if (item.getHour() == 4 && !item.isCancelled()) {
                count = 3;
            }
        } catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    private long getStartTime(int day){
        Calendar cald = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        cald.set(Calendar.DAY_OF_MONTH, day);
        cald.set(Calendar.HOUR_OF_DAY, 1);
        System.out.println("millis" + cald.getTimeInMillis() / 1000 + "month" + cald.get(Calendar.MONTH) + "day" + cald.get(Calendar.DAY_OF_MONTH));
        return cald.getTimeInMillis() / 1000;
    }

    private long getEndTime(int day){
        Calendar cald = Calendar.getInstance(TimeZone.getTimeZone("CEST"));
        cald.set(Calendar.DAY_OF_MONTH, day);
        cald.set(Calendar.HOUR_OF_DAY, 20);
        return cald.getTimeInMillis() / 1000;
    }
}
