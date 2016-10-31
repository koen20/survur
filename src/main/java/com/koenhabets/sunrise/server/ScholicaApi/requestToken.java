package com.koenhabets.sunrise.server.ScholicaApi;

import com.koenhabets.sunrise.server.KeyHolder;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by koenh on 31-10-2016.
 */
public class requestToken {
    static String requestToken;
    static void requestToken() throws Exception {
        String url = "https://api.scholica.com/2.0/communities/1/authenticate?username=407332&password="
                + KeyHolder.getScholicaPasswd() +
                "&access_token=470d7d90cae6e34f36bc9110026a4370e8864551b0e7e7b33263163562c362a3d68f1937";
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
        requestToken = response.toString();
    }
}
