package com.trackerforce.splitmate.ui.dashboard.components;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import com.trackerforce.splitmate.EventDetailActivity;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.ui.dashboard.fragments.AbstractDashFragment;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.SplitConstants;

public class EventArchiveComponent implements IEventDashComponent {

    private final AbstractDashFragment context;

    public EventArchiveComponent(AbstractDashFragment context) {
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
        context.getUserController().removeEventArchive(event.getId(), new ServiceCallback<User>() {
            @Override
            public void onSuccess(User data) {
                ((ViewPager2) convertView.getRootView().findViewById(R.id.viewPagerDashboard)).setCurrentItem(0);

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

    @Override
    public int getItemLayout() {
        return R.layout.dash_item_archive;
    }

    @Override
    public int getLayout() {
        return R.layout.dash_fragment_archive;
    }

}
