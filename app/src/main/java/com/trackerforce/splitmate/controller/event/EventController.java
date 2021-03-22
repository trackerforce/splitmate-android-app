package com.trackerforce.splitmate.controller.event;

import android.content.Context;
import android.os.AsyncTask;

import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.EmailsDTO;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.model.Item;
import com.trackerforce.splitmate.utils.AppUtils;

import java.util.Map;

public class EventController {

    private final Context context;
    private final EventServiceAPI eventServiceAPI;
    private final EventServiceLocal eventServiceLocal;

    public EventController(Context context) {
        this.context = context;
        this.eventServiceLocal = new EventServiceLocal(context);
        this.eventServiceAPI = new EventServiceAPI(context);
    }

    public void inviteAll(String eventId, EmailsDTO emails,
                          ServiceCallback<Map<String, String>> callback, boolean force) {
        if (eventId == null || eventId.isEmpty()) {
            callback.onError(AppUtils.getString(context, R.string.msgEventIdIsEmpty));
        } else if (emails.getEmails() == null || emails.getEmails().length == 0) {
            callback.onError(AppUtils.getString(context, R.string.msgEmailIsEmpty));
        } else {
            if (AppUtils.isOnline(context, force)) {
                AsyncTask.execute(() ->
                    eventServiceAPI.inviteAll(eventId, emails, callback, eventServiceLocal));
            } else {
                callback.onError(AppUtils.getString(context, R.string.msgNotConnected));
            }
        }
    }

    public void myEventsCurrent(ServiceCallback<Event[]> callback, boolean force) {
        AsyncTask.execute(() -> {
            if (AppUtils.isOnline(context, force)) {
                eventServiceAPI.myEventsCurrent(callback, eventServiceLocal);
            } else {
                eventServiceLocal.myEventsCurrent(callback);
            }
        });
    }

    public void myEventsCurrentLocal(ServiceCallback<Event[]> callback) {
        eventServiceLocal.myEventsCurrent(callback);
    }

    public void myEventsArchived(ServiceCallback<Event[]> callback, boolean force) {
        AsyncTask.execute(() -> {
            if (AppUtils.isOnline(context, force)) {
                eventServiceAPI.myEventsArchived(callback, eventServiceLocal);
            } else {
                eventServiceLocal.myEventsArchived(callback);
            }
        });
    }

    public void myEventsArchivedLocal(ServiceCallback<Event[]> callback) {
        eventServiceLocal.myEventsArchived(callback);
    }

    public void myEventsInvited(ServiceCallback<Event[]> callback, boolean force) {
        AsyncTask.execute(() -> {
            if (AppUtils.isOnline(context, force)) {
                eventServiceAPI.myEventsInvited(callback, eventServiceLocal);
            } else {
                eventServiceLocal.myEventsInvited(callback);
            }
        });
    }

    public void getEventById(String id, ServiceCallback<Event> callback, boolean force) {
        if (id == null || id.isEmpty()) {
            callback.onError(AppUtils.getString(context, R.string.msgEventIdIsEmpty));
        } else {
            AsyncTask.execute(() -> {
                if (AppUtils.isOnline(context, force)) {
                    eventServiceAPI.getEventById(id, callback, eventServiceLocal);
                } else {
                    eventServiceLocal.getEventById(id, callback);
                }
            });
        }
    }

    public void getEventItemById(String eventId, String itemId, ServiceCallback<Item> callback,
                                 boolean force) {
        if (eventId == null || eventId.isEmpty()) {
            callback.onError(AppUtils.getString(context, R.string.msgEventIdIsEmpty));
        } else if (itemId == null || itemId.isEmpty()) {
            callback.onError(AppUtils.getString(context, R.string.msgItemIdIsEmpty));
        } else {
            AsyncTask.execute(() -> {
                if (AppUtils.isOnline(context, force)) {
                    eventServiceAPI.getEventItemById(eventId, itemId, callback, eventServiceLocal);
                } else {
                    eventServiceLocal.getEvenItemById(itemId, callback);
                }
            });
        }
    }

    public void create(Event event, ServiceCallback<Event> callback, boolean force) {
        if (AppUtils.isOnline(context, force)) {
            AsyncTask.execute(() -> eventServiceAPI.create(event, callback, eventServiceLocal));
        } else {
            callback.onError(AppUtils.getString(context, R.string.msgNotConnected));
        }
    }

    public void edit(Event event, ServiceCallback<Event> callback, boolean force) {
        if (event == null || event.getId().isEmpty()) {
            callback.onError(AppUtils.getString(context, R.string.msgEventMustExist));
        } else {
            if (AppUtils.isOnline(context, force)) {
                AsyncTask.execute(() -> eventServiceAPI.edit(event, callback, eventServiceLocal));
            } else {
                callback.onError(AppUtils.getString(context, R.string.msgNotConnected));
            }
        }
    }

    public void addItem(String eventId, Item item, ServiceCallback<Event> callback, boolean force) {
        if (eventId == null || eventId.isEmpty()) {
            callback.onError(AppUtils.getString(context, R.string.msgEventIdIsEmpty));
        } else {
            if (AppUtils.isOnline(context, force)) {
                AsyncTask.execute(() ->
                        eventServiceAPI.addItem(eventId, item, callback, eventServiceLocal));
            } else {
                callback.onError(AppUtils.getString(context, R.string.msgNotConnected));
            }
        }
    }

