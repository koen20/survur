package com.koenhabets.sunrise.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by koenh on 8-5-2016.
 */
public class Server {

    public static void main(String args[]) throws IOException {
        int port = 9999;
        System.out.println("Starting server on port: " + port);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/sunset-sunrise", new SunSetHandler());
        server.createContext("/wol", new WakeOnLanHandler());
        server.start();
        System.out.println("Server started.");
    }
}
