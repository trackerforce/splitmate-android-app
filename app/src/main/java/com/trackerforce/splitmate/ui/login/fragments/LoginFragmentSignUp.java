package com.trackerforce.splitmate.ui.login.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.trackerforce.splitmate.BuildConfig;
import com.trackerforce.splitmate.DashboardActivity;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.controller.user.UserController;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.ui.SplitmateView;
import com.trackerforce.splitmate.utils.AppUtils;

public class LoginFragmentSignUp extends Fragment implements SplitmateView, ServiceCallback<User> {

    private static final String TAG = LoginFragmentSignUp.class.getSimpleName();

    public static final String TITLE = "Sign Up";
    private final UserController userController;
    private ProgressDialog progressDialog;

    public LoginFragmentSignUp(UserController userController) {
        this.userController = userController;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnClickListener(R.id.buttonSignUp, this::onClickOnSignUp);
    }

    private void onClickOnSignUp(View view) {
        AppUtils.hideKeyboard(requireActivity(), requireView());

        SafetyNet.getClient(requireContext())
            .verifyWithRecaptcha(BuildConfig.GOOGLE_RECAPTCHA_KEY)
            .addOnSuccessListener(requireActivity(),
                response -> {
                    String userResponseToken = response.getTokenResult();
                    if (!userResponseToken.isEmpty()) {
                        onSignUp(userResponseToken);
                    }
                })
            .addOnFailureListener(requireActivity(), e -> {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    int statusCode = apiException.getStatusCode();
                    Log.d(TAG, "Error: " + CommonStatusCodes.getStatusCodeString(statusCode));
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            });
    }

    private void onSignUp(String token) {
        final String name = getTextViewValue(R.id.textName);
        final String username = getTextViewValue(R.id.textUsername);
        final String email = getTextViewValue(R.id.textEmail);
        final String password = getTextViewValue(R.id.textPassword);

        progressDialog = openLoading("Sign Up", "Creating account");
        userController.signUp(name, username, email, password, token, this);
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
        AppUtils.showMessage(requireView().getContext(), error);
    }
}