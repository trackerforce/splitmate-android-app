package com.trackerforce.splitmate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.ErrorResponse;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.model.Item;
import com.trackerforce.splitmate.model.ItemValue;
import com.trackerforce.splitmate.model.Poll;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.model.pusher.PusherItemDTO;
import com.trackerforce.splitmate.pusher.PusherClient;
import com.trackerforce.splitmate.pusher.PusherEvents;
import com.trackerforce.splitmate.utils.Config;
import com.trackerforce.splitmate.ui.SplitmateActivity;
import com.trackerforce.splitmate.ui.item.ItemValuePreviewAdapter;
import com.trackerforce.splitmate.ui.item.PollValuePreviewAdapter;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.SplitConstants;

import java.util.Arrays;

public class NewItemActivity extends SplitmateActivity {

    public final int MAX_ITEM_VALUE = 5;
    private final ItemValuePreviewAdapter adapter;
    private final PollValuePreviewAdapter pollAdapter;
    private Item item;
    private String organizerId;
    private String eventId;

    public NewItemActivity() {
        super(R.layout.activity_new_item);
        adapter = new ItemValuePreviewAdapter();
        pollAdapter = new PollValuePreviewAdapter();
    }

    @Override
    protected void onCreateView() {
        RecyclerView listView = getComponent(R.id.listValues, RecyclerView.class);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView pollListView = getComponent(R.id.listPoll, RecyclerView.class);
        pollListView.setAdapter(pollAdapter);
        pollListView.setLayoutManager(new LinearLayoutManager(this));

        setOnClickListener(R.id.buttonCreateEditItem, this::onSubmitItem);
        setOnClickListener(R.id.buttonRemoveItem, this::onRemoveItem);
        getTextView(R.id.txtPollName).setOnKeyListener(this::onUpdateValue);

        getComponent(R.id.btnAddDetail, FloatingActionButton.class)
                .setOnClickListener(this::onAddItemValue);

        getComponent(R.id.btnAddPollValue, FloatingActionButton.class)
                .setOnClickListener(this::onAddPollItem);

        AppUtils.hideKeyboard(this, getView());
    }

    @Override
    protected void initActivityData() {
        item = (Item) getIntent().getSerializableExtra(SplitConstants.ITEM.toString());
        organizerId = getIntent().getStringExtra(SplitConstants.ORGANIZER.toString());
        eventId = getIntent().getStringExtra(SplitConstants.EVENT_ID.toString());

        if (item == null) {
            item = new Item();
            getComponent(R.id.linearItemEditorPanel, LinearLayout.class).setVisibility(View.GONE);
        } else {
            adapter.updateAdapter(item.getDetails());
            pollAdapter.updateAdapter(item.getPoll());

            setActivityTitle();
            getTextView(R.id.txtItemName).setText(item.getName());
            getTextView(R.id.txtItemCreator).setText(item.getV_created_by().getName());
            getTextView(R.id.txtPollName).setText(item.getPoll_name());
            getButton(R.id.buttonCreateEditItem).setText(R.string.editItem);
            getButton(R.id.buttonRemoveItem).setVisibility(isItemCreatorOrOrganizer() ? View.VISIBLE : View.GONE);
        }
    }

    private void setActivityTitle() {
        eventController.getLocalEventById(eventId, new ServiceCallback<>() {
            @Override
            public void onSuccess(Event data) {
                getTextView(R.id.newItemTitle).setText(data.getName());
            }

            @Override
            public void onError(String error) {
                getTextView(R.id.newItemTitle).setText(getResources().getString(R.string.titleNewItem));
            }
        });
    }

    private void onSubmitItem(@Nullable View view) {
        adapter.getDataSet().removeIf(item -> item.getValue().isEmpty() || item.getType().isEmpty());
        item.setDetails(adapter.getDataSet().toArray(new ItemValue[0]));

        pollAdapter.getDataSet().removeIf(item -> item.getValue().isEmpty());
        item.setPoll(pollAdapter.getDataSet().toArray(new Poll[0]));

        item.setName(getTextViewValue(R.id.txtItemName));

        AppUtils.hideKeyboard(this, getView());
        if (item.getId() == null) {
            saveItem();
        } else {
            editItem();
        }
    }

