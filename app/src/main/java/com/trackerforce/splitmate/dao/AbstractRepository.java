package com.trackerforce.splitmate.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.trackerforce.splitmate.dao.wrapper.EntityResolver;
import com.trackerforce.splitmate.dao.wrapper.EntityWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for defining basic CRUD operations
 */
public abstract class AbstractRepository<T> {

    private static final String TAG = AbstractRepository.class.getSimpleName();

    protected final Context context;
    protected EntityWrapper<T> wrapper;

    public AbstractRepository(Context context, EntityWrapper<T> wrapper) {
        this.context = context;
        this.wrapper = wrapper;
    }

    public void save(T entity) throws Exception {
        SQLiteDatabase dbWriter = DbFactory.getInstance(context).getDbWriter();
        try {
            dbWriter.insert(wrapper.getTableName(), null, wrapper.wrap(entity));
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            throw new Exception("Failed to save", e);
        }
    }

    public void update(String id, T entity) throws Exception {
        SQLiteDatabase dbWriter = DbFactory.getInstance(context).getDbWriter();
        try {
            dbWriter.update(wrapper.getTableName(), wrapper.wrap(entity),
                    "id = ?", new String[]{ id });
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            throw new Exception("Failed to update", e);
        }
    }

    public List<T> findAll() throws Exception {
        SQLiteDatabase dbReader = DbFactory.getInstance(context).getDbReader();
        final String sql = String.format("SELECT * FROM %s",  wrapper.getTableName());

        List<T> listEntity = new ArrayList<>();
        try (Cursor cursor = dbReader.rawQuery(sql, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                listEntity.add(wrapper.unWrap(cursor));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            throw new Exception("Failed to findAll", e);
        }

        return listEntity;
    }

    public T findById(String id) throws Exception {
        final List<T> result = find("id = ?", new String[] { id });
        if (result != null && result.size() > 0)
            return result.get(0);
        return null;
    }

    public T findById(String id, EntityResolver<T> resolver) throws Exception {
        final List<T> result = find("id = ?", new String[] { id }, resolver);
        if (result != null && result.size() > 0)
            return result.get(0);
        return null;
    }

    public List<T> find(String whereClause, String[] values, EntityResolver<T> resolver) throws Exception {
        SQLiteDatabase dbReader = DbFactory.getInstance(context).getDbReader();
        final String sql = String.format("SELECT * FROM %s WHERE %s", wrapper.getTableName(), whereClause);

        List<T> listEntity = new ArrayList<>();
        try (Cursor cursor = dbReader.rawQuery(sql, values)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                listEntity.add(resolver.resolve(wrapper.unWrap(cursor)));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            throw new Exception("Failed to find", e);
        }

        return listEntity;
    }

    public List<T> find(String whereClause, String[] values) throws Exception {
        SQLiteDatabase dbReader = DbFactory.getInstance(context).getDbReader();
        final String sql = String.format("SELECT * FROM %s WHERE %s", wrapper.getTableName(), whereClause);

        List<T> listEntity = new ArrayList<>();
        try (Cursor cursor = dbReader.rawQuery(sql, values)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                listEntity.add(wrapper.unWrap(cursor));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            throw new Exception("Failed to find", e);
        }

        return listEntity;
    }

    public void delete(String whereClause, String[] values) throws Exception {
        SQLiteDatabase dbWriter = DbFactory.getInstance(context).getDbWriter();
        try {
            dbWriter.delete(wrapper.getTableName(), whereClause, values);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            throw new Exception("Failed to delete", e);
        }
    }

    public void deleteById(String id) throws Exception {
        delete("id = ?", new String[] { id });
    }

}
