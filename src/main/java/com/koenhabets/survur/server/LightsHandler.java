package com.koenhabets.survur.server;

import spark.Request;
import spark.Response;

import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class LightsHandler {
    static boolean A = false;
    static boolean B = false;
    static boolean C = false;

    static boolean ledStrip = false;
    static boolean espLed = true;
    static int ledRed = 0;
    static int ledGreen = 0;
    static int ledBlue = 0;
    static long lastEspStatusUpdate;
    static boolean lamp1;

    private static int AOn = 13976916;
    private static int AOff = 13976913;
    private static int BOn = 13979988;
    private static int BOff = 13979985;
    private static int COn = 13980756;
    private static int COff = 13980753;
    String response;

    public LightsHandler() {
        Timer updateTimerRoom = new Timer();
        updateTimerRoom.scheduleAtFixedRate(new update(), 0, 15 * 1000);

        Timer updateLights = new Timer();
        updateLights.scheduleAtFixedRate(new updateLights(), 0, 90 * 60 * 1000);
    }

    static void setLedStrip(int red, int green, int blue) {
        ledRed = red;
        ledGreen = green;
        ledBlue = blue;
        Mqtt.publishMessage("home/led", red + "," + green + "," + blue + "&" + 800);
        if (red == 0 && green == 0 && blue == 0) {
            ledStrip = false;
        } else {
            ledStrip = true;
        }
        WebSocketHandler.updateAll();
    }

    static void fadeLedStrip(int red, int green, int blue, int time) {
        ledRed = red;
        ledGreen = green;
        ledBlue = blue;
        Mqtt.publishMessage("home/led", red + "," + green + "," + blue + "&" + time);
        if (red == 0 && green == 0 && blue == 0) {
            ledStrip = false;
        } else {
            ledStrip = true;
        }
        WebSocketHandler.updateAll();
    }

    String setLed(Request request, Response response) {
        String parm = request.queryParams("color");
        String[] split = parm.split(",");
        int red = Integer.parseInt(split[0]);
        int green = Integer.parseInt(split[1]);
        int blue = Integer.parseInt(split[2]);
        fadeLedStrip(red, green, blue, 500);
        return ":)";
    }

    static void setMqttLamp(int lamp, boolean status) {
        lamp1 = status;
        Mqtt.publishMessage("home/lamp", status + "");
        WebSocketHandler.updateAll();
    }

    static void Light(String light, boolean status) {
        int code = 0;
        if (light.equals("A") && status) {
            code = AOn;
            A = true;
        } else if (light.equals("A") && !status) {
            code = AOff;
            A = false;
        } else if (light.equals("B") && status) {
            code = BOn;
            B = true;
        } else if (light.equals("B") && !status) {
            code = BOff;
            B = false;
        } else if (light.equals("C") && status) {
            code = COn;
            C = true;
        } else if (light.equals("C") && !status) {
            code = COff;
            C = false;
        }

        final int thing = code;
        Thread t = new Thread(() -> {
            String command = "./home/pi/scripts/433Utils/RPi_utils/codesend " + thing;
            light(command);
            light(command);
            light(command);
        });
        t.start();

        WebSocketHandler.updateAll();
    }

    private synchronized static void light(String command) {
        ExecuteShellCommand com = new ExecuteShellCommand();
        com.executeCommand(command);
    }

    static void resetLights(int ledFadeTime) {
        //Light("Aoff");
        Light("B", false);
        Light("C", false);
        setMqttLamp(1, false);
        fadeLedStrip(0, 0, 0, ledFadeTime);
    }

    static void resetLights() {
        //Light("Aoff");
        Light("B", false);
        Light("C", false);
        setMqttLamp(1, false);
        setLedStrip(0, 0, 0);
    }

    String setLight(Request request, Response response) {
        String parm = request.queryParams("light");
        boolean status = false;
        String lamp = "";
        if (Objects.equals(parm, "Aon") || Objects.equals(parm, "aon")) {
            status = true;
            lamp = "A";
        } else if (Objects.equals(parm, "Aoff") || Objects.equals(parm, "aoff")) {
            status = false;
            lamp = "A";
        } else if (Objects.equals(parm, "Bon") || Objects.equals(parm, "bon")) {
            status = true;
            lamp = "B";
        } else if (Objects.equals(parm, "Boff") || Objects.equals(parm, "boff")) {
            status = false;
            lamp = "B";
        } else if (Objects.equals(parm, "Con") || Objects.equals(parm, "con")) {
            status = true;
            lamp = "C";
        } else if (Objects.equals(parm, "Coff") || Objects.equals(parm, "coff")) {
            status = false;
            lamp = "C";
        }
        if (lamp != "") {
            Light(lamp, status);
        } else {
            try {
                String split[] = parm.split(";");
                setMqttLamp(Integer.parseInt(split[0]) , Boolean.parseBoolean(split[1]));
            } catch (Exception ignored) {

            }

        }
        return ":)";
    }

    private class update extends TimerTask {
        @Override
        public void run() {
            Calendar cal = Calendar.getInstance();
            if (cal.getTimeInMillis() - lastEspStatusUpdate > 10000) {
                espLed = false;
                ledStrip = false;
                ledBlue = 0;
                ledGreen = 0;
                ledRed = 0;
                WebSocketHandler.updateAll();
            }
        }
    }

    private class updateLights extends TimerTask {
        @Override
        public void run() {
            //Light("A", A);
            Light("B", B);
            Light("C", C);
        }
    }
}
