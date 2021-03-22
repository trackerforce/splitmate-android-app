package com.trackerforce.splitmate.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.trackerforce.splitmate.ui.SplitmateDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class AppUtils {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})$");

    /**
     * Format date to yyyy/MM/dd
     */
    public static String formatDate(Date date) {
        @SuppressLint("SimpleDateFormat")
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(date);
    }

    /**
     * Format time to hh:mm aa"
     */
    public static String formatTime(Date date) {
        @SuppressLint("SimpleDateFormat")
        final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        return dateFormat.format(date);
    }

    public static void showMessage(Context context, String message) {
        if (context != null) {
            ((Activity) context).runOnUiThread(() -> {
                Toast dialog = Toast.makeText(context, message, Toast.LENGTH_LONG);
                dialog.setGravity(Gravity.BOTTOM, 0, 200);
                dialog.show();
            });
        }
    }

    public static void openConfirmDialog(Context context, String title, String message,
                                         SplitmateDialog callback) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> callback.onAnswer(true))
                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> callback.onAnswer(false))
                .show();
    }

    public static boolean isOnline(Context context, boolean force) {
        boolean isToggleOffline = Config.getInstance().getSettings(
                context, SplitConstants.TOGGLE_OFFLINE, false);

        if (!isToggleOffline || force) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        }
        return false;
    }

    public static String getJsonValue(okhttp3.ResponseBody json, String field) {
        try {
            return new JSONObject(json.string()).getString(field);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getJsonError(okhttp3.ResponseBody json, String field) {
        try {
            return new JSONObject(json.string()).getJSONArray("errors")
                    .getJSONObject(0).getString(field);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void hideKeyboard(Activity activity, View view) {
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static String getString(Context context, int resourceId) {
        return context.getResources().getString(resourceId);
    }

    public static void setListener(View view, View.OnClickListener listener) {
        view.setOnClickListener(new SplitOnClickListener() {
            @Override
            protected void onClicking(View view) {
                listener.onClick(view);
            }
        });
    }
}