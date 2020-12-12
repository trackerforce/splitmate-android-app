package com.trackerforce.splitmate.ui.dashboard.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.trackerforce.splitmate.DashboardActivity;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.event.EventController;
import com.trackerforce.splitmate.controller.user.UserController;
import com.trackerforce.splitmate.ui.SplitmateView;
import com.trackerforce.splitmate.ui.dashboard.EventPreviewAdapter;
import com.trackerforce.splitmate.ui.dashboard.components.IEventDashComponent;
import com.trackerforce.splitmate.ui.fragment.FragmentListener;
import com.trackerforce.splitmate.ui.fragment.IFragmentSubscriber;

public abstract class AbstractDashFragment extends Fragment
        implements SplitmateView, IFragmentSubscriber {

    protected boolean firstLoad = true;
    protected EditText textFilterEvents;
    protected EventPreviewAdapter adapter;
    protected RecyclerView listView;
    protected IEventDashComponent eventDashComponent;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeContainer = getComponent(R.id.swipeContainer, SwipeRefreshLayout.class);
        swipeContainer.setOnRefreshListener(this::onRefreshLayout);

        adapter = new EventPreviewAdapter(eventDashComponent);
        listView = getComponent(R.id.listEvents, RecyclerView.class);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));

        textFilterEvents = getComponent(R.id.textFilterEvents, EditText.class);
        textFilterEvents.setOnKeyListener(this::onFilter);
        setOnClickListener(R.id.btnToggleEventFilter, this::onToggleFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        getFragmentListener().subscribe(this);

        View view = inflater.inflate(eventDashComponent.getLayout(), container, false);
        ViewGroup viewGroup = view.findViewById(R.id.constraintLayout);
        viewGroup.getLayoutTransition().setAnimateParentHierarchy(false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (firstLoad) {
            onRefresh(getView(), false);
            firstLoad = false;
        }
    }

    @Override
    public void notifyFragment(@Nullable Object... args) {
        onRefresh(getView(), true);
    }

    protected void setComponent(IEventDashComponent eventDashComponent) {
        this.eventDashComponent = eventDashComponent;
    }

    private void onRefreshLayout() {
        onRefresh(getView(), true);
        swipeContainer.setRefreshing(false);
    }

    private boolean onFilter(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_UP && adapter.getDataSet() != null) {
            adapter.filter(textFilterEvents.getText().toString());
        }
        return false;
    }

    private void onToggleFilter(View view) {
        if (textFilterEvents.getVisibility() == View.GONE) {
            textFilterEvents.setVisibility(View.VISIBLE);
        } else {
            textFilterEvents.setVisibility(View.GONE);
        }
    }

    public EventController getEventController() {
        return ((DashboardActivity) requireActivity()).getEventController();
    }

    public UserController getUserController() {
        return ((DashboardActivity) requireActivity()).getUserController();
    }

    public FragmentListener getFragmentListener() {
        return ((DashboardActivity) requireActivity()).getFragmentListener();
    }

    /**
     * Performs API call to update the data from the view
     */
    abstract void onRefresh(@Nullable View view, boolean force);

}
