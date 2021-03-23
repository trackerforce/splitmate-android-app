package com.trackerforce.splitmate.ui.dashboard.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ProgressBar;

import com.trackerforce.splitmate.NewEventActivity;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.ui.dashboard.components.EventComponent;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.Config;
import com.trackerforce.splitmate.utils.SplitConstants;

public class DashEventsFragment extends AbstractDashFragment implements ServiceCallback<Event[]> {

    public static final String TITLE = "Events";

    public DashEventsFragment() {
        super();
        setComponent(new EventComponent(this));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnClickListener(R.id.btnNewEvent, this::onNewEvent);
        getTextView(R.id.txtSwipeRefresh).setVisibility(
                Config.getInstance().isEconomicEnabled(getContext()) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRefresh(@Nullable View view, boolean force) {
        loadEvents(force);
    }

    @Override
    public String getFragmentTag() {
        return TITLE;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!firstLoad) {
            getComponent(R.id.listEvents, RecyclerView.class).setVisibility(View.INVISIBLE);
            getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.VISIBLE);
            getEventController().myEventsCurrentLocal(this);
        }
    }

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

    private void loadEvents(boolean force) {
        getComponent(R.id.listEvents, RecyclerView.class).setVisibility(View.INVISIBLE);
        getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.VISIBLE);
        getEventController().myEventsCurrent(this, force);
    }

    private void onNewEvent(@Nullable View view) {
        startActivityForResult(new Intent(getContext(), NewEventActivity.class), SplitConstants.EVENT.ordinal());
    }
}