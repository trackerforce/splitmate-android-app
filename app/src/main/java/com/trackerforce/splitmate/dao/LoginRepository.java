package com.trackerforce.splitmate.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.trackerforce.splitmate.dao.wrapper.EventWrapper;
import com.trackerforce.splitmate.dao.wrapper.ItemWrapper;
import com.trackerforce.splitmate.dao.wrapper.LoginWrapper;
import com.trackerforce.splitmate.dao.wrapper.UserWrapper;
import com.trackerforce.splitmate.model.Login;

public class LoginRepository extends AbstractRepository<Login> {

    public LoginRepository(Context context) {
        super(context, new LoginWrapper());
    }

    public void cleanLocalStorage() {
        final SQLiteDatabase sqLiteDatabase = DbFactory.getInstance(context).getDbWriter();
        sqLiteDatabase.execSQL(String.format("DELETE FROM %s", new UserWrapper().getTableName()));
        sqLiteDatabase.execSQL(String.format("DELETE FROM %s", new LoginWrapper().getTableName()));
        sqLiteDatabase.execSQL(String.format("DELETE FROM %s", new ItemWrapper().getTableName()));
        sqLiteDatabase.execSQL(String.format("DELETE FROM %s", new EventWrapper().getTableName()));
    }
}
