package com.koenhabets.sunrise.server.ScholicaApi;


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

public class calendarScholica {
    static String schedule;
    public static int count;

    public static void getCalendar() throws Exception {
        String url = "https://api.scholica.com/2.0/communities/1/calendar/schedule";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        //con.setRequestProperty("User-Agent", USER_AGENT);
        //con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "token=" + requestToken.requestToken + "&time=" + getStartOfDayInMillis() / 1000;

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
    }

    public static void checkSchedule() throws JSONException {
        try {
            getCalendar();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject(schedule);
        JSONObject jsonMain = jsonObject.getJSONObject("result");
        JSONArray jsonArray = jsonMain.getJSONArray("items");

        JSONObject vak = jsonArray.getJSONObject(0);
        String title;
        String uur = "0";
        title = vak.getString("title");
        String first = title.substring(0, 1);
        if (!Objects.equals(first, "1")) {
            count++;
            uur = "3";
        }
        vak = jsonArray.getJSONObject(1);
        title = vak.getString("title");
        first = title.substring(0, 1);
        if (!Objects.equals(first, uur) && count != 0) {
            count++;
        }
    }

    public static long getStartOfDayInMillis() {
        Calendar cal = Calendar.getInstance();
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        currentDay = currentDay + 1;
        if (currentDay > 31) {
            currentDay = 1;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, currentDay);
        return calendar.getTimeInMillis();
    }
}
