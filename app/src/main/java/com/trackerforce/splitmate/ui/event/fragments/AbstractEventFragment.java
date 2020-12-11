package com.trackerforce.splitmate.ui.event.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.trackerforce.splitmate.EventDetailActivity;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.event.EventController;
import com.trackerforce.splitmate.controller.user.UserController;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.ui.SplitmateView;
import com.trackerforce.splitmate.utils.AppUtils;

public abstract class AbstractEventFragment extends Fragment implements SplitmateView {

    protected int layout;
    protected boolean bypassOnLoad = false;
    private boolean firstLoad = true;

    public AbstractEventFragment(int layout) {
        super();
        this.layout = layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            onRefresh(getView());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // It improves performance by using the in-memory data
        // Used at EventItemFragment
        if (!AppUtils.isOnline(getContext())) {
            bypassOnLoad = true;
        }

        View view = inflater.inflate(this.layout, container, false);
        ViewGroup viewGroup = view.findViewById(R.id.constraintLayout);
        if (viewGroup != null && viewGroup.getLayoutTransition() != null)
            viewGroup.getLayoutTransition().setAnimateParentHierarchy(false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (firstLoad) {
            onRefresh(getView());
            firstLoad = false;
        }
    }

    public String getEventId() {
        return ((EventDetailActivity) requireActivity()).getEventId();
    }

    public void setEvent(Event event) {
        ((EventDetailActivity) requireActivity()).setEvent(event);
    }

    public Event getEvent() {
        return ((EventDetailActivity) requireActivity()).getEvent();
    }

    public EventController getEventController() {
        return ((EventDetailActivity) requireActivity()).getEventController();
    }

    public UserController getUserController() {
        return ((EventDetailActivity) requireActivity()).getUserController();
    }

    public abstract void onRefresh(@Nullable View view);

}
