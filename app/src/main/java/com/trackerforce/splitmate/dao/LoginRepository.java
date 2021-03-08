package com.trackerforce.splitmate.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.petruki.dblite.AbstractRepository;
import com.github.petruki.dblite.wrapper.DbLiteWrapper;
import com.trackerforce.splitmate.dao.wrapper.EventWrapper;
import com.trackerforce.splitmate.dao.wrapper.ItemWrapper;
import com.trackerforce.splitmate.dao.wrapper.LoginWrapper;
import com.trackerforce.splitmate.dao.wrapper.UserWrapper;
import com.trackerforce.splitmate.model.Login;

public class LoginRepository extends AbstractRepository<Login> {

    public LoginRepository(Context context) {
        super(context, new LoginWrapper(), SplitmateDB.class);
    }

    public void cleanLocalStorage() {
        final String deleteStmt = "DELETE FROM %s";
        final SQLiteDatabase sqLiteDatabase =
                SplitmateDB.getInstance(context, SplitmateDB.class).getDbWriter();

        sqLiteDatabase.execSQL(String.format(deleteStmt,
                UserWrapper.class.getAnnotation(DbLiteWrapper.class).entityName()));
        sqLiteDatabase.execSQL(String.format(deleteStmt,
                LoginWrapper.class.getAnnotation(DbLiteWrapper.class).entityName()));
        sqLiteDatabase.execSQL(String.format(deleteStmt,
                ItemWrapper.class.getAnnotation(DbLiteWrapper.class).entityName()));
        sqLiteDatabase.execSQL(String.format(deleteStmt,
                EventWrapper.class.getAnnotation(DbLiteWrapper.class).entityName()));
    }
}
