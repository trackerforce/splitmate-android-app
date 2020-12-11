package com.trackerforce.splitmate.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.trackerforce.splitmate.dao.wrapper.EventWrapper;
import com.trackerforce.splitmate.dao.wrapper.ItemWrapper;
import com.trackerforce.splitmate.dao.wrapper.LoginWrapper;
import com.trackerforce.splitmate.dao.wrapper.UserWrapper;

public class DbFactory extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SPLITMATE_DB";
    private static final int DATABASE_VERSION = 1;
    private static DbFactory instance;

    private DbFactory(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(new UserWrapper().getCreateTable());
        sqLiteDatabase.execSQL(new LoginWrapper().getCreateTable());
        sqLiteDatabase.execSQL(new ItemWrapper().getCreateTable());
        sqLiteDatabase.execSQL(new EventWrapper().getCreateTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        final String dropTable = "DROP TABLE IF EXISTS %s";
        sqLiteDatabase.execSQL(String.format(dropTable, new UserWrapper().getTableName()));
        sqLiteDatabase.execSQL(String.format(dropTable, new LoginWrapper().getTableName()));
        sqLiteDatabase.execSQL(String.format(dropTable, new ItemWrapper().getTableName()));
        sqLiteDatabase.execSQL(String.format(dropTable, new EventWrapper().getTableName()));
        onCreate(sqLiteDatabase);
    }

    public static DbFactory getInstance(Context context) {
        if (instance == null) {
            instance = new DbFactory(context);
        }
        return instance;
    }

    public SQLiteDatabase getDbWriter() {
        return getWritableDatabase();
    }

    public SQLiteDatabase getDbReader() {
        return getReadableDatabase();
    }
}
