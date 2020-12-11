package com.trackerforce.splitmate.dao.wrapper;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * EntityWrappers are used to transitioning application model objects to SQLite DB objects
 */
public interface EntityWrapper<T> {

    /**
     * Defines the table name given to the Entity to be saved to SQLite DB
     */
    String getTableName();

    /**
     * Defines the create table script
     */
    String getCreateTable();

    /**
     * Unwrap object given the object model type
     */
    T unWrap(Cursor cursor);

    /**
     * Wrap object to be contained into a valida DB model object
     */
    ContentValues wrap(T entity);

}
