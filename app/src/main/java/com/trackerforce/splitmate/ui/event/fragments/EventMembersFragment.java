package com.trackerforce.splitmate.ui.event.fragments;

import android.content.Intent;
import android.os.Bundle;
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

import com.trackerforce.splitmate.InvitationActivity;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.model.pusher.PusherData;
import com.trackerforce.splitmate.model.pusher.PusherMemberDTO;
import com.trackerforce.splitmate.pusher.PusherClient;
import com.trackerforce.splitmate.pusher.PusherEvents;
import com.trackerforce.splitmate.ui.event.MemberPreviewAdapter;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.Config;
import com.trackerforce.splitmate.utils.SplitConstants;

import java.util.ArrayList;

public class EventMembersFragment extends AbstractEventFragment {

    private SwipeRefreshLayout swipeContainer;
    private MemberPreviewAdapter adapter;
    private TextView textFilterMembers;
    private PusherClient pusher;

    public EventMembersFragment() {
        super(R.layout.event_fragment_members);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getTextView(R.id.txtSwipeRefresh).setVisibility(
                Config.getInstance().isEconomicEnabled(getContext()) ? View.VISIBLE : View.GONE);

        swipeContainer = getComponent(R.id.swipeContainer, SwipeRefreshLayout.class);
        swipeContainer.setOnRefreshListener(this::onRefreshLayout);

        adapter = new MemberPreviewAdapter(new ArrayList<>(), getEventController());
        RecyclerView listView = getComponent(R.id.listMembers, RecyclerView.class);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));

        textFilterMembers = getComponent(R.id.textFilterMembers, EditText.class);
        textFilterMembers.setOnKeyListener(this::onFilter);

        setOnClickListener(R.id.btnAddMember, this::onAddMember);
        setOnClickListener(R.id.btnToggleMemberFilter, this::onToggleFilter);

        pusher = PusherClient.getInstance();
        pusher.connect(getContext(), getEventId());
        pusher.subscribe(PusherEvents.REMOVE_MEMBER.toString(), this::onMemberRemoved);
    }

    @Override
    public void onRefresh(@Nullable View view) {
        loadEvent(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pusher.unsubscribe(PusherEvents.REMOVE_MEMBER.toString());
    }

    private void loadEvent(boolean force) {
        if (getView() != null) {
            getComponent(R.id.listMembers, RecyclerView.class).setVisibility(View.INVISIBLE);
            getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.VISIBLE);

            if (bypassOnLoad) {
                updateAdapter(getEvent());
                bypassOnLoad = false;
            } else {
                getEventController().getEventById(getEventId(), new ServiceCallback<Event>() {
                    @Override
                    public void onSuccess(Event data) {
                        requireActivity().runOnUiThread(() -> updateAdapter(data));
                    }

                    @Override
                    public void onError(String error) {
                        requireActivity().runOnUiThread(() -> {
                            AppUtils.showMessage(getContext(), error);
                            getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.GONE);
                            getComponent(R.id.listMembers, RecyclerView.class).setVisibility(View.VISIBLE);
                        });
                    }
                }, force);
            }
        }
    }

    private void updateAdapter(Event event) {
        setEvent(event);

        if (event.getMembers().length > 0)
            getTextView(R.id.txtSwipeRefresh).setVisibility(View.GONE);

        if (adapter != null) {
            adapter.setEvent(event);

            getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.GONE);
            getComponent(R.id.listMembers, RecyclerView.class).setVisibility(View.VISIBLE);
        }
    }

    private void onRefreshLayout() {
        loadEvent(true);
        swipeContainer.setRefreshing(false);
    }

    private void onAddMember(@Nullable View view) {
        Intent intent = new Intent(getActivity(), InvitationActivity.class);
        intent.putExtra(SplitConstants.EVENT_ID.toString(), getEventId());
        startActivityForResult(intent, SplitConstants.MEMBER.ordinal());
    }

    private boolean onFilter(@Nullable View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_UP && adapter.getDataSet() != null) {
            adapter.filter(textFilterMembers.getText().toString());
        }
        return false;
    }

    private void onToggleFilter(@Nullable View view) {
        if (textFilterMembers.getVisibility() == View.GONE) {
            textFilterMembers.setVisibility(View.VISIBLE);
        } else {
            textFilterMembers.setVisibility(View.GONE);
        }
    }

    private void onMemberRemoved(Object... args) {
        requireActivity().runOnUiThread(() -> {
            PusherMemberDTO pusherMemberDTO = PusherData.getDTO(PusherMemberDTO.class, args);

            getEventController().removeMemberLocal(pusherMemberDTO.getChannel(), pusherMemberDTO.getMemberId());
            adapter.getDataSet().removeIf(member -> member.getId().equals(pusherMemberDTO.getMemberId()));
            adapter.notifyDataSetChanged();
        });
    }

}
