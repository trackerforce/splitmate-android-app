package com.trackerforce.splitmate.pusher;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.trackerforce.splitmate.model.pusher.PusherData;
import com.trackerforce.splitmate.utils.Config;
import com.trackerforce.splitmate.utils.SplitConstants;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;

public class PusherClient extends Application {

    private static final String TAG = PusherClient.class.getSimpleName();
    private static PusherClient instance;
    private Socket socket;

    private PusherClient() { }

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
                context, SplitConstants.TOGGLE_AUTO_SYNC, true, Boolean.class);

        if (autoSync) {
            if (socket == null || !socket.connected()) {
                initSocket(channel);
                socket.connect();
            }
        }
    }

    public void disconnect() {
        if (socket.connected())
            socket.disconnect();
    }

    public void subscribe(String event, Emitter.Listener listener) {
        if (socket.connected())
            socket.on(event, listener);
    }

    public void unsubscribe(String event) {
        socket.off(event);
    }

    public void notifyEvent(PusherData pusherData) {
        if (socket.connected()) {
            Gson gson = new Gson();
            socket.emit(PusherEvents.NOTIFY_EVENT.toString(), gson.toJson(pusherData));
        }
    }
}
