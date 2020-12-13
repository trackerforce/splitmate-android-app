package com.trackerforce.splitmate.dao.wrapper;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.trackerforce.splitmate.model.Jwt;
import com.trackerforce.splitmate.model.Login;
import com.trackerforce.splitmate.model.Plan;
import com.trackerforce.splitmate.model.User;

public class LoginWrapper implements EntityWrapper<Login> {

    @Override
    public String getTableName() {
        return "LOGIN";
    }

    @Override
    public String getCreateTable() {
        return String.format(
                "CREATE TABLE %s (id, name, email, username, plan, token)",
                getTableName());
    }

    @Override
    public Login unWrap(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getString(cursor.getColumnIndex("id")));
        user.setName(cursor.getString(cursor.getColumnIndex("name")));
        user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
        user.setUsername(cursor.getString(cursor.getColumnIndex("username")));

        Gson gson = new Gson();
        user.setV_Plan(gson.fromJson(cursor.getString(cursor.getColumnIndex("plan")), Plan.class));

        Login login = new Login();
        login.setJwt(new Jwt());
        login.getJwt().setToken(cursor.getString(cursor.getColumnIndex("token")));
        login.setUser(user);

        return login;
    }

    @Override
    public ContentValues wrap(Login entity) {
        ContentValues values = new ContentValues();
        values.put("id", entity.getUser().getId());
        values.put("name", entity.getUser().getName());
        values.put("email", entity.getUser().getEmail());
        values.put("username", entity.getUser().getUsername());
        values.put("token", entity.getJwt().getToken());

        Gson gson = new Gson();
        values.put("plan", gson.toJson(entity.getUser().getV_Plan()));
        return values;
    }
}
