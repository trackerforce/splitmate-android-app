package com.trackerforce.splitmate.ui.dashboard.components;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;

import com.trackerforce.splitmate.EventDetailActivity;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.ui.dashboard.fragments.AbstractDashFragment;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.SplitConstants;

public class EventComponent implements IEventDashComponent {

    private final AbstractDashFragment context;

    public EventComponent(AbstractDashFragment context) {
        this.context = context;
    }

    @Override
    public void onButtonTop(View convertView, Event event) {
        Intent intent = new Intent(convertView.getContext(), EventDetailActivity.class);
        intent.putExtra(SplitConstants.EVENT_ID.toString(), event.getId());
        ((Activity) convertView.getContext()).startActivityForResult(intent, SplitConstants.EVENT.ordinal());
    }

    @Override
    public void onButtonBottom(View convertView, Event event) {
        new AlertDialog.Builder(convertView.getContext())
                .setTitle("Archive Event")
                .setMessage(String.format("Do you want to archive %s ?", event.getName()))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> archiveEvent(convertView, event))
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public int getItemLayout() {
        return R.layout.dash_item_event;
    }

    @Override
    public int getLayout() {
        return R.layout.dash_fragment_events;
    }

    private void archiveEvent(View convertView, Event event) {
        context.getUserController().addEventToArchive(event.getId(), new ServiceCallback<User>() {
            @Override
            public void onSuccess(User data) {
                context.getFragmentListener()
                        .notifySubscriber(context.requireContext().getResources().getString(R.string.tabEvents));
                context.getFragmentListener()
                        .notifySubscriber(context.requireContext().getResources().getString(R.string.tabArchive));
            }

            @Override
            public void onError(String error) {
                AppUtils.showMessage(convertView.getContext(), error);
            }
        }, true);
    }
}
