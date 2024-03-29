package com.trackerforce.splitmate;

import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.ErrorResponse;
import com.trackerforce.splitmate.model.Item;
import com.trackerforce.splitmate.model.pusher.PusherData;
import com.trackerforce.splitmate.model.pusher.PusherPollItemDTO;
import com.trackerforce.splitmate.pusher.PusherClient;
import com.trackerforce.splitmate.pusher.PusherEvents;
import com.trackerforce.splitmate.ui.SplitmateActivity;
import com.trackerforce.splitmate.ui.item.PollVotePreviewAdapter;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.SplitConstants;

import java.util.Arrays;

public class PollActivity extends SplitmateActivity implements ServiceCallback<Item> {

    private PollVotePreviewAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private String itemId;
    private String eventId;

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

    private void loadItem() {
        getComponent(R.id.swipeContainer, SwipeRefreshLayout.class).setVisibility(View.INVISIBLE);
        getComponent(R.id.progressBarPoll, ProgressBar.class).setVisibility(View.VISIBLE);
        eventController.getEventItemById(eventId, itemId, this, true);
    }

    @Override
    public void onSuccess(Item data) {
        if (data.getPoll().length == 0) {
            AppUtils.showMessage(PollActivity.this,
                    PollActivity.this.getResources().getString(R.string.msgItemHasRemoved));
            finish();
            return;
        }

        getTextView(R.id.txtPollName).setText(data.getPoll_name());

        getTextView(R.id.pollTitle).setText(data.getName());
        adapter.setEventId(eventId);
        adapter.setItemId(itemId);
        adapter.updateAdapter(data.getPoll());

        showPoll();
    }

    @Override
    public void onError(String error) {
        AppUtils.showMessage(PollActivity.this, error);
        showPoll();
    }

    @Override
    public void onError(String error, Object obj) {
        final ErrorResponse errorResponse = (ErrorResponse) obj;
        showPoll();

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

    private void loadPusher() {
        final PusherClient pusher = PusherClient.getInstance();
        pusher.connect(this, eventId);
        pusher.subscribe(PusherEvents.UPDATE_POLL.toString(), this::onUpdatePoll);
        pusher.subscribe(PusherEvents.EDIT_ITEM.toString(), this::onUpdateItem);
    }

    private void onUpdatePoll(Object... args) {
        runOnUiThread(() -> {
            PusherPollItemDTO pusherPollItemDTO = PusherData.getDTO(PusherPollItemDTO.class, args);

            boolean changed = false;
            for (int i = 0; i < adapter.getDataSet().size(); i++) {
                var poll = adapter.getDataSet().get(i);

                if (pusherPollItemDTO.getPollItemId().equals(poll.getId())) {
                    if (Arrays.stream(poll.getVotes()).noneMatch(p -> p.equals(pusherPollItemDTO.getVoter()))) {
                        poll.addVote(pusherPollItemDTO.getVoter());
                        adapter.updateVotes(1);
                        changed = true;
                    }
                } else {
                    if (Arrays.stream(poll.getVotes()).anyMatch(p -> p.equals(pusherPollItemDTO.getVoter()))) {
                        poll.removeVote(pusherPollItemDTO.getVoter());
                        adapter.updateVotes(-1);
                        changed = true;
                    }
                }
            }

            if (changed)
                adapter.notifyItemRangeChanged(0, adapter.getDataSet().size());
        });
    }

    private void onUpdateItem(@Nullable Object... args) {
        runOnUiThread(this::loadItem);
    }

    private void onRefreshLayout() {
        loadItem();
    }

    private void showPoll() {
        getComponent(R.id.swipeContainer, SwipeRefreshLayout.class).setVisibility(View.VISIBLE);
        getComponent(R.id.progressBarPoll, ProgressBar.class).setVisibility(View.GONE);
        swipeContainer.setRefreshing(false);
    }
}