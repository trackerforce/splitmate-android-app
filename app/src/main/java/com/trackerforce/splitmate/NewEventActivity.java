package com.trackerforce.splitmate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.Nullable;

import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.ui.SplitmateActivity;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.SplitConstants;

import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class NewEventActivity extends SplitmateActivity {

    private final Calendar calendar = Calendar.getInstance();
    private DatePickerDialog pickDate;
    private TimePickerDialog pickTime;

    private Event event;

    public NewEventActivity() {
        super(R.layout.activity_new_event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreateView() {
        setOnClickListener(R.id.buttonCreateEditEvent, this::onSubmitEvent);

        pickDate = new DatePickerDialog(this, this::onSetDate,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        pickTime = new TimePickerDialog(this, this::onSetTime,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

        getTextView(R.id.txtDate).setOnTouchListener(this::onLoadDate);
        getTextView(R.id.txtTime).setOnTouchListener(this::onLoadTime);
    }

    @Override
    protected void initActivityData() {
        event = (Event) getIntent().getSerializableExtra(SplitConstants.EVENT.toString());

        if (event == null) {
            event = new Event();
            event.setDate(new Date());
        } else {
            getTextView(R.id.txtEventName).setText(event.getName());
            getTextView(R.id.txtLocation).setText(event.getLocation());
            getTextView(R.id.txtEventDescription).setText(event.getDescription());
            getButton(R.id.buttonCreateEditEvent).setText(R.string.editEvent);
            getTextView(R.id.newEventTitle).setText(R.string.titleEditEvent);
        }

        getTextView(R.id.txtDate).setText(AppUtils.formatDate(event.getDate()));
        getTextView(R.id.txtTime).setText(AppUtils.formatTime(event.getDate()));
    }

    private boolean onLoadDate(View view, MotionEvent motionEvent) {
        if (event.getDate() != null) {
            calendar.setTime(event.getDate());
            pickDate.getDatePicker().updateDate(
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
        pickDate.show();
        return true;
    }

    private boolean onLoadTime(View view, MotionEvent motionEvent) {
        if (event.getDate() != null) {
            calendar.setTime(event.getDate());
            pickTime.updateTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        }
        pickTime.show();
        return true;
    }

    private void onSetDate(@Nullable DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        calendar.setTime(event.getDate() != null ? event.getDate() : calendar.getTime());
        calendar.set(year, monthOfYear, dayOfMonth);
        event.setDate(calendar.getTime());

        getTextView(R.id.txtDate).setText(String.format("%s/%s/%s", monthOfYear + 1, dayOfMonth, year));
    }

    private void onSetTime(@Nullable TimePicker timerPicker, int hourOfDay, int minute) {
        calendar.setTime(event.getDate() != null ? event.getDate() : calendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        event.setDate(calendar.getTime());

        getTextView(R.id.txtTime).setText(AppUtils.formatTime(event.getDate()));
    }

    private void onSubmitEvent(View view) {
        ProgressDialog progress = openLoading("Loading", "Applying changes");

        event.setName(getTextViewValue(R.id.txtEventName));
        event.setLocation(getTextViewValue(R.id.txtLocation));
        event.setDescription(getTextViewValue(R.id.txtEventDescription));

        if (event.getId() == null) {
            saveEvent(progress);
        } else {
            editEvent(progress);
        }
    }

    private void editEvent(ProgressDialog progress) {
        eventController.edit(event, new ServiceCallback<Event>() {
            @Override
            public void onSuccess(Event data) {
                progress.dismiss();
                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public void onError(String error) {
                progress.dismiss();
                AppUtils.showMessage(getBaseContext(), error);
            }

            @Override
            public void onError(String error, Object obj) {
                progress.dismiss();
                AppUtils.showMessage(getBaseContext(), getResources().getString(R.string.msgEventHasRemoved));
                Intent intent = new Intent(getView().getContext(), DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }, true);
    }

    private void saveEvent(ProgressDialog progress) {
        eventController.create(event, new ServiceCallback<Event>() {
            @Override
            public void onSuccess(Event data) {
                progress.dismiss();
                Intent intent = new Intent(NewEventActivity.this, EventDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(SplitConstants.EVENT_ID.toString(), data.getId());
                startActivity(intent);

                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public void onError(String error) {
                progress.dismiss();
                AppUtils.showMessage(getBaseContext(), error);
            }
        }, true);
    }

}