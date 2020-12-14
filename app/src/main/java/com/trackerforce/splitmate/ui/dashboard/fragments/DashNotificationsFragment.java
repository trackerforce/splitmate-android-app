package com.trackerforce.splitmate.ui.dashboard.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.ui.dashboard.components.EventNotificationComponent;
import com.trackerforce.splitmate.ui.fragment.IFragmentSubscriber;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.Config;

public class DashNotificationsFragment extends AbstractDashFragment implements IFragmentSubscriber {

    public static final String TITLE = "Notifications";

    public DashNotificationsFragment() {
        super();
        setComponent(new EventNotificationComponent(this));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getTextView(R.id.txtSwipeRefresh).setVisibility(
                Config.getInstance().isEconomicEnabled(getContext()) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRefresh(@Nullable View view, boolean force) {
        loadEvents(force);
    }

    private void loadEvents(boolean force) {
        getComponent(R.id.listEvents, RecyclerView.class).setVisibility(View.INVISIBLE);
        getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.VISIBLE);
        getEventController().myEventsInvited(new ServiceCallback<Event[]>() {
            @Override
            public void onSuccess(Event[] data) {
                if (data.length > 0)
                    getTextView(R.id.txtSwipeRefresh).setVisibility(View.GONE);

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

    @Override
    public String getFragmentTag() {
        return TITLE;
    }

}