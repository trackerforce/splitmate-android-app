package com.trackerforce.splitmate.dao.wrapper;

import android.content.ContentValues;
import android.database.Cursor;

import com.github.petruki.dblite.wrapper.DbLiteWrapper;
import com.github.petruki.dblite.wrapper.EntityWrapper;
import com.google.gson.Gson;
import com.trackerforce.splitmate.model.Item;
import com.trackerforce.splitmate.model.ItemValue;
import com.trackerforce.splitmate.model.Poll;

@DbLiteWrapper(entityName = "ITEM", columns = {
        "id", "name", "details", "poll_name", "poll", "assigned_to", "created_by", "eventId" })
public class ItemWrapper implements EntityWrapper<Item> {

    @Override
    public Item unWrap(Cursor cursor) {
        Item item = new Item();
        item.setId(cursor.getString(cursor.getColumnIndex("id")));
        item.setName(cursor.getString(cursor.getColumnIndex("name")));
        item.setPoll_name(cursor.getString(cursor.getColumnIndex("poll_name")));
        item.setAssigned_to(cursor.getString(cursor.getColumnIndex("assigned_to")));
        item.setCreated_by(cursor.getString(cursor.getColumnIndex("created_by")));
        item.setEventId(cursor.getString(cursor.getColumnIndex("eventId")));

        Gson gson = new Gson();
        item.setDetails(gson.fromJson(cursor.getString(cursor.getColumnIndex("details")), ItemValue[].class));
        item.setPoll(gson.fromJson(cursor.getString(cursor.getColumnIndex("poll")), Poll[].class));
        return item;
    }

    @Override
    public ContentValues wrap(Item entity) {
        ContentValues values = new ContentValues();
        values.put("id", entity.getId());
        values.put("name", entity.getName());
        values.put("poll_name", entity.getPoll_name());
        values.put("assigned_to", entity.getAssigned_to());
        values.put("created_by", entity.getCreated_by());
        values.put("eventId", entity.getEventId());

        Gson gson = new Gson();
        values.put("details", gson.toJson(entity.getDetails()));
        values.put("poll", gson.toJson(entity.getPoll()));
        return values;
    }
}
