package com.koenhabets.survur.server;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


@WebSocket
public class WebSocketHandler {
    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    @OnWebSocketConnect
    public void connected(Session session) {
        sessions.add(session);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        session.getRemote().sendString(InfoHandler.getJsonInfo().toString());

    }
    public static void updateAll() {
        for (Session sessiond : sessions) {
            try {
                sessiond.getRemote().sendString(InfoHandler.getJsonInfo().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
