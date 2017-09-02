package com.koenhabets.survur.server.ZermeloApi;

import com.koenhabets.survur.server.KeyHolder;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class calendarZermelo {
    public static int count = 500;
    public static String nextSubject;

    public calendarZermelo() {
        count = getFirstHour(parseResponse(getAppointments(getStartTime(1), getEndTime(1))));
    }
    String getAppointments(long startTime, long endTime){
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://bernardinuscollege.zportal.nl/api/v3/appointments").newBuilder();
        urlBuilder.addQueryParameter("user", "~me");
        urlBuilder.addQueryParameter("access_token", KeyHolder.getZermeloCode());
        urlBuilder.addQueryParameter("start", Long.toString(startTime));
        urlBuilder.addQueryParameter("end", Long.toString(endTime));

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String res = null;
        try {
            res = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(res);
        return res;
    }

    private List<TimeTableItem> parseResponse(String response) {
        List<TimeTableItem> timeTableItem = new ArrayList<>();
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
        return timeTableItem;
    }

    private int getFirstHour(List<TimeTableItem> timeTableItems){
        int c = 50;
        try {
            TimeTableItem item = timeTableItems.get(0);
            if (item.isCancelled()){
                item = timeTableItems.get(1);
            }

            c = item.getHour() - 1;
            nextSubject = item.getSubject();

        } catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        System.out.println(c);

        return c;
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
