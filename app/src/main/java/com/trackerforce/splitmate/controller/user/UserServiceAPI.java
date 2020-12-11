package com.trackerforce.splitmate.controller.user;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.controller.event.EventServiceLocal;
import com.trackerforce.splitmate.model.Login;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.rest.SplitmateAPI;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserServiceAPI {

    private static final String TAG = UserServiceAPI.class.getSimpleName();

    private final Context context;

    public UserServiceAPI(Context context) {
        this.context = context;
    }

    public void checkAPI(ServiceCallback<String> callback) {
        SplitmateAPI.api().check().enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, String>> call,
                                   @NonNull Response<Map<String, String>> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body().get("message"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                callback.onError("API is Offline");
            }
        });
    }

    public void login(User user, ServiceCallback<User> callback,
                      UserServiceLocal userServiceLocal) {
        SplitmateAPI.user().login(user).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(@NonNull Call<Login> call, @NonNull Response<Login> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body().getUser());
                    userServiceLocal.cleanLocalStorage();
                    syncJwt(userServiceLocal, response.body(), callback);
                } else {
                    callback.onError("Invalid user/password");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Login> call, @NonNull Throwable t) {
                callback.onError("Failed to login");
            }
        });
    }

    public void logout(ServiceCallback<Boolean> callback, UserServiceLocal userServiceLocal) {
        SplitmateAPI.user().logout().enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, String>> call,
                                   @NonNull Response<Map<String, String>> response) {
                if (response.code() == 200) {
                    callback.onSuccess(true);
                    userServiceLocal.cleanLocalStorage();
                    syncJwt(userServiceLocal, null, callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                callback.onError("Failed to logout");
            }
        });
    }

    public void signUp(User user, ServiceCallback<User> callback, UserServiceLocal userServiceLocal) {
        SplitmateAPI.user().signUp(user).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(@NonNull Call<Login> call, @NonNull Response<Login> response) {
                if (response.code() == 201) {
                    assert response.body() != null;
                    callback.onSuccess(response.body().getUser());
                    userServiceLocal.cleanLocalStorage();
                    syncJwt(userServiceLocal, response.body(), callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Login> call, @NonNull Throwable t) {
                callback.onError("Failed to register user");
            }
        });
    }

    public void getUser(ServiceCallback<User> callback) {
        SplitmateAPI.user().getUser().enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                callback.responseHandler(context, 200, response, callback);
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                callback.onError("Failed to get user details");
            }
        });
    }

    public void find(String username, ServiceCallback<User> callback) {
        SplitmateAPI.user().find(username).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                callback.responseHandler(context, 200, response, callback);
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                callback.onError("Failed to find user");
            }
        });
    }

    public void joinEvent(String eventId, ServiceCallback<User> callback,
                          UserServiceLocal userServiceLocal) {
        SplitmateAPI.user().joinEvent(eventId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    syncUser(userServiceLocal, response.body(), callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                callback.onError("Failed to join event");
            }
        });
    }

    public void dismissEvent(String eventId, ServiceCallback<User> callback,
                             UserServiceLocal userServiceLocal) {
        SplitmateAPI.user().dismissEvent(eventId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    syncUser(userServiceLocal, response.body(), callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                callback.onError("Failed to dismiss event");
            }
        });
    }

    public void leaveEvent(String eventId, ServiceCallback<User> callback,
                           EventServiceLocal eventServiceLocal) {
        SplitmateAPI.user().leaveEvent(eventId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    eventServiceLocal.removeEvent(eventId);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                callback.onError("Failed to dismiss event");
            }
        });
    }

    public void archiveEvent(String action, String eventId, ServiceCallback<User> callback,
                             UserServiceLocal userServiceLocal) {
        SplitmateAPI.user().archiveEvent(action, eventId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    syncUser(userServiceLocal, response.body(), callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                callback.onError("Failed to archive event using: " + action);
            }
        });
    }

    public void deleteAccount(ServiceCallback<String> callback, UserServiceLocal userServiceLocal) {
        SplitmateAPI.user().deleteAccount().enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, String>> call,
                                   @NonNull Response<Map<String, String>> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body().get("message"));
                    userServiceLocal.cleanLocalStorage();
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                callback.onError("Failed to delete account");
            }
        });
    }

    /**
     * Synchronizes user data with the local storage
     */
    private void syncUser(UserServiceLocal userServiceLocal, User user,
                          ServiceCallback<User> callback) {
        try {
            userServiceLocal.syncUser(user);
        } catch (Exception e) {
            callback.onError(e.getMessage());
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * Synchronizes authentication data with local storage
     */
    private void syncJwt(UserServiceLocal userServiceLocal, Login login,
                         ServiceCallback<?> callback) {
        try {
            userServiceLocal.syncJwt(login);
        } catch (Exception e) {
            callback.onError(e.getMessage());
            Log.d(TAG, e.getMessage());
        }
    }

}
