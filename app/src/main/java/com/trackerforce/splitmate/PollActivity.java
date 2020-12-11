package com.trackerforce.splitmate;

import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.ErrorResponse;
import com.trackerforce.splitmate.model.Item;
import com.trackerforce.splitmate.pusher.PusherClient;
import com.trackerforce.splitmate.pusher.PusherEvents;
import com.trackerforce.splitmate.ui.SplitmateActivity;
import com.trackerforce.splitmate.ui.item.PollVotePreviewAdapter;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.SplitConstants;

public class PollActivity extends SplitmateActivity {

    private PollVotePreviewAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private String itemId;
    private String eventId;
    private PusherClient pusher;

    public PollActivity() {
        super(R.layout.activity_poll);
    }

    @Override
    protected void onCreateView() {
        adapter = new PollVotePreviewAdapter(eventController);
        final RecyclerView listView = getComponent(R.id.listPoll, RecyclerView.class);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));

        swipeContainer = getComponent(R.id.swipeContainer, SwipeRefreshLayout.class);
        swipeContainer.setOnRefreshListener(this::onRefreshLayout);
    }

    @Override
    protected void initActivityData() {
        eventId = getIntent().getStringExtra(SplitConstants.EVENT_ID.toString());
        itemId = getIntent().getStringExtra(SplitConstants.ITEM_ID.toString());

        loadItem();
        loadPusher();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pusher.unsubscribe(PusherEvents.UPDATE_POLL.toString());
    }

    private void loadItem() {
        eventController.getEventItemById(eventId, itemId, new ServiceCallback<Item>() {
            @Override
            public void onSuccess(Item data) {
                getTextView(R.id.txtPollName).setText(data.getPoll_name());

                getTextView(R.id.pollTitle).setText(data.getName());
                adapter.setEventId(eventId);
                adapter.setItemId(itemId);
                adapter.updateAdapter(data.getPoll());

                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onError(String error) {
                AppUtils.showMessage(PollActivity.this, error);
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onError(String error, Object obj) {
                swipeContainer.setRefreshing(false);
                final ErrorResponse errorResponse = (ErrorResponse) obj;

                if ("event".equals(errorResponse.getDocument())) {
                    AppUtils.showMessage(PollActivity.this,
                            PollActivity.this.getResources().getString(R.string.msgEventHasRemoved));

                    Intent intent = new Intent(PollActivity.this, DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    AppUtils.showMessage(PollActivity.this,
                            PollActivity.this.getResources().getString(R.string.msgItemHasRemoved));
                    finish();
                }
            }
        }, false);
    }

    private void loadPusher() {
        pusher = PusherClient.getInstance();
        pusher.connect(this, eventId);
        pusher.subscribe(PusherEvents.UPDATE_POLL.toString(), this::onUpdatePoll);
    }

    private void onUpdatePoll(Object... args) {
        runOnUiThread(this::loadItem);
    }

    private void onRefreshLayout() {
        loadItem();
    }
}