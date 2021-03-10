package com.trackerforce.splitmate.dao.wrapper;

import android.content.ContentValues;
import android.database.Cursor;

import com.github.petruki.dblite.wrapper.DbLiteWrapper;
import com.github.petruki.dblite.wrapper.EntityWrapper;
import com.trackerforce.splitmate.model.Plan;
import com.trackerforce.splitmate.model.User;

@DbLiteWrapper(entityName = "USER", columns = {
        "id", "name", "email", "username", "plan", "eventId" })
public class UserWrapper implements EntityWrapper<User> {

    @Override
    public User unWrap(Cursor cursor) {
        User user = new User();
        user.setId(getString(cursor, "id"));
        user.setName(getString(cursor, "name"));
        user.setEmail(getString(cursor, "email"));
        user.setUsername(getString(cursor, "username"));
        user.setEventId(getString(cursor, "eventId"));
        user.setV_Plan(getJson(cursor, "plan", Plan.class));
        return user;
    }

    @Override
    public ContentValues wrap(User user) {
        ContentValues values = new ContentValues();
        values.put("id", user.getId());
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("username", user.getUsername());
        values.put("eventId", user.getEventId());
        values.put("plan", toJson(user.getV_Plan()));
        return values;
    }

}
