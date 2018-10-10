package com.koenhabets.survur.server;

import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;

import java.util.Calendar;

import static com.koenhabets.survur.server.SleepHandler.sleeping;
import static org.eclipse.paho.client.mqttv3.MqttClient.generateClientId;

public class Mqtt implements MqttCallbackExtended {
    static MqttClient client;
    static String location;
    private String[] topics = {"owntracks/koen/lux/event", "home/motion", "home/status/pc", "home/button/sleep", "home/led/status"};

    public Mqtt() {
        try {
            client = new MqttClient("ssl://mqtt.koenhabets.nl:8752", generateClientId());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName(KeyHolder.getMqttUsername());
            connOpts.setPassword(KeyHolder.getMqttPassword().toCharArray());
            connOpts.setAutomaticReconnect(true);
            connOpts.setCleanSession(false);
            client.connect(connOpts);
            client.setCallback(this);
            client.subscribe(topics);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    static void publishMessage(String topic, String content) {
        try {
            MqttClient client2 = new MqttClient("ssl://mqtt.koenhabets.nl:8752", "survur-publish");
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName(KeyHolder.getMqttUsername());
            connOpts.setPassword(KeyHolder.getMqttPassword().toCharArray());
            client2.connect(connOpts);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(0);
            client2.publish(topic, message);
            client2.disconnect();
            client2.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        if (topic.equals("owntracks/koen/oneplus6/event")) {
            JSONObject jsonObject = new JSONObject(message.toString());
            String event = jsonObject.getString("event");
            String desc = jsonObject.getString("desc");
            location = desc;
            Log.d(event + " " + desc);
            if (desc.equals("Thuis")) {
                if (event.equals("enter")) {
                    SleepHandler.inside = true;
                    Log.d("inside");
                } else {
                    SleepHandler.inside = false;
                }
            }
            if(!event.equals("enter")){
                location = "onbekend";
            }
        } else if (topic.equals("home/motion")) {
            if(ConfigHandler.motionEnabled) {
                RoomHandler.enterRoom();
            }
        } else if (topic.equals("home/status/pc")) {
            if (message.toString().equals("online")) {
                WakeOnLanHandler.pcIsOn();
            }
        } else if (topic.equals("home/button/sleep")) {
            if (message.toString().equals("start")) {
                SleepHandler.setSleeping(true);
                if(sleeping) {
                    LightsHandler.resetLights(1000);
                }
            } else if (message.toString().equals("stop")) {
                SleepHandler.setSleeping(false);
                if (SleepHandler.inside) {
                    LightsHandler.fadeLedStrip(200, 100, 0, 5000);
                }
            }
        } else if (topic.equals("home/led/status")) {
            Calendar cal = Calendar.getInstance();
            LightsHandler.lastEspStatusUpdate = cal.getTimeInMillis();
            LightsHandler.espLed = true;
            String[] split = message.toString().split(",");
            int red = Integer.parseInt(split[0]);
            int green = Integer.parseInt(split[1]);
            int blue = Integer.parseInt(split[2]);
            LightsHandler.ledRed = red;
            LightsHandler.ledGreen = green;
            LightsHandler.ledBlue = blue;
            if (red == 0 && green == 0 && blue == 0) {
                LightsHandler.ledStrip = false;
            } else {
                LightsHandler.ledStrip = true;
            }

        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void connectComplete(boolean reconnect, java.lang.String serverURI) {
        if (reconnect) {
            try {
                client.subscribe(topics);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
