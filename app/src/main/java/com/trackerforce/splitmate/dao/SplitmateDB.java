package com.trackerforce.splitmate.dao;

import android.content.Context;

import com.github.petruki.dblite.DbLite;
import com.github.petruki.dblite.DbLiteFactory;
import com.trackerforce.splitmate.dao.wrapper.EventWrapper;
import com.trackerforce.splitmate.dao.wrapper.ItemWrapper;
import com.trackerforce.splitmate.dao.wrapper.LoginWrapper;
import com.trackerforce.splitmate.dao.wrapper.UserWrapper;

@DbLite(dbName = "SPLITMATE_DB", version = 1, wrappers = {
        EventWrapper.class,
        ItemWrapper.class,
        UserWrapper.class,
        LoginWrapper.class
})
abstract class SplitmateDB  extends DbLiteFactory {

    protected SplitmateDB(Context context) {
        super(context);
    }
}
