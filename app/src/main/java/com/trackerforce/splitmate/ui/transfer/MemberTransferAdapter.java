package com.trackerforce.splitmate.ui.transfer;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trackerforce.splitmate.R;
import com.trackerforce.splitmate.TransferActivity;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.Config;

import java.util.Arrays;
import java.util.List;

public class MemberTransferAdapter extends ListAdapter<User, MemberTransferAdapter.MemberViewHolder> {

    private final TransferActivity.TransferAdapterResult transferAdapterResult;
    private final List<User> localDataSet;

    public MemberTransferAdapter(List<User> members, TransferActivity.TransferAdapterResult transferAdapterResult) {
        super(new MemberTransferAdapter.MemberDiff());
        this.localDataSet = members;
        this.transferAdapterResult = transferAdapterResult;
    }

    @NonNull
    @Override
    public MemberTransferAdapter.MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.transfer_member_preview, parent, false);

        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberTransferAdapter.MemberViewHolder viewHolder, int position) {
        viewHolder.bind(localDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(User[] data) {
        localDataSet.clear();
        localDataSet.addAll(Arrays.asList(data));
        notifyDataSetChanged();
    }

    public List<User> getDataSet() {
        return this.localDataSet;
    }

    static class MemberDiff extends DiffUtil.ItemCallback<User> {

        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.equals(newItem);
        }
    }

    class MemberViewHolder extends RecyclerView.ViewHolder {

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(User member) {
            TextView textMemberName = itemView.findViewById(R.id.textMemberName);
            textMemberName.setText(member.getName());

            TextView textMemberUsername = itemView.findViewById(R.id.textMemberUsername);
            if (member.getUsername() == null || member.getUsername().isEmpty()) {
                textMemberUsername.setVisibility(View.INVISIBLE);
            } else {
                textMemberUsername.setText(String.format("@%s", member.getUsername()));
            }

            FloatingActionButton btnSelectMember = itemView.findViewById(R.id.btnSelectMember);
            btnSelectMember.setVisibility(isOrganizer(member) ? View.VISIBLE : View.INVISIBLE);
            AppUtils.setListener(btnSelectMember, view -> onSelect(member));
        }

        private void onSelect(User memberSelected) {
            transferAdapterResult.onSelectedMember(memberSelected);
        }

        private boolean isOrganizer(User member) {
            return !Config.getInstance().getLoggedUser().getUser().getId().equals(member.getId());
        }
    }

}
