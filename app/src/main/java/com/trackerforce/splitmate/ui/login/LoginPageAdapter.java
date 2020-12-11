package com.trackerforce.splitmate.ui.login;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.trackerforce.splitmate.controller.user.UserController;
import com.trackerforce.splitmate.ui.login.fragments.LoginFragmentAuth;
import com.trackerforce.splitmate.ui.login.fragments.LoginFragmentSignUp;

public class LoginPageAdapter extends FragmentStateAdapter {

    private final UserController userController;

    public LoginPageAdapter(FragmentActivity fa, UserController userController) {
        super(fa);
        this.userController = userController;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new LoginFragmentAuth(userController);
            case 1:
                return new LoginFragmentSignUp(userController);
        }

        return new LoginFragmentAuth(userController);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}