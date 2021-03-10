package com.trackerforce.splitmate.dao.wrapper;

import android.content.ContentValues;
import android.database.Cursor;

import com.github.petruki.dblite.wrapper.DbLiteWrapper;
import com.github.petruki.dblite.wrapper.EntityWrapper;
import com.trackerforce.splitmate.model.Item;
import com.trackerforce.splitmate.model.ItemValue;
import com.trackerforce.splitmate.model.Poll;

@DbLiteWrapper(entityName = "ITEM", columns = {
        "id", "name", "details", "poll_name", "poll", "assigned_to", "created_by", "eventId" })
public class ItemWrapper implements EntityWrapper<Item> {

    @Override
    public Item unWrap(Cursor cursor) {
        Item item = new Item();
        item.setId(getString(cursor, "id"));
        item.setName(getString(cursor, "name"));
        item.setPoll_name(getString(cursor, "poll_name"));
        item.setAssigned_to(getString(cursor, "assigned_to"));
        item.setCreated_by(getString(cursor, "created_by"));
        item.setEventId(getString(cursor, "eventId"));
        item.setDetails(getJson(cursor, "details", ItemValue[].class));
        item.setPoll(getJson(cursor, "poll", Poll[].class));
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
        values.put("details", toJson(entity.getDetails()));
        values.put("poll", toJson(entity.getPoll()));
        return values;
    }
}
