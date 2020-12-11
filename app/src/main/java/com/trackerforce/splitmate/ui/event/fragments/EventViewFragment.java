package com.trackerforce.splitmate.ui.event.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.FragmentManager;

import com.trackerforce.splitmate.NewEventActivity;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.TransferActivity;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.utils.Config;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.SplitConstants;

import java.text.SimpleDateFormat;

public class EventViewFragment extends AbstractEventFragment {

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("E, dd MMM yyyy hh:mm aa");

    public EventViewFragment() {
        super(R.layout.event_fragment_view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setOnClickListener(R.id.btnRefresh, v -> loadEvent(true));
        setOnClickListener(R.id.btnEditEvent, this::onEdit);
    }

    @Override
    public void onRefresh(@Nullable View view) {
        loadEvent(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SplitConstants.TRANSFER.ordinal() && resultCode == Activity.RESULT_OK) {
            onRefresh(getView());
        }
    }

    private void loadEvent(boolean force) {
        if (getView() != null) {
            getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.VISIBLE);
            getComponent(R.id.linearLayout, LinearLayout.class).setVisibility(View.GONE);

            getEventController().getEventById(getEventId(), new ServiceCallback<Event>() {
                @Override
                public void onSuccess(Event data) {
                    setEvent(data);

                    loadViewFieldValues();
                    loadPreviewFragment(data);
                    getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.GONE);
                    getComponent(R.id.groupControls, Group.class).setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(String error) {
                    AppUtils.showMessage(getContext(), error);
                    getComponent(R.id.progressBar, ProgressBar.class).setVisibility(View.GONE);
                }
            }, force);
        }
    }

    private void loadPreviewFragment(Event event) {
        FragmentManager fm = getChildFragmentManager();
        EventPreviewFragment eventPreviewFragment = (EventPreviewFragment) fm.findFragmentById(R.id.fragmentPreview);
        assert eventPreviewFragment != null;
        eventPreviewFragment.loadPreview(event);
    }

    private void onEdit(@Nullable View view) {
        Intent intent = new Intent(requireView().getContext(), NewEventActivity.class);
        intent.putExtra(SplitConstants.EVENT.toString(), getEvent());
        startActivityForResult(intent, SplitConstants.EVENT.ordinal());
    }

    private void onLeave(@Nullable View view) {
        openConfirmDialog("Leave event", String.format("Do you want to leave %s",
            getEvent().getName()), answer -> {
                if (answer) {
                    getUserController().leaveEvent(getEventId(), new ServiceCallback<User>() {
                        @Override
                        public void onSuccess(User data) {
                            requireActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        }

                        @Override
                        public void onError(String error) {
                            AppUtils.showMessage(getContext(), error);
                        }
                    }, true);
                }
            });
    }

    private void onDelete(@Nullable View view) {
        openConfirmDialog("Delete event", String.format("Do you want to delete %s",
                getEvent().getName()), answer -> {
                if (answer) {
                    getEventController().delete(getEventId(), new ServiceCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            requireActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        }

                        @Override
                        public void onError(String error) {
                            AppUtils.showMessage(getContext(), error);
                        }
                    }, true);
                }
            });
    }

    private void onTransfer(@Nullable View view) {
        Intent intent = new Intent(getContext(), TransferActivity.class);
        intent.putExtra(SplitConstants.EVENT.toString(), getEvent());
        startActivityForResult(intent, SplitConstants.TRANSFER.ordinal());
    }

    private void loadViewFieldValues() {
        if (getView() != null) {
            final Event event = getEvent();
            TextView title = requireActivity().findViewById(R.id.eventDetailTitle);
            title.setText(getEvent().getName());

            getTextView(R.id.textEventDetailName).setText(event.getName());
            getTextView(R.id.textEventDetailLocation).setText(event.getLocation());
            getTextView(R.id.textEventDetailNotes).setText(event.getDescription());
            getTextView(R.id.textEventDetailWhen).setText(simpleDateFormat.format(event.getDate()));
            getTextView(R.id.textEventDetailOrganizedBy).setText(event.getV_organizer().getName());

            if (isOrganizer()) {
                getButton(R.id.btnTransferAdmin).setVisibility(View.VISIBLE);
                setOnClickListener(R.id.btnTransferAdmin, this::onTransfer);
                setOnClickListener(R.id.btnLeaveDelete, this::onDelete);
            } else {
                getButton(R.id.btnTransferAdmin).setVisibility(View.GONE);
                setOnClickListener(R.id.btnLeaveDelete, this::onLeave);
            }
        }

        getComponent(R.id.linearLayout, LinearLayout.class).setVisibility(View.VISIBLE);
    }

    private boolean isOrganizer() {
        return getEvent().getOrganizer().equals(Config.getInstance().getLoggedUser().getUser().getId());
    }

}
