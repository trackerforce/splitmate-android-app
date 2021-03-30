package com.trackerforce.splitmate.ui.login.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trackerforce.splitmate.DashboardActivity;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.controller.user.UserController;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.utils.AppUtils;

import com.trackerforce.splitmate.ui.SplitmateView;

public class LoginFragmentAuth extends Fragment implements SplitmateView, ServiceCallback<User> {

    public static final String TITLE = "Login";
    private final UserController userController;
    private ProgressDialog progressDialog;

    public LoginFragmentAuth(UserController userController) {
        this.userController = userController;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnClickListener(R.id.buttonLogin, this::onLogin);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment_auth, container, false);
    }

    private void onLogin(View view) {
        AppUtils.hideKeyboard(requireActivity(), requireView());

        final String login = getTextViewValue(R.id.textLogin);
        final String password = getTextViewValue(R.id.textPassword);

        progressDialog = openLoading("Authenticating", "Verifying credentials");
        userController.login(login, password, this);
    }

    @Override
    public void onSuccess(User data) {
        progressDialog.dismiss();
        AppUtils.showMessage(requireView().getContext(), String.format("Welcome %s", data.getName()));

        requireActivity().finish();
        Intent intent = new Intent(getContext(), DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onError(String error) {
        progressDialog.dismiss();
        AppUtils.showMessage(getContext(), error);
    }

}