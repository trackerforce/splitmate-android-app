package com.trackerforce.splitmate.ui.item;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.model.ItemValue;
import com.trackerforce.splitmate.utils.AppUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemValuePreviewAdapter extends ListAdapter<ItemValue,
        ItemValuePreviewAdapter.ItemValueViewHolder> {

    private final List<ItemValue> localDataSet;

    public ItemValuePreviewAdapter() {
        super(new ItemValueDiff());

        this.localDataSet = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemValueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_value_preview, parent, false);

        return new ItemValueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemValueViewHolder viewHolder, int position) {
        viewHolder.bind(localDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void updateAdapter(ItemValue[] data) {
        if (data != null) {
            localDataSet.clear();
            localDataSet.addAll(Arrays.asList(data));

            notifyDataSetChanged();
        }
    }

    public List<ItemValue> getDataSet() {
        return this.localDataSet;
    }

    static class ItemValueDiff extends DiffUtil.ItemCallback<ItemValue> {

        @Override
        public boolean areItemsTheSame(@NonNull ItemValue oldItem, @NonNull ItemValue newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ItemValue oldItem, @NonNull ItemValue newItem) {
            return oldItem.equals(newItem);
        }
    }

    class ItemValueViewHolder extends RecyclerView.ViewHolder {

        private ItemValue itemValue;

        public ItemValueViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @SuppressLint("ClickableViewAccessibility")
        public void bind(ItemValue itemValue) {
            this.itemValue = itemValue;

            final FloatingActionButton btnRemove = itemView.findViewById(R.id.btnRemove);
            btnRemove.setOnClickListener(this::onRemove);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    itemView.getContext(),
                    android.R.layout.simple_list_item_1,
                    itemView.getResources().getStringArray(R.array.item_units));

            AutoCompleteTextView textType = itemView.findViewById(R.id.textType);
            textType.setAdapter(adapter);
            textType.setText(itemValue.getType());
            textType.setOnKeyListener(this::onUpdateType);
            textType.setOnItemClickListener((adapterView, view, i, l) ->
                    itemValue.setType(adapterView.getItemAtPosition(i).toString().trim()));
            textType.setOnTouchListener((view, motionEvent) -> {
                textType.showDropDown();
                return false;
            });

            EditText textValue = itemView.findViewById(R.id.textValue);
            textValue.setOnKeyListener(this::onUpdateValue);
            textValue.setText(itemValue.getValue());
        }

        private void onRemove(View view) {
            localDataSet.remove(this.itemValue);
            notifyDataSetChanged();
            AppUtils.hideKeyboard((Activity) view.getContext(), view);
        }

        private boolean onUpdateType(View view, int i, KeyEvent keyEvent) {
            itemValue.setType(((EditText) view).getText().toString().trim());
            return false;
        }

        private boolean onUpdateValue(View view, int i, KeyEvent keyEvent) {
            itemValue.setValue(((EditText) view).getText().toString().trim());
            return false;
        }

    }

}
