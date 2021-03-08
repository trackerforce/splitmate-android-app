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
        event.setId(cursor.getString(cursor.getColumnIndex("id")));
        event.setName(cursor.getString(cursor.getColumnIndex("name")));
        event.setDescription(cursor.getString(cursor.getColumnIndex("description")));
        event.setLocation(cursor.getString(cursor.getColumnIndex("location")));
        event.setOrganizer(cursor.getString(cursor.getColumnIndex("organizer")));
        event.setVersion(cursor.getString(cursor.getColumnIndex("version")));
        event.setDate(new Date(cursor.getLong(cursor.getColumnIndex("date"))));
        event.setCategory(cursor.getString(cursor.getColumnIndex("category")));
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
