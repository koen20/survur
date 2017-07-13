package com.koenhabets.survur.server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class WebSocket2 {
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

    public static void listen(){
        for (Session sessiond : sessions) {
            try {
                sessiond.getRemote().sendString("listen;");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void voice(String message){
        for (Session sessiond : sessions) {
            try {
                sessiond.getRemote().sendString("voice;" + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
