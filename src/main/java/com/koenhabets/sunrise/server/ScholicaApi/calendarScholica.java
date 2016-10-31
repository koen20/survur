package com.koenhabets.sunrise.server.ScholicaApi;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class calendarScholica {
    static String schedule;

    public static void getCalendar() throws Exception {
        String url = "https://api.scholica.com/2.0/communities/1/authenticate";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        //con.setRequestProperty("User-Agent", USER_AGENT);
        //con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "token=" + requestToken.requestToken;

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

    static void checkSchedule() throws JSONException {
        try {
            getCalendar();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject(schedule);
        JSONObject jsonMain = jsonObject.getJSONObject("result");
        JSONArray jsonArray = jsonMain.getJSONArray("items");
        for (int i = 0; i < jsonArray.length(); i++) {
            int uur = i + 1;
            String title = uur + ". Tussenuur";
            String lokaal = "";
            JSONObject vak = jsonArray.getJSONObject(i);

            title = vak.getString("title");
            lokaal = vak.getString("subtitle");

        }
    }
}
