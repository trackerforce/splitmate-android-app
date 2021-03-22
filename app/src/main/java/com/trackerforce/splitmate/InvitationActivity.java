package com.trackerforce.splitmate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.EmailsDTO;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.ui.SplitmateActivity;
import com.trackerforce.splitmate.ui.event.MemberPreviewAdapter;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.SplitConstants;

import java.util.ArrayList;
import java.util.Map;

public class InvitationActivity extends SplitmateActivity {

    private MemberPreviewAdapter adapter;
    private String eventId;

    public InvitationActivity() {
        super(R.layout.activity_invitation);
    }

    @Override
    protected void onCreateView() {
        AppUtils.hideKeyboard(this, getView());
        
        adapter = new MemberPreviewAdapter(new ArrayList<>(), eventController);
        RecyclerView listViewMembers = getComponent(R.id.listViewMembers, RecyclerView.class);
        listViewMembers.setAdapter(adapter);
        listViewMembers.setLayoutManager(new LinearLayoutManager(this));

        setOnClickListener(R.id.btnAddMembers, this::onAddMember);
        setOnClickListener(R.id.btnSendInvite, this::onSendInvite);
    }

    @Override
    protected void initActivityData() {
        eventId = getIntent().getStringExtra(SplitConstants.EVENT_ID.toString());
    }

    private void onAddMember(@Nullable View view) {
        final String member = getTextViewValue(R.id.txtMember);

        if (member.startsWith("@")) {
            findEmailByUsername(member);
        } else {
            if (AppUtils.isValidEmail(member)) {
                addMemberToList(member, member);
            } else {
                AppUtils.showMessage(this, "Invalid email address");
            }
        }
    }

    private void findEmailByUsername(String member) {
        userController.find(member.substring(1), new ServiceCallback<User>() {
            @Override
            public void onSuccess(User data) {
                addMemberToList(member, data.getEmail());
            }

            @Override
            public void onError(String error) {
                AppUtils.showMessage(InvitationActivity.this, "User not found");
            }
        });
    }

    /**
     * Add member to list. If member already exist, it's ignored
     *
     * @param member Member searched (can be email or username). It will be displayed
     * @param email Member email (it won't be displayed)
     */
    private void addMemberToList(String member, String email) {
        User user = new User();
        user.setName(member);
        user.setEmail(email);

        for (User userAdapter : adapter.getDataSet()) {
            if (userAdapter.getName().equals(member)) {
                AppUtils.showMessage(this, "Already added");
                return;
            }
        }

        getTextView(R.id.txtMember).setText("");
        adapter.getDataSet().add(user);
        adapter.notifyDataSetChanged();
    }

    private void onSendInvite(@Nullable View view) {
        final EmailsDTO emailsDTO = new EmailsDTO();
        emailsDTO.setEmails(new String[adapter.getDataSet().size()]);

        int index = 0;
        for (User u : adapter.getDataSet())
            emailsDTO.getEmails()[index++] = u.getEmail();

        ProgressDialog progress = openLoading("Loading", "Sending invitations");
        eventController.inviteAll(eventId, emailsDTO, new ServiceCallback<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> data) {
                runOnUiThread(() -> {
                    progress.dismiss();
                    setResult(Activity.RESULT_OK);
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progress.dismiss();
                    AppUtils.showMessage(InvitationActivity.this, error);
                });
            }
        }, true);
    }
}