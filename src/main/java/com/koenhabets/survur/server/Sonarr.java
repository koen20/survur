package com.koenhabets.survur.server;


import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

public class Sonarr {
    private String baseUrl = "https://koenhabets.nl/sonarr/api/calendar";
    private String upcoming;

    public Sonarr() {
        getCalendar();
    }

    private String getCalendar() {
        try {
            URL url = new URL(baseUrl + "?apikey=" + KeyHolder.getSonarrApiKey());
            upcoming = Resources.toString(url, Charset.defaultCharset());
        } catch (IOException ignored) {
        }

        return upcoming;
    }
}
