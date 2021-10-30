package com.trackerforce.splitmate.ui.event;

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
import com.trackerforce.splitmate.controller.ServiceCallback;
import com.trackerforce.splitmate.controller.event.EventController;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.model.User;
import com.trackerforce.splitmate.model.pusher.PusherMemberDTO;
import com.trackerforce.splitmate.pusher.PusherClient;
import com.trackerforce.splitmate.pusher.PusherEvents;
import com.trackerforce.splitmate.utils.AppUtils;
import com.trackerforce.splitmate.utils.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class MemberPreviewAdapter extends ListAdapter<User, MemberPreviewAdapter.MemberViewHolder> {

    private Event event;
    private final List<User> localDataSet;
    private final List<User> originalLocalData;
    private final EventController eventController;

    public MemberPreviewAdapter(List<User> members, EventController eventController) {
        super(new MemberPreviewAdapter.MemberDiff());
        this.localDataSet = members;
        this.originalLocalData = new ArrayList<>(members);
        this.eventController = eventController;
    }

    @NonNull
    @Override
    public MemberPreviewAdapter.MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.event_member_preview, parent, false);

        return new MemberPreviewAdapter.MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberPreviewAdapter.MemberViewHolder viewHolder, int position) {
        viewHolder.bind(localDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public List<User> getDataSet() {
        return this.localDataSet;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(User[] data) {
        localDataSet.clear();
        localDataSet.addAll(Arrays.asList(data));
        localDataSet.sort(Comparator.comparing(User::getName));

        originalLocalData.clear();
        originalLocalData.addAll(Arrays.asList(data));

        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String filter) {
        localDataSet.clear();
        if(filter.isEmpty()){
            localDataSet.addAll(originalLocalData);
        } else{
            final String query = filter.toLowerCase();
            for (User member : originalLocalData) {
                if (member.getName().toLowerCase().contains(query) ||
                        member.getUsername().toLowerCase().contains(query)) {
                    localDataSet.add(member);
                }
            }
        }
        notifyDataSetChanged();
    }

    private int getIndex(User data) {
        OptionalInt indexOpt = IntStream.range(0, localDataSet.size())
                .filter(i -> data.getId().equals(localDataSet.get(i).getId()))
                .findFirst();

        return indexOpt.isPresent() ? indexOpt.getAsInt() : -1;
    }

    private void onClickRemove(View convertView, User member) {
        if (event != null) {
            AppUtils.openConfirmDialog(convertView.getContext(),
                    convertView.getResources().getString(R.string.labelMembers),
                    String.format(convertView.getResources().getString(R.string.msgConfirmRemoveMember),
                            member.getName()), answer -> {
                    if (answer)
                        onRemoveFromEvent(convertView, member, getIndex(member));
            });
        } else {
            localDataSet.remove(member);
            notifyItemRemoved(getIndex(member));
        }
    }

    private void onRemoveFromEvent(View convertView, User member, int position) {
        eventController.removeMember(member.getId(), event.getId(), new ServiceCallback<>() {
            @Override
            public void onSuccess(Event data) {
                localDataSet.remove(member);
                notifyItemRemoved(position);

                final PusherClient pusher = PusherClient.getInstance();
                final PusherMemberDTO pusherMemberDTO =
                        new PusherMemberDTO(event.getId(), PusherEvents.REMOVE_MEMBER);
                pusherMemberDTO.setMemberId(member.getId());
                pusher.notifyEvent(pusherMemberDTO);
            }

            @Override
            public void onError(String error) {
                AppUtils.showMessage(convertView.getContext(), error);
            }
        }, true);
    }

    private boolean isOrganizer(User member) {
        return event == null ||
                (!Config.getInstance().getLoggedUser().getUser().getId().equals(member.getId()) &&
                Config.getInstance().getLoggedUser().getUser().getId().equals(event.getOrganizer()));
    }

    public void setEvent(Event event) {
        this.event = event;
        updateAdapter(event.getV_members());
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

            FloatingActionButton btnRemoveMember = itemView.findViewById(R.id.btnRemoveMember);
            btnRemoveMember.setVisibility(isOrganizer(member) ? View.VISIBLE : View.INVISIBLE);
            AppUtils.setListener(btnRemoveMember, view -> onClickRemove(itemView, member));
        }
    }

}