    private void onRemoveItem(@Nullable View view) {
        eventController.deleteItem(eventId, item, new ServiceCallback<>() {
            @Override
            public void onSuccess(Event data) {
                setResult(Activity.RESULT_OK, new Intent().putExtra(SplitConstants.EVENT.toString(), data));
                finish();
                notifyPusher(item.getId(), PusherEvents.DELETE_ITEM);
            }

            @Override
            public void onError(String error) {
                AppUtils.showMessage(NewItemActivity.this, error);
            }

            @Override
            public void onError(String error, Object obj) {
                AppUtils.showMessage(NewItemActivity.this, "Refreshing Event");
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        }, true);
    }

    private void onAddItemValue(View view) {
        if (adapter.getDataSet().size() < MAX_ITEM_VALUE) {
            adapter.getDataSet().add(new ItemValue());
            adapter.notifyItemInserted(adapter.getItemCount());
            AppUtils.hideKeyboard(this, view);
        } else {
            AppUtils.showMessage(this, getResources().getString(R.string.msgLimitReached));
        }
    }

    private boolean onUpdateValue(View view, int i, KeyEvent keyEvent) {
        item.setPoll_name(((EditText) view).getText().toString().trim());
        return false;
    }

    private void onAddPollItem(View view) {
        if (pollAdapter.getDataSet().size() < MAX_ITEM_VALUE) {
            pollAdapter.getDataSet().add(new Poll());
            pollAdapter.notifyItemInserted(pollAdapter.getItemCount());
            AppUtils.hideKeyboard(this, view);
        } else {
            AppUtils.showMessage(this, getResources().getString(R.string.msgLimitReached));
        }
    }

    private boolean isItemCreatorOrOrganizer() {
        final User user = Config.getInstance().getLoggedUser().getUser();
        return item.getCreated_by().equals(user.getId()) || user.getId().equals(organizerId);
    }

    private void editItem() {
        ProgressDialog progress = openLoading("Item", "Updating...");
        eventController.editItem(eventId, item, new ServiceCallback<>() {
            @Override
            public void onSuccess(Event data) {
                progress.dismiss();
                setResult(Activity.RESULT_OK, new Intent().putExtra(SplitConstants.EVENT.toString(), data));
                finish();
                notifyPusher(item.getId(), PusherEvents.EDIT_ITEM);
            }

            @Override
            public void onError(String error) {
                progress.dismiss();
                AppUtils.showMessage(NewItemActivity.this, error);
            }

            @Override
            public void onError(String error, Object obj) {
                progress.dismiss();
                final ErrorResponse errorResponse = (ErrorResponse) obj;
                if ("event".equals(errorResponse.getDocument())) {
                    AppUtils.showMessage(NewItemActivity.this, getResources().getString(R.string.msgEventHasRemoved));
                    Intent intent = new Intent(getView().getContext(), DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    AppUtils.showMessage(NewItemActivity.this, getResources().getString(R.string.msgItemHasRemoved));
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            }
        }, true);
    }

    private void saveItem() {
        ProgressDialog progress = openLoading("Item", "Saving...");
        eventController.addItem(eventId, item, new ServiceCallback<>() {
            @Override
            public void onSuccess(Event data) {
                progress.dismiss();
                setResult(Activity.RESULT_OK, new Intent().putExtra(SplitConstants.EVENT.toString(), data));
                finish();

                final Item newItem = Arrays.stream(data.getItems())
                        .filter(i -> i.getName().equals(item.getName()))
                        .findFirst()
                        .orElse(item);

                notifyPusher(newItem.getId(), PusherEvents.CREATE_ITEM);
            }

            @Override
            public void onError(String error) {
                progress.dismiss();
                AppUtils.showMessage(NewItemActivity.this, error);
            }

            @Override
            public void onError(String error, Object obj) {
                progress.dismiss();
                final ErrorResponse errorResponse = (ErrorResponse) obj;
                if ("event".equals(errorResponse.getDocument())) {
                    AppUtils.showMessage(NewItemActivity.this, "Event has been removed");
                    Intent intent = new Intent(getView().getContext(), DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    AppUtils.showMessage(NewItemActivity.this, "Item has been removed");
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            }
        }, true);
    }

    private void notifyPusher(String itemId, PusherEvents pusherEvent) {
        final PusherClient pusher = PusherClient.getInstance();
        final PusherItemDTO pusherItemDTO = new PusherItemDTO(eventId, pusherEvent);
        pusherItemDTO.setItemId(itemId);
        pusher.notifyEvent(pusherItemDTO);
    }

}