package com.trackerforce.splitmate;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.pusher.PusherClient;
import com.trackerforce.splitmate.ui.SplitmateActivity;
import com.trackerforce.splitmate.ui.event.EventPageAdapter;
import com.trackerforce.splitmate.ui.event.fragments.EventItemsFragment;
import com.trackerforce.splitmate.ui.event.fragments.EventPreviewFragment;
import com.trackerforce.splitmate.ui.fragment.FragmentListener;
import com.trackerforce.splitmate.utils.Config;
import com.trackerforce.splitmate.utils.SplitConstants;

import java.util.Objects;

public class EventDetailActivity extends SplitmateActivity {

    private final FragmentListener fragmentListener;
    private Event event;
    private String eventId;

    public EventDetailActivity() {
        super(R.layout.activity_event_detail);
        fragmentListener = new FragmentListener();
    }

    @Override
    protected void onCreateView() {
        initActivityData();

        TabLayout tabLayout = findViewById(R.id.tabEvent);
        ViewPager2 viewEvent = findViewById(R.id.viewPagerEvent);

        viewEvent.setAdapter(new EventPageAdapter(this));
        new TabLayoutMediator(tabLayout, viewEvent, this::createTabMediator).attach();

        loadAdView();
    }

    @Override
    protected void initActivityData() {
        eventId = getIntent().getStringExtra(SplitConstants.EVENT_ID.toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode && SplitConstants.EDIT_ITEM.ordinal() == requestCode) {
            assert data != null;
            final Event event = (Event) data.getSerializableExtra(SplitConstants.EVENT.toString());
            fragmentListener.notifySubscriber(EventPreviewFragment.TITLE, event);

            ((EventItemsFragment) Objects.requireNonNull(getSupportFragmentManager()
                    .findFragmentByTag("f1")))
                    .onRefresh(getView());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PusherClient.getInstance().disconnect();
    }

    private void createTabMediator(TabLayout.Tab tab, int position) {
        if (position == 0) {
            tab.setText(getResources().getString(R.string.labelEvent));
            tab.setIcon(android.R.drawable.ic_menu_today);
        } else if (position == 1) {
            tab.setText(getResources().getString(R.string.labelItems));
            tab.setIcon(android.R.drawable.ic_menu_sort_by_size);
        } else {
            tab.setText(getResources().getString(R.string.labelMembers));
            tab.setIcon(android.R.drawable.ic_menu_myplaces);
        }
    }

    private void loadAdView() {
        AdView adView = getComponent(R.id.adView, AdView.class);
        if (Config.getInstance().getLoggedUser().getUser().getV_Plan().isEnable_ads()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        } else {
            adView.setVisibility(View.GONE);
        }
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public FragmentListener getFragmentListener() {
        return fragmentListener;
    }

}