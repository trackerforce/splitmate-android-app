package com.trackerforce.splitmate.dao;

import android.content.Context;

import com.github.petruki.dblite.AbstractRepository;
import com.trackerforce.splitmate.dao.wrapper.UserWrapper;
import com.trackerforce.splitmate.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository extends AbstractRepository<User> {

    public UserRepository(Context context) {
        super(context, new UserWrapper(), SplitmateDB.class);
    }

    public List<User> findByEventId(String eventId) throws Exception {
        return new ArrayList<>(find("eventId = ?", new String[]{ eventId }));
    }

    public void deleteByEventId(String eventId) throws Exception {
        delete("eventId = ?", new String[] { eventId });
    }

    public void deleteByEventIdUserId(String eventId, String userId) throws Exception {
        delete("eventId = ? AND id = ?", new String[] { eventId, userId });
    }
}
