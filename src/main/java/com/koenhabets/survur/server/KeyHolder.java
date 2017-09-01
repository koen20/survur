package com.koenhabets.survur.server;

import com.google.common.io.Files;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class KeyHolder {

    private static KeyHolder instance;
    private final String arKey1;
    private final String arKey2;
    private final String weather;
    private final String scholicaPasswd;
    private final String sonarrApiKey;
    private final String zermeloCode;

    public KeyHolder(String arKey1, String arKey2, String weather, String scholicaPasswd, String sonarrApiKey, String zermeloCode) {
        this.arKey1 = arKey1;
        this.arKey2 = arKey2;
        this.weather = weather;
        this.scholicaPasswd = scholicaPasswd;
        this.sonarrApiKey = sonarrApiKey;
        this.zermeloCode = zermeloCode;
    }

    public static void init(String path) throws IOException {
        String json = Files.asCharSource(new File(path), Charset.defaultCharset()).read();
        instance = new Gson().fromJson(json, KeyHolder.class);
    }

    public static String getARKey() {
        //xt1064
        return instance.arKey1;
    }

    public static String getArKey2() {
        //xt1562
        return instance.arKey2;
    }

    public static String getWeatherKey() {
        return instance.weather;
    }

    public static String getScholicaPasswd() {
        return instance.scholicaPasswd;
    }

    public static String getSonarrApiKey() {
        return instance.sonarrApiKey;
    }

    public static String getZermeloCode() {
        return instance.zermeloCode;
    }
}
