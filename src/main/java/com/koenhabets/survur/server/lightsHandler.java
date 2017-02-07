package com.koenhabets.survur.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class lightsHandler implements HttpHandler {
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
        if (Objects.equals(light, "Aon")) {
            code = AOn;
            A = true;
        } else if (Objects.equals(light, "Aoff")) {
            code = AOff;
            A = false;
        } else if (Objects.equals(light, "Bon")) {
            code = BOn;
            B = true;
        } else if (Objects.equals(light, "Boff")) {
            code = BOff;
            B = false;
        } else if (Objects.equals(light, "Con")) {
            code = COn;
            C = true;
        } else if (Objects.equals(light, "Coff")) {
            code = COff;
            C = false;
        }
        ExecuteShellCommand com = new ExecuteShellCommand();
        com.executeCommand("./home/pi/433Utils/RPi_utils/codesend " + code);
        ExecuteShellCommand com2 = new ExecuteShellCommand();
        com2.executeCommand("./home/pi/433Utils/RPi_utils/codesend " + code);
        ExecuteShellCommand com3 = new ExecuteShellCommand();
        com3.executeCommand("./home/pi/433Utils/RPi_utils/codesend " + code);
    }

    static void resetLights() {
        Light("Aoff");
        Light("Boff");
        Light("Coff");
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String parm = httpExchange.getRequestURI().getQuery();
        Light(parm);

        response = "Sent";
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
