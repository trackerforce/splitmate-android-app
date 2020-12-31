package com.trackerforce.splitmate.pusher;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.trackerforce.splitmate.model.pusher.PusherData;
import com.trackerforce.splitmate.utils.Config;
import com.trackerforce.splitmate.utils.SplitConstants;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;

public class PusherClient extends Application {

    private static final String TAG = PusherClient.class.getSimpleName();
    private static PusherClient instance;
    private final List<String> events;
    private Socket socket;

    private PusherClient() {
        events = new ArrayList<>();
    }

    public static PusherClient getInstance() {
        if (instance == null)
            instance = new PusherClient();
        return instance;
    }

    private void initSocket(String channel) {
        try {
            IO.Options opts = new IO.Options();
            opts.transports = new String[]{WebSocket.NAME};
            opts.query = String.format("auth=%s&channel=%s", Config.getInstance().getToken(), channel);
            socket = IO.socket(Config.getInstance().API_URL, opts);
        } catch (URISyntaxException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void connect(Context context, String channel) {
        boolean autoSync = Config.getInstance().getSettings(
                context, SplitConstants.TOGGLE_AUTO_SYNC, true);

        if (autoSync) {
            if (socket == null || !socket.connected()) {
                initSocket(channel);
                socket.connect();
            }
        }
    }

    public void disconnect() {
        socket.disconnect();
    }

    public void subscribe(String event, Emitter.Listener listener) {
        socket.on(event, listener);
        events.add(event);
    }

    public void unsubscribe(String event) {
        socket.off(event);
    }

    public void unsubscribeAll() {
        for (String event : events)
            socket.off(event);
        events.clear();
    }

    public void notifyEvent(PusherData pusherData) {
        if (socket.connected()) {
            Gson gson = new Gson();
            socket.emit(PusherEvents.NOTIFY_EVENT.toString(), gson.toJson(pusherData));
        }
    }
}
