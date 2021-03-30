package com.trackerforce.splitmate;

import android.app.Activity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.ui.SplitmateActivity;
import com.trackerforce.splitmate.ui.transfer.MemberTransferAdapter;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.SplitConstants;

import java.util.ArrayList;

public class TransferActivity extends SplitmateActivity implements ServiceCallback<Event> {

    private String eventId;
    private MemberTransferAdapter adapter;

    public TransferActivity() {
        super(R.layout.activity_transfer);
    }

    @Override
    protected void onCreateView() {
        setOnClickListener(R.id.btnCancel, this::onCancel);

        adapter = new MemberTransferAdapter(new ArrayList<>(), new TransferAdapterResult());
        RecyclerView listView = getComponent(R.id.listMembers, RecyclerView.class);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void initActivityData() {
        eventId = getIntent().getStringExtra(SplitConstants.EVENT_ID.toString());
        eventController.getEventById(eventId, this, true);
    }

    @Override
    public void onSuccess(Event data) {
        adapter.updateAdapter(data.getV_members());
    }

    @Override
    public void onError(String error) {
        AppUtils.showMessage(TransferActivity.this, error);
        finish();
    }

    private void onCancel(@Nullable View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public class TransferAdapterResult implements ServiceCallback<Event> {

        private User member;

        public void onSelectedMember(User member) {
            this.member = member;
            getEventController().transfer(eventId, member.getId(), this, true);
        }

        @Override
        public void onSuccess(Event data) {
            AppUtils.showMessage(TransferActivity.this,
                    String.format(
                            getResources().getString(R.string.msgSuccessTransferEvent),
                            member.getName()));

            setResult(Activity.RESULT_OK);
            finish();
        }

        @Override
        public void onError(String error) {
            AppUtils.showMessage(TransferActivity.this, error);
        }
    }
}