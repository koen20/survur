package com.koenhabets.survur.server;


import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

public class Sonarr {
    private String baseUrl = "https://koenhabets.nl/sonarr/api/calendar";
    private String upcoming;

    public Sonarr() {
        try {
            getCalendar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCalendar() throws IOException {
        URL url = new URL(baseUrl + "?apikey=" + KeyHolder.getSonarrApiKey());
        upcoming = Resources.toString(url, Charset.defaultCharset());


        return upcoming;
    }
}
