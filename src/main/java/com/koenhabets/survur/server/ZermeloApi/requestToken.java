package com.koenhabets.survur.server.ZermeloApi;

import com.koenhabets.survur.server.KeyHolder;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class requestToken {
    static String requestToken;

    public static void requestToken() throws Exception {
        String url = "https://bernardinuscollege.zportal.nl/api/v3/oauth/token";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        //con.setRequestProperty("User-Agent", USER_AGENT);
        //con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "grant_type=authorization&code="
                + KeyHolder.getZermeloCode();

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
        //JSONObject responsetoken = new JSONObject(response.toString());
        //JSONObject jsonMain = responsetoken.getJSONObject("result");
        requestToken = "";
    }
}
