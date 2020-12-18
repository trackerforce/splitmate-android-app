package com.trackerforce.splitmate.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.trackerforce.splitmate.utils.AppUtils;

public interface SplitmateView {

    View getView();

    default <T> T getComponent(int id, Class<T> viewClass) {
        return getComponent(getView(), id, viewClass);
    }

    default <T> T getComponent(View view, int id, Class<T> viewClass) {
        T component = viewClass.cast(view.findViewById(id));
        assert component != null;
        return component;
    }

    default String getTextViewValue(int id) {
        return getComponent(id, TextView.class).getText().toString();
    }

    default Button getButton(int id) {
        return getComponent(id, Button.class);
    }

    default TextView getTextView(int id) {
        return getComponent(id, TextView.class);
    }

    default void setOnClickListener(int id, View.OnClickListener handler) {
        AppUtils.setListener(getComponent(id, Button.class), handler);
    }

    default void openConfirmDialog(String title, String message, SplitmateDialog callback) {
        new AlertDialog.Builder(getView().getContext())
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> callback.onAnswer(true))
                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> callback.onAnswer(false))
                .show();
    }

    default ProgressDialog openLoading(String title, String message) {
        final ProgressDialog progress = new ProgressDialog(getView().getContext());
        progress.setTitle(title);
        progress.setMessage(message);
        progress.setCancelable(false);
        progress.show();
        return progress;
    }
}