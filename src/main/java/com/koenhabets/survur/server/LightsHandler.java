package com.koenhabets.survur.server;

import spark.Request;
import spark.Response;

import java.util.Calendar;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.round;

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
        updateLights.scheduleAtFixedRate(new updateLights(), 0, 60 * 60 * 1000);
    }

    static void setLedStrip(int red, int green, int blue) {
        ledRed = red;
        ledGreen = green;
        ledBlue = blue;
        Mqtt.publishMessage("home/led", red + "," + green + "," + blue);
        if (red == 0 && green == 0 && blue == 0) {
            ledStrip = false;
        } else {
            ledStrip = true;
        }
        WebSocketHandler.updateAll();
    }

    static void fadeLedStrip(int red, int green, int blue, int time) {//todo add this directly to the esp
        setLedStrip(red, green, blue);
        /*final int ledRedStart = ledRed;
        final int ledGreenStart = ledGreen;
        final int ledBlueStart = ledBlue;

        Thread t = new Thread(() -> {
            try {
                int steps = time / 100;//calculate amount of steps required to make every step 100 milliseconds within time
                for (int i = 0; i <= steps; i++) {
                    int r = ledRedStart + round((red - ledRedStart) / steps * i);
                    int g = ledGreenStart + round((green- ledGreenStart) / steps * i);
                    int b = ledBlueStart + round((blue - ledBlueStart) / steps * i);
                    Mqtt.publishMessage("home/led", r + "," + g + "," + b);
                    Thread.sleep(100);
                }
                Mqtt.publishMessage("home/led", red + "," + green + "," + blue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        ledRed = red;
        ledGreen = green;
        ledBlue = blue;*/
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

    static void Light(String light) {
        int code = 0;
        if (Objects.equals(light, "Aon") || Objects.equals(light, "aon")) {
            code = AOn;
            A = true;
        } else if (Objects.equals(light, "Aoff") || Objects.equals(light, "aoff")) {
            code = AOff;
            A = false;
        } else if (Objects.equals(light, "Bon") || Objects.equals(light, "bon")) {
            code = BOn;
            B = true;
        } else if (Objects.equals(light, "Boff") || Objects.equals(light, "boff")) {
            code = BOff;
            B = false;
        } else if (Objects.equals(light, "Con") || Objects.equals(light, "con")) {
            code = COn;
            C = true;
        } else if (Objects.equals(light, "Coff") || Objects.equals(light, "coff")) {
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

    static void Light(String light, boolean status){
        int code = 0;
        if(light.equals("A") && status){
            code = AOn;
            A = true;
        } else if(light.equals("A") && !status){
            code = AOff;
            A = false;
        } else if(light.equals("B") && status){
            code = BOn;
            B = true;
        } else if(light.equals("B") && !status){
            code = BOff;
            B = false;
        } else if(light.equals("C") && status){
            code = COn;
            C = true;
        } else if(light.equals("C") && !status){
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
        Light("Aoff");
        Light("Boff");
        Light("Coff");
        fadeLedStrip(0, 0, 0, ledFadeTime);
    }

    static void resetLights() {
        Light("Aoff");
        Light("Boff");
        Light("Coff");
        setLedStrip(0, 0, 0);
    }

    String setLight(Request request, Response response) {
        Log.d(request.ip());
        String parm = request.queryParams("light");
        Light(parm);
        return ":)";
    }

    private class update extends TimerTask {
        @Override
        public void run() {
            Calendar cal = Calendar.getInstance();
            if(cal.getTimeInMillis() - lastEspStatusUpdate > 10000){
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
            Light("A", A);
            Light("B", B);
            Light("C", C);
        }
    }
}
