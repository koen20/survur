package com.koenhabets.survur.server;

import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;

import static org.eclipse.paho.client.mqttv3.MqttClient.generateClientId;

public class Mqtt implements MqttCallbackExtended {
    static MqttClient client;
    static String location;
    private String[] topics = {"owntracks/koen/lux/event", "home/motion", "home/status/pc"};

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

    static void publishMessage(String topic, String content){
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(0);
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        Log.d(topic + " " + message.toString());
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
        } else if (topic.equals("home/status/pc")){
            if (message.toString().equals("online")) {
                WakeOnLanHandler.pcIsOn();
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
                this.client.subscribe(topics);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
