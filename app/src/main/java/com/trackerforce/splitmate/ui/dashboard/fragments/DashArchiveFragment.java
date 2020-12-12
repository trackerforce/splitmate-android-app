package com.trackerforce.splitmate.ui.dashboard.fragments;

import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.ui.dashboard.components.EventArchiveComponent;
import com.trackerforce.splitmate.utils.AppUtils;

public class DashArchiveFragment extends AbstractDashFragment implements ServiceCallback<Event[]> {

    public static final String TITLE = "Archive";

    public DashArchiveFragment() {
        super();
        setComponent(new EventArchiveComponent(this));
    }

    @Override
    public void onRefresh(@Nullable View view, boolean force) {
        loadEvents(force);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!firstLoad) {
            getComponent(R.id.listEvents, RecyclerView.class).setVisibility(View.INVISIBLE);
            getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.VISIBLE);
            getEventController().myEventsArchivedLocal(this);
        }
    }

    @Override
    public String getFragmentTag() {
        return TITLE;
    }

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

    private void loadEvents(boolean force) {
        getComponent(R.id.listEvents, RecyclerView.class).setVisibility(View.INVISIBLE);
        getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.VISIBLE);
        getEventController().myEventsArchived(this, force);
    }
}