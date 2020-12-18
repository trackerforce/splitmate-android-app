package com.trackerforce.splitmate.ui.item;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.trackerforce.splitmate.DashboardActivity;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.controller.event.EventController;
import com.trackerforce.splitmate.model.ErrorResponse;
import com.trackerforce.splitmate.model.Item;
import com.trackerforce.splitmate.model.Poll;
import com.trackerforce.splitmate.model.pusher.PusherPollItemDTO;
import com.trackerforce.splitmate.pusher.PusherClient;
import com.trackerforce.splitmate.pusher.PusherEvents;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PollVotePreviewAdapter extends ListAdapter<Poll, PollVotePreviewAdapter.PollViewHolder> {

    private final EventController eventController;
    private final List<Poll> localDataSet;

    private String eventId;
    private String itemId;
    private int totalVotes;

    public PollVotePreviewAdapter(EventController eventController) {
        super(new PollDiff());

        this.eventController = eventController;
        this.localDataSet = new ArrayList<>();
    }

    @NonNull
    @Override
    public PollViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.poll_vote_item_preview, parent, false);

        return new PollViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PollViewHolder viewHolder, int position) {
        viewHolder.bind(localDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void updateAdapter(Poll[] data) {
        if (data != null) {
            localDataSet.clear();
            localDataSet.addAll(Arrays.asList(data));
            localDataSet.sort((poll, t1) -> poll.getValue().compareTo(t1.getValue()));

            this.totalVotes = 0;
            for (Poll poll : data)
                this.totalVotes += poll.getVotes().length;

            notifyDataSetChanged();
        }
    }

    public List<Poll> getDataSet() {
        return this.localDataSet;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    static class PollDiff extends DiffUtil.ItemCallback<Poll> {

        @Override
        public boolean areItemsTheSame(@NonNull Poll oldItem, @NonNull Poll newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Poll oldItem, @NonNull Poll newItem) {
            return oldItem.equals(newItem);
        }
    }

    class PollViewHolder extends RecyclerView.ViewHolder {

        private Poll poll;

        public PollViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @SuppressLint("ClickableViewAccessibility")
        public void bind(Poll poll) {
            this.poll = poll;

            AppUtils.setListener(itemView, this::onVote);

            TextView textValue = itemView.findViewById(R.id.textValue);
            textValue.setText(poll.getValue());

            int votes = poll.getVotes().length;
            float percentage = totalVotes == 0 ? 0 : ((float) votes/totalVotes) * 100;
            TextView textCounter = itemView.findViewById(R.id.textCounter);

            textCounter.setText(String.format("%s%%", (int) percentage));

            if (percentage > 0) {
                textCounter.setWidth((700*((int) percentage))/100);
            } else {
                textCounter.setWidth(100);
            }
        }

        private void onVote(View view) {
            setLoading(true);
            eventController.votePoll(eventId, itemId, poll.getId(), new ServiceCallback<Item>() {
                @Override
                public void onSuccess(Item data) {
                    setLoading(false);
                    updateAdapter(data.getPoll());

                    PusherClient pusher = PusherClient.getInstance();
                    PusherPollItemDTO pusherPollItemDTO = new PusherPollItemDTO(eventId, itemId, PusherEvents.UPDATE_POLL);
                    pusherPollItemDTO.setPollItemId(poll.getId());
                    pusherPollItemDTO.setVoter(Config.getInstance().getLoggedUser().getUser().getId());
                    pusher.notifyEvent(pusherPollItemDTO);
                }

                @Override
                public void onError(String error) {
                    setLoading(false);
                    AppUtils.showMessage(itemView.getContext(), error);
                }

                @Override
                public void onError(String error, Object obj) {
                    final ErrorResponse errorResponse = (ErrorResponse) obj;

                    if ("event".equals(errorResponse.getDocument())) {
                        Intent intent = new Intent(itemView.getContext(), DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        itemView.getContext().startActivity(intent);
                    } else {
                        ((Activity) itemView.getContext()).finish();
                    }
                }
            }, true);
        }

        private void setLoading(boolean status) {
            itemView.findViewById(R.id.pollProgressBar).setVisibility(status ? View.VISIBLE : View.INVISIBLE);
            itemView.findViewById(R.id.pollIcon).setVisibility(status ? View.INVISIBLE : View.VISIBLE);
        }
    }

}
