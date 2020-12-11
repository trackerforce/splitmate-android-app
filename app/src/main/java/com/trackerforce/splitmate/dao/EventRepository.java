package com.trackerforce.splitmate.dao;

import android.content.Context;

import com.trackerforce.splitmate.dao.wrapper.EventWrapper;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.model.Item;
import com.trackerforce.splitmate.model.User;

import java.util.ArrayList;
import java.util.List;

public class EventRepository extends AbstractRepository<Event> {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public EventRepository(Context context) {
        super(context, new EventWrapper());
        userRepository = new UserRepository(context);
        itemRepository = new ItemRepository(context, userRepository);
    }

    @Override
    public void update(String id, Event entity) throws Exception {
        super.update(id, entity);

        //update Members
        if (entity.getV_members() != null) {
            userRepository.deleteByEventId(id);
            for (User member : entity.getV_members()) {
                member.setEventId(entity.getId());
                userRepository.save(member);
            }
        }

        //update Items
        if (entity.getItems() != null) {
            itemRepository.deleteByEventId(id);
            for (Item item : entity.getItems()) {
                item.setEventId(entity.getId());
                itemRepository.save(item);
            }
        }
    }

    public Event findById(String id, boolean resolve) throws Exception {
        if (resolve)
            return findById(id, entity -> {
                if (entity != null) {
                    final List<Item> items = itemRepository.findByEventId(id);
                    entity.setItems(items.toArray(new Item[0]));

                    final List<User> members = userRepository.findByEventId(id);
                    entity.setV_members(members.toArray(new User[0]));

                    User organizer = userRepository.findById(entity.getOrganizer());
                    if (organizer == null) organizer = new User();

                    entity.setV_organizer(organizer);
                }
                return entity;
            });
        else
            return findById(id);
    }

    public List<Event> findByCategory(String category) throws Exception {
        return new ArrayList<>(find("category = ?", new String[] { category }));
    }

    @Override
    public void deleteById(String id) throws Exception {
        super.deleteById(id);
        userRepository.deleteByEventId(id);
        itemRepository.deleteByEventId(id);
    }

    public ItemRepository getItemRepository() {
        return itemRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

}
