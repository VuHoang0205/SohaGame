package com.s.sdk.tracking;

import android.util.Log;

import com.s.sdk.base.Constants;
import com.s.sdk.base.SContext;
import com.s.sdk.init.model.InitModel;
import com.s.sdk.init.model.ResponseInit;
import com.s.sdk.login.model.SLoginResult;
import com.s.sdk.login.model.UserGameInfo;
import com.s.sdk.utils.Alog;
import com.s.sdk.utils.EncryptorEngine;
import com.s.sdk.utils.MQTTUtils;
import com.s.sdk.utils.PrefUtils;
import com.s.sdk.utils.Utils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class MQTTTracker {
    private static MQTTTracker instance;
    private MqttAndroidClient mqttAndroidClient;
    private String sessionIdMqtt;
    //    public static boolean pendingActionInstall = false;
    public static boolean pendingActionOpenApp = true;
//    public static boolean pendingActionOpenNoti = false;

    private Queue<ActionMQTT> queueAction = new LinkedList<>();

    public synchronized static MQTTTracker getInstance() {
        if (instance == null) {
            instance = new MQTTTracker();
        }
        return instance;
    }

    public void initMQTT() {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            return;
        }
        Alog.e("initMQTT()");
        ResponseInit.Data data = PrefUtils.getObject(Constants.PREF_RESPONSE_INIT_DATA, ResponseInit.Data.class);
        if (data == null) return;
        InitModel initModel = PrefUtils.getObject(Constants.PREF_INIT_MODEL, InitModel.class);
        if (initModel == null) return;
        Random rn = new Random();
        int randomNum = rn.nextInt(99999 - 10000 + 1) + 10000;
        sessionIdMqtt = String.format("%s_%s", System.currentTimeMillis(), randomNum);
        Alog.e("sessionIdMqtt: " + sessionIdMqtt);

        String serverUri = "tcp://" + data.getDomain_mqtt() + ":" + data.getPort_mqtt();
        // [mã game]_[app_id]_[device_id]_[time unix]_[random string (5 kí tự)]
        String clientId = Utils.getClientIdMQTT(initModel);
        //Alog.e( "clientIdmqtt: " + clientId);
        final String username = "shg";
        final String password = "WE8Yax5ndKApNmJAQpqQAsB";

        mqttAndroidClient = new MqttAndroidClient(SContext.getApplicationContext(), serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Alog.e("mqtt connectComplete: reconnect: " + reconnect + "//" + queueAction.size());
                ActionMQTT actionMQTT;
                do {
                    actionMQTT = queueAction.poll();
                    if (actionMQTT != null) {
                        sendAgain(actionMQTT.getAction(), actionMQTT.getExt(), actionMQTT.getTime());
                    }
                } while (actionMQTT != null);
            }

            @Override
            public void connectionLost(Throwable cause) {
                Alog.e("mqtt connectionLost");
                //addToHistory("The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                //addToHistory("Incoming message: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();

        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setKeepAliveInterval(15);//15
        mqttConnectOptions.setConnectionTimeout(15);//15
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        try {
            //addToHistory("Connecting to " + serverUri);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
//                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
//                    disconnectedBufferOptions.setBufferEnabled(true);
//                    disconnectedBufferOptions.setBufferSize(100);
//                    disconnectedBufferOptions.setPersistBuffer(false);
//                    disconnectedBufferOptions.setDeleteOldestMessages(false);
//                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(10000);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(true);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);


                    Alog.e("mqtt connect onSuccess");
                    //subscribeToTopic();

//                    //send track install and open app
//                    if (pendingActionInstall) {
//                        Log.e("mytag", "send install mqtt");
//                        send(STracker.ACTION_INSTALL, "");
//                        pendingActionInstall = false;
//                    }
//                    if (pendingActionOpenApp) {
//                        Log.e("mytag", "send open mqtt");
//                        send(STracker.ACTION_OPEN, "");
//                        pendingActionOpenApp = false;
//                    }
//                    if (pendingActionOpenNoti) {
//                        Log.e("mytag", "send open noti mqtt");
//                        send(STracker.ACTION_OPEN_NOTIFI, "");
//                        pendingActionOpenNoti = false;
//                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //addToHistory("Failed to connect to: " + serverUri);
                    Alog.e("mqtt connect onFailure " + exception.getMessage());
                }
            });


        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }


    private synchronized void sendAgain(final String action, String ext, long time) {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            final JSONObject obj = createParamsTracking(action, ext, time);
            String sMessage = EncryptorEngine.encryptDataNoURLEn(obj.toString(), Constants.PUBLIC_KEY_MQTT);
            try {
                MqttMessage message = new MqttMessage();
                message.setPayload(sMessage.getBytes());
                mqttAndroidClient.publish("tracklog", sMessage.getBytes(), 2, true, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        //Alog.e("publish again mqtt onSuccess " + action);
                        if (STracker.ACTION_INSTALL.equals(action)) {
                            PrefUtils.putBoolean(STracker.ACTION_INSTALL, true);
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                        if (exception != null) {
//                            Alog.e("publish again mqtt onFailure " + exception.getMessage());
//                        } else {
//                            Alog.e("publish again mqtt onFailure "/*+asyncActionToken.getException().getMessage()*/);
//                        }
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }


    public synchronized void send(final String actionType, final String ext) {
        final long time = System.currentTimeMillis() / 1000L;
        if (mqttAndroidClient == null) {
            Alog.e("add to queue mqtt " + actionType);
            queueAction.offer(new ActionMQTT(actionType, ext, time));
            return;
        }
        boolean isConnected;
        try {
            isConnected = mqttAndroidClient.isConnected();
        } catch (IllegalArgumentException e) {
            isConnected = false;
        }
        if (!isConnected) {
            Alog.e("add to queue mqtt " + actionType);
            queueAction.offer(new ActionMQTT(actionType, ext, time));
            return;
        }

        final JSONObject obj = createParamsTracking(actionType, ext, time);
        String sMessage = EncryptorEngine.encryptDataNoURLEn(obj.toString(), Constants.PUBLIC_KEY_MQTT);
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(sMessage.getBytes());
            mqttAndroidClient.publish("tracklog", sMessage.getBytes(), 2, true, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Alog.e("publish mqtt onSuccess " + actionType);
                    //Alog.e("publish mqtt onSuccess " + obj.toString());
                    if (actionType.equals(STracker.ACTION_KILL_APP)) {
                        disconnect();
                    } else if (STracker.ACTION_INSTALL.equals(actionType)) {
                        PrefUtils.putBoolean(STracker.ACTION_INSTALL, true);
                    }

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (exception != null) {
                        Alog.e("publish mqtt onFailure " + exception.getMessage());
                    } else {
                        Alog.e("publish mqtt onFailure "/*+asyncActionToken.getException().getMessage()*/);
                    }
                    Alog.e("add to queue mqtt " + actionType);
                    queueAction.offer(new ActionMQTT(actionType, ext, time));
                    if (actionType.equals(STracker.ACTION_KILL_APP)) {
                        disconnect();
                    }
                }
            });
        } catch (MqttException e) {
            Log.e("mytag", "Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }

    }

    JSONObject createParamsTracking(String actionType, String ext, long time) {
        JSONObject obj = new JSONObject();
        ResponseInit.Data dataInit = PrefUtils.getObject(Constants.PREF_RESPONSE_INIT_DATA, ResponseInit.Data.class);
        InitModel initModel = PrefUtils.getObject(Constants.PREF_INIT_MODEL, InitModel.class);
        UserGameInfo userGameInfo = PrefUtils.getObject(Constants.PREF_USER_GAME_INFO, UserGameInfo.class);
        SLoginResult loginResult = PrefUtils.getObject(Constants.PREF_LOGIN_RESULT, SLoginResult.class);
        String englishName = "";
        String device_token = "";
        if (dataInit != null) {
            englishName = dataInit.getE_name();
            device_token = dataInit.getDevice_token();
        }
        String appId = "";
        String gameCode = "";
        if (initModel != null) {
            appId = initModel.getAppId();
            gameCode = initModel.getClientCode();
        }

        String packageName = SContext.getApplicationContext().getPackageName();
        String gameVersion = Utils.getAppVersionName(SContext.getApplicationContext());
        String sdkVersion = Utils.getSDKVersion(SContext.getApplicationContext());
        String roleId = "";
        String roleName = "";
        String roleLevel = "";
        String areaId = "";

        if (userGameInfo != null) {
            roleId = userGameInfo.getRoleId();
            roleName = userGameInfo.getRoleName()/*Base64.encodeToString(userGameInfo.getRoleName().getBytes(), Base64.DEFAULT)*/;
            roleLevel = userGameInfo.getRoleLevel();
            areaId = userGameInfo.getAreaId();
        }


        String userId = "";
        String vietId = "";
        if (loginResult != null) {
            userId = loginResult.getUserId();
            vietId = loginResult.getPuid();
        }

        String sessionId = sessionIdMqtt;

        String deviceId = PrefUtils.getString(Constants.PREF_ADS_ID_GG);
        if (time == 0) {
            time = System.currentTimeMillis() / 1000L;
        }
        String mOSVersion = MQTTUtils.getOSVersion();
        String mLang = MQTTUtils.getDeviceLang();
        String mDeviceName = MQTTUtils.getPhoneName();
        String networkType = MQTTUtils.getNetworkType(SContext.getApplicationContext());
        String ipLan = MQTTUtils.getIPLan(true);
        String mMF = MQTTUtils.getMF();
        String mDM = MQTTUtils.getDM();
        String mDB = MQTTUtils.getDB();
        String mMBN = MQTTUtils.getMBN();
        String mDP = MQTTUtils.getDP();
        String mDV = MQTTUtils.isEmulator();
        String mClientId = PrefUtils.getString(Constants.PREF_CLIENTID_MQTT);

        try {
            obj.put("k", "os,osv,lang,dn,t,nt,ipl,mf,dm," +
                    "db,dbn,dp,rd,lv,clientid,di,ac,en,ai,bdi,gv,sdkv," +
                    "ri,rn,rl,ari,uid,vid,ext,dt,ssi,gc");
            obj.put("v", "Android," + mOSVersion + "," + mLang
                    + "," + mDeviceName + "," + time + ","
                    + networkType + ","
                    + ipLan + "," + mMF + "," + mDM
                    + "," + mDB + "," + mMBN + "," + mDP + ","
                    + mDV + "," + "1.0.1" + "," + mClientId + ","
                    + deviceId + "," + actionType + "," + englishName + ","
                    + appId + "," + packageName + "," + gameVersion + ","
                    + sdkVersion + "," + roleId + "," + roleName + "," + roleLevel
                    + "," + areaId + "," + userId + "," + vietId + "," + ext + ","
                    + device_token + "," + sessionId + "," + gameCode);
            //Log.e("mytag", "data mqtt send: " + obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private void disconnect() {
        try {
            if (mqttAndroidClient != null) {
                if (mqttAndroidClient.isConnected()) {
                    mqttAndroidClient.disconnect();
                }
                mqttAndroidClient = null;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private static class ActionMQTT {
        String action;
        String ext;
        long time;

        public ActionMQTT(String action, String ext, long time) {
            this.action = action;
            this.ext = ext;
            this.time = time;
        }

        public String getAction() {
            return action;
        }

        public String getExt() {
            return ext;
        }

        public long getTime() {
            return time;
        }
    }

}
