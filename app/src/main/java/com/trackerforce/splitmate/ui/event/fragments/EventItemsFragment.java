package com.trackerforce.splitmate.ui.event.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.trackerforce.splitmate.NewItemActivity;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.model.Item;
import com.trackerforce.splitmate.model.pusher.PusherData;
import com.trackerforce.splitmate.model.pusher.PusherItemDTO;
import com.trackerforce.splitmate.pusher.PusherClient;
import com.trackerforce.splitmate.pusher.PusherEvents;
import com.trackerforce.splitmate.ui.event.ItemPreviewAdapter;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.Config;
import com.trackerforce.splitmate.utils.SplitConstants;

public class EventItemsFragment extends AbstractEventFragment {

    private static final String TAG = EventItemsFragment.class.getSimpleName();

    private SwipeRefreshLayout swipeContainer;
    private ItemPreviewAdapter adapter;
    private TextView textFilterItems;
    private PusherClient pusher;

    public EventItemsFragment() {
        super(R.layout.event_fragment_items);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getTextView(R.id.txtSwipeRefresh).setVisibility(
                Config.getInstance().isEconomicEnabled(getContext()) ? View.VISIBLE : View.GONE);

        swipeContainer = getComponent(R.id.swipeContainer, SwipeRefreshLayout.class);
        swipeContainer.setOnRefreshListener(this::onRefreshLayout);

        adapter = new ItemPreviewAdapter(getEventController());
        RecyclerView listView = getComponent(R.id.listItems, RecyclerView.class);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));

        textFilterItems = getComponent(R.id.textFilterItems, EditText.class);
        textFilterItems.setOnKeyListener(this::onFilter);

        setOnClickListener(R.id.btnAddItem, this::onAddItem);
        setOnClickListener(R.id.btnToggleItemFilter, this::onToggleFilter);

        pusher = PusherClient.getInstance();
        pusher.connect(getContext(), getEventId());
        pusher.subscribe(PusherEvents.UNPICK_ITEM.toString(), this::onNotifyItemUnpick);
        pusher.subscribe(PusherEvents.PICK_ITEM.toString(), this::onNotifyItemPick);
        pusher.subscribe(PusherEvents.DELETE_ITEM.toString(), this::onNotifyItemDeleted);
        pusher.subscribe(PusherEvents.EDIT_ITEM.toString(), this::onNotifyItemEdited);
        pusher.subscribe(PusherEvents.CREATE_ITEM.toString(), this::onNotifyItemCreated);
    }

    @Override
    public void onRefresh(@Nullable View view) {
        loadEvent(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pusher.unsubscribeAll();
    }

    private void loadEvent(boolean force) {
        getComponent(R.id.listItems, RecyclerView.class).setVisibility(View.INVISIBLE);
        getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.VISIBLE);
        getEventController().getEventById(getEventId(), new ServiceCallback<>() {
            @Override
            public void onSuccess(Event data) {
                updateAdapter(data);
            }

            @Override
            public void onError(String error) {
                AppUtils.showMessage(getContext(), error);
                getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.GONE);
                getComponent(R.id.listItems, RecyclerView.class).setVisibility(View.VISIBLE);
            }
        }, force);
    }

    private void updateAdapter(Event event) {
        setEvent(event);

        if (event.getItems().length > 0)
            getTextView(R.id.txtSwipeRefresh).setVisibility(View.GONE);

        if (adapter != null) {
            adapter.setOrganizerId(event.getOrganizer());
            adapter.setEventId(getEventId());
            adapter.updateAdapter(event.getItems());

            getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.GONE);
            getComponent(R.id.listItems, RecyclerView.class).setVisibility(View.VISIBLE);
        }
    }

    private void onRefreshLayout() {
        loadEvent(true);
        swipeContainer.setRefreshing(false);
    }

    private boolean onFilter(@Nullable View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_UP && adapter.getDataSet() != null) {
            adapter.filter(textFilterItems.getText().toString());
        }
        return false;
    }

    private void onAddItem(@Nullable View view) {
        Intent intent = new Intent(getActivity(), NewItemActivity.class);
        intent.putExtra(SplitConstants.EVENT_ID.toString(), getEventId());
        requireActivity().startActivityForResult(intent, SplitConstants.EDIT_ITEM.ordinal());
    }

    private void onToggleFilter(View view) {
        if (textFilterItems.getVisibility() == View.GONE) {
            textFilterItems.setVisibility(View.VISIBLE);
        } else {
            textFilterItems.setVisibility(View.GONE);
        }
    }

    private void onNotifyItemPick(Object... args) {
        requireActivity().runOnUiThread(() -> {
            PusherItemDTO pusherItemDTO = PusherData.getDTO(PusherItemDTO.class, args);
            for (int i = 0; i < adapter.getDataSet().size(); i++) {
                var item = adapter.getDataSet().get(i);
                if (item.getId().equals(pusherItemDTO.getItemId())) {
                    getEventController().pickItemLocal(item, pusherItemDTO.getAssigned_to());
                    adapter.notifyItemChanged(i);
                    break;
                }
            }

            updateEventPreview(pusherItemDTO);
        });
    }

    private void onNotifyItemUnpick(Object... args) {
        requireActivity().runOnUiThread(() -> {
            PusherItemDTO pusherItemDTO = PusherData.getDTO(PusherItemDTO.class, args);
            for (int i = 0; i < adapter.getDataSet().size(); i++) {
                var item = adapter.getDataSet().get(i);
                if (item.getId().equals(pusherItemDTO.getItemId())) {
                    getEventController().unpickItemLocal(item);
                    adapter.notifyItemChanged(i);
                    break;
                }
            }

            updateEventPreview(pusherItemDTO);
        });
    }

    private void onNotifyItemDeleted(Object... args) {
        requireActivity().runOnUiThread(() -> {
            PusherItemDTO pusherItemDTO = PusherData.getDTO(PusherItemDTO.class, args);
            for (int i = 0; i < adapter.getDataSet().size(); i++) {
                var item = adapter.getDataSet().get(i);
                if (item.getId().equals(pusherItemDTO.getItemId())) {
                    getEventController().deleteItemLocal(item.getId());
                    adapter.getDataSet().remove(item);
                    adapter.notifyItemRemoved(i);
                    break;
                }
            }

            updateEventPreview(pusherItemDTO);
        });
    }

    private void onNotifyItemCreated(Object... args) {
        requireActivity().runOnUiThread(() -> {
            PusherItemDTO pusherItemDTO = PusherData.getDTO(PusherItemDTO.class, args);

            getEventController().getEventItemById(pusherItemDTO.getEventId(),
                pusherItemDTO.getItemId(), new ServiceCallback<>() {
                    @Override
                    public void onSuccess(Item data) {
                        adapter.getDataSet().add(data);
                        adapter.notifyItemInserted(adapter.getItemCount());
                        updateEventPreview(pusherItemDTO);
                    }

                    @Override
                    public void onError(String error) {
                        Log.d(TAG, error);
                    }
                }, true);

        });
    }

    private void onNotifyItemEdited(Object... args) {
        requireActivity().runOnUiThread(() -> {
            PusherItemDTO pusherItemDTO = PusherData.getDTO(PusherItemDTO.class, args);

            getEventController().getEventItemById(pusherItemDTO.getEventId(),
                pusherItemDTO.getItemId(), new ServiceCallback<>() {
                    @Override
                    public void onSuccess(Item data) {
                        for (int i = 0; i < adapter.getDataSet().size(); i++) {
                            var item = adapter.getDataSet().get(i);
                            if (item.getId().equals(pusherItemDTO.getItemId())) {
                                item.setName(data.getName());
                                item.setPoll_name(data.getPoll_name());
                                item.setPoll(data.getPoll());
                                item.setDetails(data.getDetails());
                                adapter.notifyItemChanged(i);
                                break;
                            }
                        }

                        updateEventPreview(pusherItemDTO);
                    }

                    @Override
                    public void onError(String error) {
                        Log.d(TAG, error);
                    }
                }, true);
        });
    }

    private void updateEventPreview(PusherItemDTO pusherItemDTO) {
        getEventController().getLocalEventById(pusherItemDTO.getEventId(), new ServiceCallback<>() {
            @Override
            public void onSuccess(Event data) {
                getFragmentListener().notifySubscriber(EventPreviewFragment.TITLE, data);
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, error);
            }
        });
    }

}
