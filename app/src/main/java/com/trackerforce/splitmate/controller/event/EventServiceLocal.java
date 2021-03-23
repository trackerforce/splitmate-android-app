package com.trackerforce.splitmate.controller.event;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.dao.EventRepository;
import com.trackerforce.splitmate.dao.ItemRepository;
import com.trackerforce.splitmate.dao.UserRepository;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.model.Item;
import com.trackerforce.splitmate.model.User;

import java.util.List;

public class EventServiceLocal {

    private static final String TAG = EventServiceLocal.class.getSimpleName();

    private final EventRepository eventRepository;
    private final Context context;

    public EventServiceLocal(Context context) {
        eventRepository = new EventRepository(context);
        this.context = context;
    }

    public void myEventsCurrent(ServiceCallback<Event[]> callback) {
        myEvents("current", callback);
    }

    public void myEventsArchived(ServiceCallback<Event[]> callback) {
        myEvents("archived", callback);
    }

    public void myEventsInvited(ServiceCallback<Event[]> callback) {
        myEvents("invited", callback);
    }

    private void myEvents(String category, ServiceCallback<Event[]> callback) {
        try {
            List<Event> events = eventRepository.findByCategory(category);
            callback.onSuccessResponse(context, events.toArray(new Event[0]));
        } catch (Exception e) {
            callback.onErrorResponse(context, e.getMessage());
            Log.d(TAG, e.getMessage());
        }
    }

    public void getEventById(String id, ServiceCallback<Event> callback) {
        try {
            callback.onSuccessResponse(context, eventRepository.findById(id, true));
        } catch (Exception e) {
            callback.onErrorResponse(context, e.getMessage());
        }
    }

    public void getEvenItemById(String itemId, ServiceCallback<Item> callback) {
        try {
            callback.onSuccessResponse(context, eventRepository.getItemRepository().findById(itemId));
        } catch (Exception e) {
            callback.onErrorResponse(context, e.getMessage());
            Log.d(TAG, e.getMessage());
        }
    }

    public void unpickItem(Item item) {
        final ItemRepository itemRepository = eventRepository.getItemRepository();
        try {
            item.setV_assigned_to(null);
            item.setAssigned_to(null);
            itemRepository.update(item.getId(), item);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void pickItem(Item item, String assigneeId) {
        final ItemRepository itemRepository = eventRepository.getItemRepository();
        final UserRepository userRepository = eventRepository.getUserRepository();

        try {
            final User member = userRepository.findById(assigneeId);
            item.setV_assigned_to(member);
            item.setAssigned_to(assigneeId);
            itemRepository.update(item.getId(), item);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void deleteItem(String itemId) {
        final ItemRepository itemRepository = eventRepository.getItemRepository();
        try {
            itemRepository.deleteById(itemId);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void removeMember(String eventId, String memberId) {
        final UserRepository userRepository = eventRepository.getUserRepository();

        try {
            userRepository.deleteByEventIdUserId(eventId, memberId);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void syncMyEvents(String category, Event[] events) throws Exception {
        List<Event> localEvents = eventRepository.findAll();

        //synchronize existing events
        for (Event event : events) {
            syncEvent(category, event);
            localEvents.removeIf(localEvent -> localEvent.getId().equals(event.getId()));
        }

        //synchronize deleted events
        for (Event event : localEvents) {
            if (event.getCategory() != null && event.getCategory().equals(category))
                removeEvent(event.getId());
        }
    }

    /**
     * Synchronize Event with local storage.
     * When visualizing Event, category is null. In that case, to avoid the app to override category
     * with a null value, it transfers the category from the local data to the argument event.
     *
     * Updates are made only when the stored event has not been loaded or has a different version.
     */
    public void syncEvent(@Nullable String category, Event event) throws Exception {
        Event localEvent = eventRepository.findById(event.getId(), false);

        if (category != null) {
            event.setCategory(category);
        } else if (localEvent != null) {
            event.setCategory(localEvent.getCategory());
        }

        if (localEvent == null) {
            eventRepository.save(event);
        } else if (!event.equals(localEvent)) {
            eventRepository.update(event.getId(), event);
        }
    }

    public void syncEvent(Event event) throws Exception {
        syncEvent(null, event);
    }

    public void syncItem(Item item, String eventId) throws Exception {
        item.setEventId(eventId);
        eventRepository.getItemRepository().update(item.getId(), item);
    }

    public void removeEvent(String id) {
        try {
            eventRepository.deleteById(id);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void removeItem(String itemId) {
        try {
            eventRepository.getItemRepository().deleteById(itemId);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

}
