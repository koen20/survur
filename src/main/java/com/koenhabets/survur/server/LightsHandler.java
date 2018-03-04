package com.koenhabets.survur.server;

import spark.Request;
import spark.Response;

import java.util.Objects;

public class LightsHandler {
    static boolean A = false;
    static boolean B = false;
    static boolean C = false;
    private static int AOn = 13976916;
    private static int AOff = 13976913;
    private static int BOn = 13979988;
    private static int BOff = 13979985;
    private static int COn = 13980756;
    private static int COff = 13980753;
    String response;

    static void Light(String light) {
        int code = 0;
        if (Objects.equals(light, "Aon") || Objects.equals(light, "aon")) {
            code = AOn;
            A = true;
        } else if (Objects.equals(light, "Aoff" ) || Objects.equals(light, "aoff")) {
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

    private synchronized static void light(String command){
        ExecuteShellCommand com = new ExecuteShellCommand();
        com.executeCommand(command);
    }

    static void resetLights() {
        Light("Aoff");
        Light("Boff");
        Light("Coff");
    }

    String setLight(Request request, Response response) {
        Log.d(request.ip());
        String parm = request.queryParams("light");
        Light(parm);
        return ":)";
    }
}
