package com.trackerforce.splitmate.ui.dashboard.components;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.ui.dashboard.fragments.AbstractDashFragment;
import com.trackerforce.splitmate.utils.AppUtils;

public class EventNotificationComponent implements IEventDashComponent {

    private final AbstractDashFragment context;

    public EventNotificationComponent(AbstractDashFragment context) {
        this.context = context;
    }

    @Override
    public void onButtonTop(View convertView, Event event) {
        context.getUserController().joinEvent(event.getId(), new ServiceCallback<User>() {
            @Override
            public void onSuccess(User data) {
                ((ViewPager2) convertView.getRootView().findViewById(R.id.viewPagerDashboard)).setCurrentItem(0);

                context.getFragmentListener()
                        .notifySubscriber(context.requireContext().getResources().getString(R.string.tabEvents));
                context.getFragmentListener()
                        .notifySubscriber(context.requireContext().getResources().getString(R.string.tabNotifications));
            }

            @Override
            public void onError(String error) {
                AppUtils.showMessage(convertView.getContext(), error);
            }
        }, true);
    }

    @Override
    public void onButtonBottom(View convertView, Event event) {
        context.getUserController().dismissEvent(event.getId(), new ServiceCallback<User>() {
            @Override
            public void onSuccess(User data) {
                context.getFragmentListener()
                        .notifySubscriber(context.requireContext().getResources().getString(R.string.tabNotifications));
            }

            @Override
            public void onError(String error) {
                AppUtils.showMessage(convertView.getContext(), error);
            }
        }, true);
    }

    @Override
    public int getItemLayout() {
        return R.layout.dash_item_notification;
    }

    @Override
    public int getLayout() {
        return R.layout.dash_fragment_notifications;
    }

}
