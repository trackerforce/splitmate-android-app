package com.trackerforce.splitmate.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import com.trackerforce.splitmate.DashboardActivity;
import com.trackerforce.splitmate.LoginActivity;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.ui.SplitmateView;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.Config;

public class AccountFragment extends Fragment implements SplitmateView {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final User loggedUser = Config.getInstance().getLoggedUser().getUser();
        getTextView(R.id.txtName).setText(loggedUser.getName());
        getTextView(R.id.txtAcctUsername).setText(String.format("@%s", loggedUser.getUsername()));

        setOnClickListener(R.id.btnDeleteAccount, this::onClickDeleteAccount);
        setAccountPlan();
    }

    private void setAccountPlan() {
        getComponent(R.id.groupPlanPerk, Group.class).setVisibility(View.GONE);
        ((DashboardActivity) requireActivity()).getUserController().getUser(new ServiceCallback<User>() {
            @Override
            public void onSuccess(User data) {
                switch (data.getV_Plan().getName()) {
                    case "MEMBER":
                        getTextView(R.id.txtAccountType).setText(getResources().getString(R.string.labelAccountType_Member));
                        break;
                    case "FOUNDER":
                        getTextView(R.id.txtAccountType).setText(getResources().getString(R.string.labelAccountType_Founder));
                        getComponent(R.id.imgAvatar, ImageView.class)
                                .setBackgroundColor(getResources().getColor(R.color.gold, getContext().getTheme()));
                        break;
                }
                getComponent(R.id.groupPlanPerk, Group.class).setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String error) {
                AppUtils.showMessage(getContext(), error);
            }
        });
    }

    private void onClickDeleteAccount(@Nullable View view) {
        openConfirmDialog(getResources().getString(R.string.btnDeleteAccount),
                getResources().getString(R.string.msgConfirmDeleteAccount), answer -> {
            if (answer) {
                ((DashboardActivity) requireActivity()).getUserController().deleteAccount(new ServiceCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        requireActivity().finish();

                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        requireActivity().startActivity(intent);
                    }

                    @Override
                    public void onError(String error) {
                        AppUtils.showMessage(getContext(), error);
                    }
                }, true);
            }
        });
    }
}