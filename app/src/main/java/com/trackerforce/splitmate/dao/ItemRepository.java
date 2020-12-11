package com.trackerforce.splitmate.dao;

import android.content.Context;
import com.trackerforce.splitmate.dao.wrapper.EntityResolver;
import com.trackerforce.splitmate.dao.wrapper.ItemWrapper;
import com.trackerforce.splitmate.model.Item;
import com.trackerforce.splitmate.model.User;

import java.util.ArrayList;
import java.util.List;

public class ItemRepository extends AbstractRepository<Item> implements EntityResolver<Item> {

    private final UserRepository userRepository;

    public ItemRepository(Context context, UserRepository userRepository) {
        super(context, new ItemWrapper());
        this.userRepository = userRepository;
    }

    @Override
    public Item findById(String id) throws Exception {
        return super.findById(id, this);
    }

    public List<Item> findByEventId(String eventId) throws Exception {
        return new ArrayList<>(find("eventId = ?", new String[]{ eventId }, this));
    }

    public void deleteByEventId(String eventId) throws Exception {
        delete("eventId = ?", new String[]{ eventId });
    }

    @Override
    public Item resolve(Item entity) throws Exception {
        //resolve User Creator
        if (entity.getCreated_by() != null) {
            final User userCreator = userRepository.findById(entity.getCreated_by());
            entity.setV_created_by(userCreator);
        }

        //resolve User assignee
        if (entity.getAssigned_to() != null) {
            final User userAssignee = userRepository.findById(entity.getAssigned_to());
            entity.setV_assigned_to(userAssignee);
        }

        return entity;
    }
}
