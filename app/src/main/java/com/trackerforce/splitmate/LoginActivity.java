package com.trackerforce.splitmate;

import android.view.Menu;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.trackerforce.splitmate.ui.SplitmateActivity;
import com.trackerforce.splitmate.ui.login.LoginPageAdapter;
import com.trackerforce.splitmate.ui.login.fragments.LoginFragmentAuth;
import com.trackerforce.splitmate.ui.login.fragments.LoginFragmentSignUp;

public class LoginActivity extends SplitmateActivity {

    public LoginActivity() {
        super(R.layout.activity_login);
    }

    @Override
    protected void onCreateView() {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewLogin = findViewById(R.id.viewPagerLogin);

        viewLogin.setAdapter(new LoginPageAdapter(this, userController));
        new TabLayoutMediator(tabLayout, viewLogin,
                (tab, position) -> {
                    if (position == 0)
                        tab.setText(LoginFragmentAuth.TITLE);
                    else
                        tab.setText(LoginFragmentSignUp.TITLE);
                }
        ).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}