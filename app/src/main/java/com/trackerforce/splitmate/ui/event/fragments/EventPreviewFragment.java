package com.trackerforce.splitmate.ui.event.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.model.pusher.PusherData;
import com.trackerforce.splitmate.model.pusher.PusherMemberDTO;
import com.trackerforce.splitmate.pusher.PusherClient;
import com.trackerforce.splitmate.pusher.PusherEvents;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.Config;

import java.text.DecimalFormat;

public class EventPreviewFragment extends AbstractEventFragment {

    private final DecimalFormat df = new DecimalFormat("'$'0.##");
    private float totalCost;
    private PusherClient pusher;

    public EventPreviewFragment() {
        super(R.layout.event_fragment_preview);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getComponent(R.id.txtQtMembers, EditText.class).setOnKeyListener(this::onChangeMember);

        pusher = PusherClient.getInstance();
        pusher.connect(getContext(), getEventId());
        pusher.subscribe(PusherEvents.UNPICK_ITEM.toString(), this::onUpdatePreview);
        pusher.subscribe(PusherEvents.PICK_ITEM.toString(), this::onUpdatePreview);
        pusher.subscribe(PusherEvents.DELETE_ITEM.toString(), this::onUpdatePreview);
        pusher.subscribe(PusherEvents.EDIT_ITEM.toString(), this::onUpdatePreview);
        pusher.subscribe(PusherEvents.CREATE_ITEM.toString(), this::onUpdatePreview);
        pusher.subscribe(PusherEvents.REMOVE_MEMBER.toString(), this::onMemberRemoved);
    }

    @Override
    public void onRefresh(@Nullable View view) {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        pusher.unsubscribe(PusherEvents.UNPICK_ITEM.toString());
        pusher.unsubscribe(PusherEvents.PICK_ITEM.toString());
        pusher.unsubscribe(PusherEvents.DELETE_ITEM.toString());
        pusher.unsubscribe(PusherEvents.EDIT_ITEM.toString());
        pusher.unsubscribe(PusherEvents.CREATE_ITEM.toString());
        pusher.unsubscribe(PusherEvents.REMOVE_MEMBER.toString());
    }

    public void loadPreview(Event event) {
        totalCost = event.getTotalCost();

        int members = Integer.parseInt(String.valueOf(event.getMembers() != null ? event.getMembers().length : "0"));
        float individualCost = members > 0 ? totalCost / members : 0f;
        int votingPolls = event.getVotingPolls();
        int pendingItems = event.getPendingItems();

        getTextView(R.id.txtTotalCost).setText(String.format("%s", df.format(totalCost)));
        getTextView(R.id.txtIndividualCost).setText(String.format("%s", df.format(individualCost)));
        getTextView(R.id.txtVoltingPolls).setText(String.valueOf(votingPolls));
        getTextView(R.id.txtItemsPending).setText(String.valueOf(pendingItems));
        getTextView(R.id.txtItems).setText(String.valueOf(event.getItems().length));
        getTextView(R.id.txtQtMembers).setText(String.valueOf(members));
    }

    private boolean onChangeMember(View view, int i, KeyEvent keyEvent) {
        final String value = ((EditText) view).getText().toString().trim();
        int members = Integer.parseInt(value.isEmpty() || value.equals("0") ? "1" : value);
        float individualCost = totalCost / members;
        getTextView(R.id.txtIndividualCost).setText(String.format("%s", df.format(individualCost)));
        return false;
    }

    private void onUpdatePreview(Object... args) {
        requireActivity().runOnUiThread(() ->
                getEventController().getEventById(getEventId(), new ServiceCallback<Event>() {
            @Override
            public void onSuccess(Event data) {
                setEvent(data);
                loadPreview(data);
            }

            @Override
            public void onError(String error) {
                AppUtils.showMessage(getContext(), error);
            }
        }, true));
    }

    private void onMemberRemoved(Object... args) {
        requireActivity().runOnUiThread(() -> {
            PusherMemberDTO pusherMemberDTO = PusherData.getDTO(PusherMemberDTO.class, args);

            if (pusherMemberDTO.getMemberId().equals(Config.getInstance().getLoggedUser().getUser().getId())) {
                getEventController().deleteLocal(getEventId());
                requireActivity().finish();
            } else {
                onUpdatePreview(args);
            }
        });
    }

}
