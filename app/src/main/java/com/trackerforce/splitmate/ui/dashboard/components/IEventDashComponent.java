package com.trackerforce.splitmate.ui.dashboard.components;

import android.view.View;

import com.trackerforce.splitmate.model.Event;

public interface IEventDashComponent {

    /**
     * It defines the behavior of the top button
     */
    void onButtonTop(View convertView, Event event);

    /**
     * It defines the behavior of the bottom button
     */
    void onButtonBottom(View convertView, Event event);

    /**
     * @return The item layout for the component
     */
    int getItemLayout();


    /**
     * @return The layout Activity/Fragment that the item is displayed
     */
    int getLayout();

}
