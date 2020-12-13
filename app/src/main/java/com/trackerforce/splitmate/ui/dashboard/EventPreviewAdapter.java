package com.trackerforce.splitmate.ui.dashboard;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.ui.dashboard.components.IEventDashComponent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventPreviewAdapter extends ListAdapter<Event, EventPreviewAdapter.EventViewHolder> {

    private final SimpleDateFormat simpleDateFormat;
    private final IEventDashComponent eventDashComponent;
    private final List<Event> localDataSet;
    private final List<Event> originalLocalData;

    @SuppressLint("SimpleDateFormat")
    public EventPreviewAdapter(IEventDashComponent eventPreview) {
        super(new EventDiff());

        this.localDataSet = new ArrayList<>();
        this.originalLocalData = new ArrayList<>();
        this.eventDashComponent = eventPreview;
        simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm");
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(eventDashComponent.getItemLayout(), parent, false);

        return new EventViewHolder(view, eventDashComponent);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder viewHolder, int position) {
        viewHolder.bind(localDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void updateAdapter(Event[] data) {
        localDataSet.clear();
        localDataSet.addAll(Arrays.asList(data));

        originalLocalData.clear();
        originalLocalData.addAll(Arrays.asList(data));

        notifyDataSetChanged();
    }

    public void filter(String filter) {
        localDataSet.clear();
        if(filter.isEmpty()){
            localDataSet.addAll(originalLocalData);
        } else{
            final String query = filter.toLowerCase();
            for (Event event : originalLocalData) {
                if (event.getName().toLowerCase().contains(query) ||
                        event.getLocation().toLowerCase().contains(query) ||
                        simpleDateFormat.format(event.getDate()).toLowerCase().contains(query)) {
                    localDataSet.add(event);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<Event> getDataSet() {
        return this.localDataSet;
    }

    static class EventDiff extends DiffUtil.ItemCallback<Event> {

        @Override
        public boolean areItemsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
            return oldItem.equals(newItem);
        }
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        private Event event;

        public EventViewHolder(@NonNull View itemView, IEventDashComponent eventDashComponent) {
            super(itemView);

            final Button btnEventTop = itemView.findViewById(R.id.btnEventUp);
            btnEventTop.setOnClickListener(view -> eventDashComponent.onButtonTop(itemView, event));

            final View btnEventBottom = itemView.findViewById(R.id.btnEventDown);
            btnEventBottom.setOnClickListener(view -> eventDashComponent.onButtonBottom(itemView, event));
        }

        public void bind(Event event) {
            this.event = event;

            ((TextView) itemView.findViewById(R.id.textEventName)).setText(event.getName());

            if (event.getLocation() == null || event.getLocation().isEmpty()) {
               itemView.findViewById(R.id.textEventLocation).setVisibility(View.GONE);
            } else {
                ((TextView) itemView.findViewById(R.id.textEventLocation)).setText(event.getLocation());
            }

            @SuppressLint("SimpleDateFormat")
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy hh:mm aa");

            if (event.getDate() != null) {
                ((TextView) itemView.findViewById(R.id.textEventDate)).setText(simpleDateFormat.format(event.getDate()));
            }
        }
    }

}
