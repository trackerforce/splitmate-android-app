package com.trackerforce.splitmate;

import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.ui.SplitmateActivity;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.Config;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends SplitmateActivity {

     public MainActivity() {
         super(R.layout.activity_main);
     }

     @Override
     protected void onCreateView() {
         Objects.requireNonNull(getSupportActionBar()).hide();
         setOnClickListener(R.id.buttonGoDashboard, this::initAPI);
         initAPI(getView());
     }

     private void initAPI(@Nullable View view) {
         getComponent(R.id.progressLoading, ProgressBar.class).setVisibility(View.VISIBLE);
         getButton(R.id.buttonGoDashboard).setVisibility(View.GONE);
         Config.getInstance().loadSettings(getBaseContext());

         TimerTask task = new TimerTask() {
             @Override
             public void run() {
                 initApp();
             }
         };

         userController.checkAPI(new ServiceCallback<String>() {
             @Override
             public void onSuccess(String data) {
                 Timer opening = new Timer();
                 opening.schedule(task, 2000);
             }

             @Override
             public void onError(String error) {
                 AppUtils.showMessage(MainActivity.this.getBaseContext(), error);
                 getComponent(R.id.progressLoading, ProgressBar.class).setVisibility(View.GONE);
                 getButton(R.id.buttonGoDashboard).setVisibility(View.VISIBLE);
             }
         });
     }

     /**
      * Initialize app
      *
      * Verifies if user already exist and is logged, then it will redirect to the Dashboard view.
      * Otherwise, it will redirect to login/sign up activity
      */
     private void initApp() {
         try {
             userController.syncToken();
             userController.getUser(new ServiceCallback<User>() {
                 @Override
                 public void onSuccess(User data) {
                     Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                     startActivity(intent);

                     finish();
                 }

                 @Override
                 public void onError(String error) {
                     Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                     startActivity(intent);

                     finish();
                 }

                 @Override
                 public void onError(String error, Object obj) {
                     AppUtils.showMessage(MainActivity.this.getBaseContext(), obj.toString());
                 }
             });
         } catch (Exception e) {
             AppUtils.showMessage(MainActivity.this.getBaseContext(), e.getMessage());
         }
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

 }