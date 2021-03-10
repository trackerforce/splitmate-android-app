package com.trackerforce.splitmate.dao.wrapper;

import android.content.ContentValues;
import android.database.Cursor;

import com.github.petruki.dblite.wrapper.DbLiteWrapper;
import com.github.petruki.dblite.wrapper.EntityWrapper;
import com.trackerforce.splitmate.model.Event;

import java.util.Date;

@DbLiteWrapper(entityName = "EVENT", columns = {
        "id", "name", "description", "location", "organizer", "version", "date", "category" })
public class EventWrapper implements EntityWrapper<Event> {

    @Override
    public Event unWrap(Cursor cursor) {
        Event event = new Event();
        event.setId(getString(cursor, "id"));
        event.setName(getString(cursor, "name"));
        event.setDescription(getString(cursor, "description"));
        event.setLocation(getString(cursor, "location"));
        event.setOrganizer(getString(cursor, "organizer"));
        event.setVersion(getString(cursor, "version"));
        event.setDate(new Date(getLong(cursor, "date")));
        event.setCategory(getString(cursor, "category"));
        return event;
    }

    @Override
    public ContentValues wrap(Event entity) {
        ContentValues values = new ContentValues();
        values.put("id", entity.getId());
        values.put("name", entity.getName());
        values.put("description", entity.getDescription());
        values.put("location", entity.getLocation());
        values.put("organizer", entity.getOrganizer());
        values.put("version", entity.getVersion());
        values.put("date", entity.getDate().getTime());
        values.put("category", entity.getCategory());
        return values;
    }
}
