package com.koenhabets.survur.server;

import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;

import static org.eclipse.paho.client.mqttv3.MqttClient.generateClientId;

public class Mqtt implements MqttCallbackExtended {
    static MqttClient client;
    static String location;
    //private String[] topics = {"owntracks/koen/lux/event", "home/motion", "home/status/pc", "home/button/sleep"};
    private String[] topics = {"owntracks/koen/lux/event", "home/#"};

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
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        System.out.println(topic + " " + message.toString());
        if (topic.equals("owntracks/koen/lux/event")) {
            JSONObject jsonObject = new JSONObject(message.toString());
            String event = jsonObject.getString("event");
            String desc = jsonObject.getString("desc");
            location = desc;
            Log.d(event + " " + desc);
            if (desc.equals("Thuis")) {
                if (event.equals("enter")) {
                    ActionHandler.inside = true;
                    Log.d("inside");
                } else {
                    ActionHandler.inside = false;
                }
            }
        } else if (topic.equals("home/motion")) {
            RoomHandler.enterRoom();
        } else if (topic.equals("home/status/pc")) {
            if (message.toString().equals("online")) {
                WakeOnLanHandler.pcIsOn();
            }
        } else if (topic.equals("home/button/sleep")) {
            if (message.toString().equals("start")) {
                System.out.println("start sleeping");
                ActionHandler.sleeping = true;
                LightsHandler.resetLights();
            } else if (message.toString().equals("stop")) {
                ActionHandler.sleeping = false;
                System.out.println("stop sleeping");
                if (ActionHandler.inside) {
                    LightsHandler.setLedStrip(200, 100, 0);
                }
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