    public void editItem(String eventId, Item item, ServiceCallback<Event> callback, boolean force) {
        if (eventId == null || eventId.isEmpty()) {
            callback.onError(AppUtils.getString(context, R.string.msgEventIdIsEmpty));
        } else if (item == null) {
            callback.onError(AppUtils.getString(context, R.string.msgItemIdIsEmpty));
        } else {
            if (AppUtils.isOnline(context, force)) {
                AsyncTask.execute(() ->
                        eventServiceAPI.editItem(eventId, item, callback, eventServiceLocal));
            } else {
                callback.onError(AppUtils.getString(context, R.string.msgNotConnected));
            }
        }
    }

    public void deleteItem(String eventId, Item item, ServiceCallback<Event> callback, boolean force) {
        if (eventId == null || eventId.isEmpty()) {
            callback.onError(AppUtils.getString(context, R.string.msgEventIdIsEmpty));
        } else if (item == null) {
            callback.onError(AppUtils.getString(context, R.string.msgItemIdIsEmpty));
        } else {
            if (AppUtils.isOnline(context, force)) {
                AsyncTask.execute(() ->
                        eventServiceAPI.deleteItem(eventId, item, callback, eventServiceLocal));
            } else {
                callback.onError(AppUtils.getString(context, R.string.msgNotConnected));
            }
        }
    }

    public void pickupItem(String eventId, Item item, ServiceCallback<Event> callback, boolean force) {
        if (eventId == null || eventId.isEmpty()) {
            callback.onError(AppUtils.getString(context, R.string.msgEventIdIsEmpty));
        } else if (item == null) {
            callback.onError(AppUtils.getString(context, R.string.msgItemIdIsEmpty));
        } else {
            if (AppUtils.isOnline(context, force)) {
                AsyncTask.execute(() ->
                        eventServiceAPI.pickupItem(eventId, item, callback, eventServiceLocal));
            } else {
                callback.onError(AppUtils.getString(context, R.string.msgNotConnected));
            }
        }
    }

    public void unpickItem(String eventId, Item item, ServiceCallback<Event> callback, boolean force) {
        if (eventId == null || eventId.isEmpty()) {
            callback.onError(AppUtils.getString(context, R.string.msgEventIdIsEmpty));
        } else if (item == null) {
            callback.onError(AppUtils.getString(context, R.string.msgItemIdIsEmpty));
        } else {
            if (AppUtils.isOnline(context, force)) {
                AsyncTask.execute(() ->
                        eventServiceAPI.unpickItem(eventId, item, callback, eventServiceLocal));
            } else {
                callback.onError(AppUtils.getString(context, R.string.msgNotConnected));
            }
        }
    }

    public void unpickItemLocal(Item item) {
        AsyncTask.execute(() -> eventServiceLocal.unpickItem(item));
    }

    public void pickItemLocal(Item item, String assigneeId) {
        AsyncTask.execute(() -> eventServiceLocal.pickItem(item, assigneeId));
    }

    public void deleteItemLocal(String itemId) {
        AsyncTask.execute(() -> eventServiceLocal.deleteItem(itemId));
    }

    public void delete(String id, ServiceCallback<String> callback, boolean force) {
        if (AppUtils.isOnline(context, force)) {
            AsyncTask.execute(() -> eventServiceAPI.delete(id, callback, eventServiceLocal));
        } else {
            callback.onError(AppUtils.getString(context, R.string.msgNotConnected));
        }
    }

    public void deleteLocal(String id) {
        eventServiceLocal.removeEvent(id);
    }

    public void removeMember(String userId, String eventId,
                             ServiceCallback<Event> callback, boolean force) {
        if (AppUtils.isOnline(context, force)) {
            AsyncTask.execute(() ->
                    eventServiceAPI.removeMember(userId, eventId, callback, eventServiceLocal));
        } else {
            callback.onError(AppUtils.getString(context, R.string.msgNotConnected));
        }
    }

    public void removeMemberLocal(String eventId, String memberId) {
        AsyncTask.execute(() -> eventServiceLocal.removeMember(eventId, memberId));
    }

    public void transfer(String eventId, String userId,
                         ServiceCallback<Event> callback, boolean force) {
        if (AppUtils.isOnline(context, force)) {
            AsyncTask.execute(() ->
                    eventServiceAPI.transfer(eventId, userId, callback, eventServiceLocal));
        } else {
            callback.onError(AppUtils.getString(context, R.string.msgNotConnected));
        }
    }

    public void votePoll(String eventId, String itemId, String pollItemId,
                         ServiceCallback<Item> callback, boolean force) {
        if (AppUtils.isOnline(context, force)) {
            AsyncTask.execute(() ->
                    eventServiceAPI.votePoll(eventId, itemId, pollItemId, callback, eventServiceLocal));
        } else {
            callback.onError(AppUtils.getString(context, R.string.msgNotConnected));
        }
    }

    public void getLocalEventById(String eventId, ServiceCallback<Event> callback) {
        AsyncTask.execute(() -> eventServiceLocal.getEventById(eventId, callback));
    }
}
