package com.trackerforce.splitmate.ui.item;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.model.Poll;
import com.trackerforce.splitmate.utils.AppUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class PollValuePreviewAdapter extends ListAdapter<Poll, PollValuePreviewAdapter.PollViewHolder> {

    private final List<Poll> localDataSet;

    public PollValuePreviewAdapter() {
        super(new PollDiff());

        this.localDataSet = new ArrayList<>();
    }

    @NonNull
    @Override
    public PollViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.poll_item_preview, parent, false);

        return new PollViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PollViewHolder viewHolder, int position) {
        viewHolder.bind(localDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(Poll[] data) {
        if (data != null) {
            localDataSet.clear();
            localDataSet.addAll(Arrays.asList(data));

            notifyDataSetChanged();
        }
    }

    public List<Poll> getDataSet() {
        return this.localDataSet;
    }

    static class PollDiff extends DiffUtil.ItemCallback<Poll> {

        @Override
        public boolean areItemsTheSame(@NonNull Poll oldItem, @NonNull Poll newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Poll oldItem, @NonNull Poll newItem) {
            return oldItem.equals(newItem);
        }
    }

    class PollViewHolder extends RecyclerView.ViewHolder implements TextWatcher {

        private Poll poll;

        public PollViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @SuppressLint("ClickableViewAccessibility")
        public void bind(Poll poll) {
            this.poll = poll;

            final FloatingActionButton btnRemove = itemView.findViewById(R.id.btnRemove);
            btnRemove.setOnClickListener(this::onRemove);

            EditText textValue = itemView.findViewById(R.id.textValue);
            textValue.addTextChangedListener(this);
            textValue.setText(poll.getValue());
            textValue.setEnabled(poll.getVotes() == null || poll.getVotes().length <= 0);
        }

        private void onRemove(View view) {
            OptionalInt indexOpt = IntStream.range(0, localDataSet.size())
                    .filter(i -> this.poll.getValue().equals(localDataSet.get(i).getValue()))
                    .findFirst();

            if (indexOpt.isPresent()) {
                localDataSet.remove(indexOpt.getAsInt());
                notifyItemRemoved(indexOpt.getAsInt());
            }

            AppUtils.hideKeyboard((Activity) view.getContext(), view);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            poll.setValue(charSequence.toString().trim());
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

}
