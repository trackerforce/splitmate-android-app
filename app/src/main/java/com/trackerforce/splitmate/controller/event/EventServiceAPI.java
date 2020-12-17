package com.trackerforce.splitmate.controller.event;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.EmailsDTO;
import com.trackerforce.splitmate.model.ErrorResponse;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.model.EventDTO;
import com.trackerforce.splitmate.model.Item;
import com.trackerforce.splitmate.rest.SplitmateAPI;
import com.trackerforce.splitmate.utils.AppUtils;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventServiceAPI {

    private static final String TAG = EventServiceAPI.class.getSimpleName();

    private final Context context;

    public EventServiceAPI(Context context) {
        this.context = context;
    }

    public void create(Event event, ServiceCallback<Event> callback,
                       EventServiceLocal eventServiceLocal) {
        SplitmateAPI.event().create(event).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.code() == 201) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    syncEvent(eventServiceLocal, response.body(), callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                callback.onError(AppUtils.getString(context, R.string.msgFailedCreateEvent));
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void edit(Event event, ServiceCallback<Event> callback,
                     EventServiceLocal eventServiceLocal) {
        final EventDTO eventDTO = new EventDTO(event);
        SplitmateAPI.event().edit(event.getId(), eventDTO).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    syncEvent(eventServiceLocal, response.body(), callback);
                } else if (response.code() == 404) {
                    assert response.errorBody() != null;
                    notFoundEvent(event.getId(), eventServiceLocal, response.errorBody(), callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                callback.onError(AppUtils.getString(context, R.string.msgFailedEditingEvent));
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void addItem(String id, Item item, ServiceCallback<Event> callback,
                        EventServiceLocal eventServiceLocal) {
        SplitmateAPI.event().addItem(id, item).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    syncEvent(eventServiceLocal, response.body(), callback);
                } else if (response.code() == 404) {
                    assert response.errorBody() != null;
                    notFoundEvent(id, eventServiceLocal, response.errorBody(), callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                callback.onError(AppUtils.getString(context, R.string.msgFailedAddingItem));
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void editItem(String id, Item item, ServiceCallback<Event> callback,
                        EventServiceLocal eventServiceLocal) {
        SplitmateAPI.event().editItem(id, item).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    syncEvent(eventServiceLocal, response.body(), callback);
                } else if (response.code() == 404) {
                    assert response.errorBody() != null;
                    notFoundEvent(id, eventServiceLocal, response.errorBody(), callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                callback.onError(AppUtils.getString(context, R.string.msgFailedEditingItem));
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void deleteItem(String id, Item item, ServiceCallback<Event> callback,
                           EventServiceLocal eventServiceLocal) {
        SplitmateAPI.event().deleteItem(id, item).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    eventServiceLocal.removeItem(item.getId());
                } else if (response.code() == 404) {
                    assert response.errorBody() != null;
                    notFoundEvent(id, eventServiceLocal, response.errorBody(), callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                callback.onError(AppUtils.getString(context, R.string.msgFailedDeletingItem));
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void pickupItem(String id, Item item, ServiceCallback<Event> callback,
                           EventServiceLocal eventServiceLocal) {
        SplitmateAPI.event().pickupItem(id, item).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    syncEvent(eventServiceLocal, response.body(), callback);
                } else if (response.code() == 404) {
                    assert response.errorBody() != null;
                    notFoundEvent(id, eventServiceLocal, response.errorBody(), callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                callback.onError(AppUtils.getString(context, R.string.msgFailedPickingItem));
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void unpickItem(String id, Item item, ServiceCallback<Event> callback,
                           EventServiceLocal eventServiceLocal) {
        SplitmateAPI.event().unpickItem(id, item).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    syncEvent(eventServiceLocal, response.body(), callback);
                } else if (response.code() == 404) {
                    assert response.errorBody() != null;
                    notFoundEvent(id, eventServiceLocal, response.errorBody(), callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                callback.onError(AppUtils.getString(context, R.string.msgFailedUnpickingItem));
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void delete(String id, ServiceCallback<String> callback,
                       EventServiceLocal eventServiceLocal) {
       SplitmateAPI.event().delete(id).enqueue(new Callback<Map<String, String>>() {
           @Override
           public void onResponse(@NonNull Call<Map<String, String>> call,
                                  @NonNull Response<Map<String, String>> response) {
               if (response.code() == 200) {
                   assert response.body() != null;
                   callback.onSuccess(response.body().get("message"));
                   eventServiceLocal.removeEvent(id);
               } else if (response.code() == 404) {
                   assert response.errorBody() != null;
                   notFoundEvent(id, eventServiceLocal, response.errorBody(), callback);
               } else {
                   callback.errorHandler(context, response);
               }
           }

           @Override
           public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
               callback.onError(AppUtils.getString(context, R.string.msgFailedDeletingEvent));
               Log.d(TAG, t.getMessage());
           }
       });
    }

    public void removeMember(String userId, String eventId, ServiceCallback<Event> callback,
                             EventServiceLocal eventServiceLocal) {
        SplitmateAPI.event().removeMember(userId, eventId).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    syncEvent(eventServiceLocal, response.body(), callback);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                callback.onError(AppUtils.getString(context, R.string.msgFailedRemoveMember));
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void inviteAll(String eventId, EmailsDTO emails,
                          ServiceCallback<Map<String, String>> callback, EventServiceLocal eventServiceLocal) {
        SplitmateAPI.event().inviteAll(eventId, emails).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, String>> call,
                                   @NonNull Response<Map<String, String>> response) {
                if (response.code() == 404) {
                    assert response.errorBody() != null;
                    notFoundEvent(eventId, eventServiceLocal, response.errorBody(), callback);
                } else {
                    callback.responseHandler(context,200, response, callback);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                callback.onError(AppUtils.getString(context, R.string.msgFailedInviteUser));
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void myEventsCurrent(ServiceCallback<Event[]> callback,
                                EventServiceLocal eventServiceLocal) {
        myEvents("current", callback, eventServiceLocal);
    }

    public void myEventsArchived(ServiceCallback<Event[]> callback,
                                 EventServiceLocal eventServiceLocal) {
        myEvents("archived", callback, eventServiceLocal);
    }

    public void myEventsInvited(ServiceCallback<Event[]> callback,
                                EventServiceLocal eventServiceLocal) {
        myEvents("invited", callback, eventServiceLocal);
    }

    private void myEvents(String category, ServiceCallback<Event[]> callback,
                          EventServiceLocal eventServiceLocal) {
        SplitmateAPI.event().myEvents(category).enqueue(new Callback<Event[]>() {
            @Override
            public void onResponse(@NonNull Call<Event[]> call, @NonNull Response<Event[]> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    syncMyEvents(eventServiceLocal, category, response.body(), callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event[]> call, @NonNull Throwable t) {
                callback.onError(AppUtils.getString(context, R.string.msgFailedReturnEvents));
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void getEventById(String id, ServiceCallback<Event> callback,
                             EventServiceLocal eventServiceLocal) {
        SplitmateAPI.event().getEventById(id).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    syncEvent(eventServiceLocal, response.body(), callback);
                } else if (response.code() == 404) {
                    assert response.errorBody() != null;
                    notFoundEvent(id, eventServiceLocal, response.errorBody(), callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                callback.onError(AppUtils.getString(context, R.string.msgFailedReturnEvents));
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void getEventItemById(String eventId, String itemId, ServiceCallback<Item> callback,
                                 EventServiceLocal eventServiceLocal) {
        SplitmateAPI.event().getEventItemById(eventId, itemId).enqueue(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    syncItem(eventServiceLocal, response.body(), eventId, callback);
                } else if (response.code() == 404) {
                    assert response.errorBody() != null;
                    notFoundEvent(eventId, eventServiceLocal, response.errorBody(), callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                callback.onError(AppUtils.getString(context, R.string.msgFailedReturnEvents));
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void transfer(String eventId, String userId, ServiceCallback<Event> callback,
                         EventServiceLocal eventServiceLocal) {
        SplitmateAPI.event().transfer(eventId, userId).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    syncEvent(eventServiceLocal, response.body(), callback);
                } else if (response.code() == 404) {
                    assert response.errorBody() != null;
                    notFoundEvent(eventId, eventServiceLocal, response.errorBody(), callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                callback.onError(AppUtils.getString(context, R.string.msgFailedTransferEvent));
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void votePoll(String eventId, String itemId, String pollItemId,
                         ServiceCallback<Item> callback, EventServiceLocal eventServiceLocal) {
        SplitmateAPI.event().pollVote(eventId, itemId, pollItemId).enqueue(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    callback.onSuccess(response.body());
                    syncItem(eventServiceLocal, response.body(), eventId, callback);
                } else if (response.code() == 404) {
                    assert response.errorBody() != null;
                    notFoundEvent(eventId, eventServiceLocal, response.errorBody(), callback);
                } else {
                    callback.errorHandler(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                callback.onError(AppUtils.getString(context, R.string.msgFailedUnpickingItem));
                Log.d(TAG, t.getMessage());
            }
        });
    }

    /**
     * Synchronizes event data with the local storage
     */
    private void syncEvent(EventServiceLocal eventServiceLocal, Event event,
                           ServiceCallback<Event> callback) {
        try {
            eventServiceLocal.syncEvent(event);
        } catch (Exception e) {
            callback.onError(e.getMessage());
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * Synchronizes events data with the local storage
     */
    private void syncMyEvents(EventServiceLocal eventServiceLocal, String category,
                              Event[] events, ServiceCallback<Event[]> callback) {
        try {
            eventServiceLocal.syncMyEvents(category, events);
        } catch (Exception e) {
            callback.onError(e.getMessage());
            Log.d(TAG, e.getMessage());
        }
    }

    private void syncItem(EventServiceLocal eventServiceLocal, Item item, String eventId,
                          ServiceCallback<Item> callback) {
        try {
            eventServiceLocal.syncItem(item, eventId);
        } catch (Exception e) {
            callback.onError(e.getMessage());
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * Event does not exist anymore and should redirect user to the main view
     */
    private void notFoundEvent(String eventId, EventServiceLocal eventServiceLocal,
                               ResponseBody errorBody, ServiceCallback<?> callback) {
        try {
            final String json = errorBody.string();
            Gson gson = new Gson();
            ErrorResponse error = gson.fromJson(json, ErrorResponse.class);

            if ("event".equals(error.getDocument()))
                eventServiceLocal.removeEvent(eventId);

            callback.onError("404", error);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

}
