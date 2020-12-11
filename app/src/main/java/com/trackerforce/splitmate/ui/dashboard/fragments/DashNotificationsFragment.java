package com.trackerforce.splitmate.ui.dashboard.fragments;

import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.ui.dashboard.components.EventNotificationComponent;
import com.trackerforce.splitmate.ui.fragment.IFragmentSubscriber;
import com.trackerforce.splitmate.utils.AppUtils;

public class DashNotificationsFragment extends AbstractDashFragment implements IFragmentSubscriber {

    public static final String TITLE = "Notifications";

    public DashNotificationsFragment() {
        super();
        setComponent(new EventNotificationComponent(this));
    }

    @Override
    public void onRefresh(@Nullable View view, boolean force) {
        if (getView() != null) {
            getComponent(R.id.listEvents, RecyclerView.class).setVisibility(View.INVISIBLE);
            getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.VISIBLE);
            getEventController().myEventsInvited(new ServiceCallback<Event[]>() {
                @Override
                public void onSuccess(Event[] data) {
                    if (adapter != null) {
                        adapter.updateAdapter(data);

                        getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.GONE);
                        getComponent(R.id.listEvents, RecyclerView.class).setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(String error) {
                    AppUtils.showMessage(getContext(), error);
                    getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.GONE);
                    getComponent(R.id.listEvents, RecyclerView.class).setVisibility(View.VISIBLE);
                }
            }, force);
        }
    }

    @Override
    public String getFragmentTag() {
        return TITLE;
    }

}