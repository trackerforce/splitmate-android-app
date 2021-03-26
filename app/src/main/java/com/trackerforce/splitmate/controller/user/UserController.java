package com.trackerforce.splitmate.controller.user;

import android.content.Context;
import android.os.AsyncTask;

import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.controller.event.EventServiceLocal;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.utils.Config;
import com.trackerforce.splitmate.utils.AppUtils;

public class UserController {

    private final Context context;
    private final UserServiceAPI userServiceAPI;
    private final UserServiceLocal userServiceLocal;

    public UserController(Context context) {
        this.context = context;
        this.userServiceLocal = new UserServiceLocal(context);
        this.userServiceAPI = new UserServiceAPI(context);
    }

    public void checkAPI(ServiceCallback<String> callback) {
        AsyncTask.execute(() -> {
            if (AppUtils.isOnline(context, true)) {
                userServiceAPI.checkAPI(callback);
            } else {
                callback.onSuccessResponse(context, null);
            }
        });
    }

    public void login(String login, String password, ServiceCallback<User> callback) {
        User user = new User();
        user.setUsername(login);
        user.setPassword(password);

        AsyncTask.execute(() -> {
            if (AppUtils.isOnline(context, true)) {
                userServiceAPI.login(user, callback, this.userServiceLocal);
            } else {
                callback.onErrorResponse(context, AppUtils.getString(context, R.string.msgNotConnected));
            }
        });
    }

    public void logout(ServiceCallback<Boolean> callback) {
        AsyncTask.execute(() -> {
            if (AppUtils.isOnline(context, true)) {
                userServiceAPI.logout(callback, this.userServiceLocal);
            } else {
                callback.onErrorResponse(context, AppUtils.getString(context, R.string.msgNotConnected));
            }
        });
    }

    public void signUp(String name, String username, String email,
                       String password, String token, ServiceCallback<User> callback) {
        String error = "";

        if (name == null || name.isEmpty()) error = "Name";
        else if (username == null || username.isEmpty()) error = "Username";
        else if (email == null || email.isEmpty()) error = "Email";
        else if (password == null || password.isEmpty()) error = "Password";

        if (!error.isEmpty()) {
            final String message = AppUtils.getString(context, R.string.msgFieldMustBeValid);
            callback.onError(String.format(message, error));
            return;
        }

        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setToken(token);

        AsyncTask.execute(() -> {
            if (AppUtils.isOnline(context, true)) {
                userServiceAPI.signUp(user, callback, this.userServiceLocal);
            } else {
                callback.onErrorResponse(context, AppUtils.getString(context, R.string.msgNotConnected));
            }
        });
    }

    public void getUser(ServiceCallback<User> callback) {
        AsyncTask.execute(() -> {
            if (AppUtils.isOnline(context, true)) {
                userServiceAPI.getUser(callback);
            } else {
                userServiceLocal.getUser(callback);
            }
        });
    }

    public void find(String username, ServiceCallback<User> callback) {
        AsyncTask.execute(() -> {
            if (AppUtils.isOnline(context, true)) {
                userServiceAPI.find(username, callback);
            } else {
                callback.onErrorResponse(context, AppUtils.getString(context, R.string.msgNotConnected));
            }
        });
    }

    public void joinEvent(String eventId, ServiceCallback<User> callback, boolean force) {
        AsyncTask.execute(() -> {
            if (AppUtils.isOnline(context, force)) {
                userServiceAPI.joinEvent(eventId, callback, this.userServiceLocal);
            } else {
                callback.onErrorResponse(context, AppUtils.getString(context, R.string.msgNotConnected));
            }
        });
    }

    public void dismissEvent(String eventId, ServiceCallback<User> callback, boolean force) {
        AsyncTask.execute(() -> {
            if (AppUtils.isOnline(context, force)) {
                userServiceAPI.dismissEvent(eventId, callback, this.userServiceLocal);
            } else {
                callback.onErrorResponse(context, AppUtils.getString(context, R.string.msgNotConnected));
            }
        });
    }

    public void leaveEvent(String eventId, ServiceCallback<User> callback, boolean force) {
        AsyncTask.execute(() -> {
            if (AppUtils.isOnline(context, force)) {
                userServiceAPI.leaveEvent(eventId, callback, new EventServiceLocal(context));
            } else {
                callback.onErrorResponse(context, AppUtils.getString(context, R.string.msgNotConnected));
            }
        });
    }

    public void addEventToArchive(String eventId, ServiceCallback<User> callback, boolean force) {
        archiveEvent("add", eventId, callback, force);
    }

    public void removeEventArchive(String eventId, ServiceCallback<User> callback, boolean force) {
        archiveEvent("remove", eventId, callback, force);
    }

    public void deleteAccount(ServiceCallback<String> callback, boolean force) {
        AsyncTask.execute(() -> {
            if (AppUtils.isOnline(context, force)) {
                userServiceAPI.deleteAccount(callback, this.userServiceLocal);
            } else {
                callback.onErrorResponse(context, AppUtils.getString(context, R.string.msgNotConnected));
            }
        });
    }

    private void archiveEvent(String action, String eventId, ServiceCallback<User> callback,
                              boolean force) {
        AsyncTask.execute(() -> {
            if (AppUtils.isOnline(context, force)) {
                userServiceAPI.archiveEvent(action, eventId, callback, new UserServiceLocal(context));
            } else {
                callback.onErrorResponse(context, AppUtils.getString(context, R.string.msgNotConnected));
            }
        });
    }

    public void syncToken() throws Exception {
        Config.getInstance().setLoggedUser(userServiceLocal.getLoggedUser());
    }
}
