package com.trackerforce.splitmate.dao.wrapper;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.trackerforce.splitmate.model.Plan;
import com.trackerforce.splitmate.model.User;

public class UserWrapper implements EntityWrapper<User> {

    @Override
    public String getTableName() {
        return "USER";
    }

    @Override
    public String getCreateTable() {
        return String.format("CREATE TABLE %s (id, name, email, username, plan, eventId)", getTableName());
    }

    @Override
    public User unWrap(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getString(cursor.getColumnIndex("id")));
        user.setName(cursor.getString(cursor.getColumnIndex("name")));
        user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
        user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
        user.setEventId(cursor.getString(cursor.getColumnIndex("eventId")));

        Gson gson = new Gson();
        user.setV_Plan(gson.fromJson(cursor.getString(cursor.getColumnIndex("plan")), Plan.class));
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

        Gson gson = new Gson();
        values.put("plan", gson.toJson(user.getV_Plan()));
        return values;
    }

}
