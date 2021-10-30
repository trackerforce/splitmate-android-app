package com.trackerforce.splitmate.ui.event;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trackerforce.splitmate.DashboardActivity;
import com.trackerforce.splitmate.EventDetailActivity;
import com.trackerforce.splitmate.NewItemActivity;
import com.trackerforce.splitmate.PollActivity;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.controller.event.EventController;
import com.trackerforce.splitmate.model.ErrorResponse;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.model.Item;
import com.trackerforce.splitmate.model.pusher.PusherItemDTO;
import com.trackerforce.splitmate.pusher.PusherClient;
import com.trackerforce.splitmate.pusher.PusherEvents;
import com.trackerforce.splitmate.ui.event.fragments.EventPreviewFragment;
import com.trackerforce.splitmate.utils.Config;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.SplitConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ItemPreviewAdapter extends ListAdapter<Item, ItemPreviewAdapter.ItemViewHolder> {

    private final EventController eventController;
    private final List<Item> localDataSet;
    private final List<Item> originalLocalData;
    private String organizerId;
    private String eventId;

    public ItemPreviewAdapter(EventController eventController) {
        super(new ItemPreviewAdapter.ItemDiff());

        this.eventController = eventController;
        this.localDataSet = new ArrayList<>();
        this.originalLocalData = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemPreviewAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.event_item_preview, parent, false);

        return new ItemPreviewAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemPreviewAdapter.ItemViewHolder viewHolder, int position) {
        viewHolder.bind(localDataSet.get(position), position);
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String filter) {
        localDataSet.clear();
        if(filter.isEmpty()){
            localDataSet.addAll(originalLocalData);
        } else{
            final String query = filter.toLowerCase();
            for (Item item : originalLocalData) {
                if (item.getName().toLowerCase().contains(query) || //check item name
                    (item.getV_assigned_to() != null && //check item assignee
                        item.getV_assigned_to().getName().toLowerCase().contains(query))) {
                    localDataSet.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<Item> getDataSet() {
        return this.localDataSet;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(Item[] data) {
        localDataSet.clear();
        localDataSet.addAll(Arrays.asList(data));
        localDataSet.sort(Comparator.comparing(Item::getName));

        originalLocalData.clear();
        originalLocalData.addAll(Arrays.asList(data));

        notifyDataSetChanged();
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    static class ItemDiff extends DiffUtil.ItemCallback<Item> {

        @Override
        public boolean areItemsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem.equals(newItem);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        private Switch switchPickItem;
        private ProgressBar progressBar;
        private int position;
        private boolean loading;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Item item, int position) {
            this.position = position;
            this.loading = true;

            ((TextView) itemView.findViewById(R.id.textItemName)).setText(item.getName());

            final FloatingActionButton btnEditItem = itemView.findViewById(R.id.btnEditItem);
            AppUtils.setListener(btnEditItem, view -> onEdit(itemView, item));

            progressBar = itemView.findViewById(R.id.itemProgressBar);
            switchPickItem = itemView.findViewById(R.id.switchPickItem);
            switchPickItem.setOnCheckedChangeListener((compoundButton, isChecked) ->
                    onChangePick(itemView, compoundButton, isChecked, item));
            switchPickItem.setClickable(isPickable(item));

            //set up not/assignee
            if (item.getV_assigned_to() != null) {
                switchPickItem.setChecked(true);
                ((TextView) itemView.findViewById(R.id.textPickedBy)).setText(item.getV_assigned_to().getName());
            } else {
                switchPickItem.setChecked(false);
                ((TextView) itemView.findViewById(R.id.textPickedBy)).setText("");
            }

            //set up poll
            final ImageView iconPoll = itemView.findViewById(R.id.iconPoll);
            if (item.hasPoll()) {
                iconPoll.setVisibility(View.VISIBLE);
                itemView.setClickable(true);
                AppUtils.setListener(iconPoll, view -> this.onShowPoll(itemView, item));
            } else {
                iconPoll.setVisibility(View.GONE);
                itemView.setClickable(false);
                iconPoll.setOnClickListener(null);
            }

            loading = false;
        }

        private boolean isPickable(Item item) {
            return (item.getAssigned_to() == null ||
                    Config.getInstance().getLoggedUser().getUser().getId().equals(item.getAssigned_to()));
        }

        private void onShowPoll(View view, Item item) {
            Intent intent = new Intent(view.getContext(), PollActivity.class);
            intent.putExtra(SplitConstants.ITEM_ID.toString(), item.getId());
            intent.putExtra(SplitConstants.EVENT_ID.toString(), eventId);
            (view.getContext()).startActivity(intent);
        }

        private void onChangePick(View convertView, CompoundButton compoundButton,
                                  boolean isChecked, Item item) {
            if (!loading) {
                if (isChecked) {
                    switchPickItem.setClickable(false);
                    pickItem(convertView, compoundButton, item);
                } else {
                    switchPickItem.setClickable(false);
                    unpickItem(convertView, compoundButton, item);
                }
            }
        }

        private void pickItem(View convertView, CompoundButton compoundButton, Item item) {
            toggleSlider();
            eventController.pickupItem(eventId, item, new ServiceCallback<>() {
                @Override
                public void onSuccess(Event data) {
                    item.setV_assigned_to(Config.getInstance().getLoggedUser().getUser());
                    item.setAssigned_to(Config.getInstance().getLoggedUser().getUser().getId());
                    notifyItemChanged(position);

                    switchPickItem.setClickable(true);
                    toggleSlider();
                    notifyPusher(item.getId(), item.getAssigned_to(), PusherEvents.PICK_ITEM);

                    ((EventDetailActivity) convertView.getContext()).getFragmentListener()
                            .notifySubscriber(EventPreviewFragment.TITLE, data);
                }

                @Override
                public void onError(String error) {
                    AppUtils.showMessage(convertView.getContext(), error);
                    toggleSlider();

                    loading = true;
                    compoundButton.setChecked(false);
                    compoundButton.setClickable(true);
                    loading = false;
                }

                @Override
                public void onError(String error, Object obj) {
                    final ErrorResponse errorResponse = (ErrorResponse) obj;
                    ItemViewHolder.this.onError(convertView, errorResponse, item);
                }
            }, true);
        }

        private void unpickItem(View convertView, CompoundButton compoundButton, Item item) {
            toggleSlider();
            eventController.unpickItem(eventId, item, new ServiceCallback<>() {
                @Override
                public void onSuccess(Event data) {
                    item.setV_assigned_to(null);
                    item.setAssigned_to(null);
                    notifyItemChanged(position);

                    switchPickItem.setClickable(true);
                    toggleSlider();
                    notifyPusher(item.getId(), null, PusherEvents.UNPICK_ITEM);

                    ((EventDetailActivity) convertView.getContext()).getFragmentListener()
                            .notifySubscriber(EventPreviewFragment.TITLE, data);
                }

                @Override
                public void onError(String error) {
                    AppUtils.showMessage(convertView.getContext(), error);
                    toggleSlider();

                    loading = true;
                    compoundButton.setChecked(true);
                    compoundButton.setClickable(true);
                    loading = false;
                }

                @Override
                public void onError(String error, Object obj) {
                    final ErrorResponse errorResponse = (ErrorResponse) obj;
                    ItemViewHolder.this.onError(convertView, errorResponse, item);
                }
            }, true);
        }

        private void onEdit(View convertView, Item item) {
            eventController.getEventItemById(eventId, item.getId(), new ServiceCallback<>() {
                @Override
                public void onSuccess(Item data) {
                    Intent intent = new Intent(convertView.getContext(), NewItemActivity.class);
                    intent.putExtra(SplitConstants.ITEM.toString(), data);
                    intent.putExtra(SplitConstants.ORGANIZER.toString(), organizerId);
                    intent.putExtra(SplitConstants.EVENT_ID.toString(), eventId);
                    ((Activity) convertView.getContext()).startActivityForResult(intent, SplitConstants.EDIT_ITEM.ordinal());
                }

                @Override
                public void onError(String error) {
                    AppUtils.showMessage(convertView.getContext(), error);
                }

                @Override
                public void onError(String error, Object obj) {
                    final ErrorResponse errorResponse = (ErrorResponse) obj;
                    ItemViewHolder.this.onError(convertView, errorResponse, item);
                }
            }, true);
        }

        private void onError(View convertView, ErrorResponse errorResponse, Item item) {
            if ("event".equals(errorResponse.getDocument())) {
                AppUtils.showMessage(convertView.getContext(), convertView.getResources().getString(R.string.msgEventHasRemoved));
                Intent intent = new Intent(convertView.getContext(), DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                convertView.getContext().startActivity(intent);
            } else {
                AppUtils.showMessage(convertView.getContext(), convertView.getResources().getString(R.string.msgItemHasRemoved));
                localDataSet.remove(item);
                originalLocalData.remove(item);
                notifyItemRemoved(position);
            }
        }

        private void toggleSlider() {
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
                switchPickItem.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                switchPickItem.setVisibility(View.GONE);
            }
        }

        private void notifyPusher(String itemId, @Nullable String assigneeId, PusherEvents pusherEvent) {
            final PusherClient pusher = PusherClient.getInstance();
            final PusherItemDTO pusherItemDTO = new PusherItemDTO(eventId, pusherEvent);
            pusherItemDTO.setItemId(itemId);
            pusherItemDTO.setAssigned_to(assigneeId);

            pusher.notifyEvent(pusherItemDTO);
        }
    }

}
