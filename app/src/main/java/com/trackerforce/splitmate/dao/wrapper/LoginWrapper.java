package com.trackerforce.splitmate.dao.wrapper;

import android.content.ContentValues;
import android.database.Cursor;

import com.github.petruki.dblite.wrapper.DbLiteWrapper;
import com.github.petruki.dblite.wrapper.EntityWrapper;
import com.trackerforce.splitmate.model.Jwt;
import com.trackerforce.splitmate.model.Login;
import com.trackerforce.splitmate.model.Plan;
import com.trackerforce.splitmate.model.User;

@DbLiteWrapper(entityName = "LOGIN", columns = {
        "id", "name", "email", "username", "plan", "token" })
public class LoginWrapper implements EntityWrapper<Login> {

    @Override
    public Login unWrap(Cursor cursor) {
        User user = new User();
        user.setId(getString(cursor, "id"));
        user.setName(getString(cursor, "name"));
        user.setEmail(getString(cursor, "email"));
        user.setUsername(getString(cursor, "username"));
        user.setV_Plan(getJson(cursor, "plan", Plan.class));

        Login login = new Login();
        login.setJwt(new Jwt());
        login.getJwt().setToken(getString(cursor, "token"));
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
        values.put("plan", toJson(entity.getUser().getV_Plan()));
        return values;
    }
}
