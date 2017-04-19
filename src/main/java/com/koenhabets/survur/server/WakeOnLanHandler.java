package com.koenhabets.survur.server;

import spark.Request;
import spark.Response;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class WakeOnLanHandler {
    public static final int PORT = 9;
    private long miliseconds = 0;
    static boolean pcOn = false;

    public WakeOnLanHandler() {
        Timer updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new updateStatus(), 0, 60 * 1000);
    }

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }

    public String wol(Request request, Response response) throws Exception {
        String ipStr = "192.168.2.45";
        String macStr = "90:2B:34:33:58:E7";
        ExecuteShellCommand com = new ExecuteShellCommand();
        com.executeCommand("wakeonlan 90:2B:34:33:58:E7");

        byte[] macBytes = getMacBytes(macStr);
        byte[] bytes = new byte[6 + 16 * macBytes.length];
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) 0xff;
        }
        for (int i = 6; i < bytes.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
        }

        InetAddress address = InetAddress.getByName(ipStr);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);
        socket.close();

        return "sent";
    }

    public String status(Request request, Response response) throws Exception {
        Calendar cal = Calendar.getInstance();
        miliseconds = cal.getTimeInMillis();
        if (!pcOn) {
            WebSocketHandler.updateAll();
        }
        pcOn = true;
        return ":)";
    }

    private class updateStatus extends TimerTask {
        @Override
        public void run() {
            Calendar cal = Calendar.getInstance();
            long Cmiliseconds = cal.getTimeInMillis();
            long milisecondsDif = Cmiliseconds - miliseconds;
            if (milisecondsDif > 5 * 60 * 1000) {
                if (pcOn) {
                    WebSocketHandler.updateAll();
                }
                pcOn = false;
            }
        }
    }

}
