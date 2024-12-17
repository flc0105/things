package flc.things.service;

import flc.things.entity.Category;
import flc.things.entity.Item;
import flc.things.mapper.AttachmentMapper;
import flc.things.mapper.CategoryMapper;
import flc.things.mapper.ItemMapper;
import flc.things.mapper.TimelineEventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService extends BaseService<Item> {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TimelineEventMapper timelineEventMapper;

    @Autowired
    private TimelineEventService timelineEventService;

//    @Autowired
//    private AttachmentService attachmentService;

    public ItemService(ItemMapper itemMapper) {
        super(itemMapper);
    }


    public void populateCategory(Item item) {
        if (item.getCategoryId() != null) {
            Category category = categoryMapper.selectById(item.getCategoryId());
            item.setCategory(category);
        }
    }

    public List<Item> getAllItems() {
        List<Item> items = getAll();
        items.forEach(Item::calculateOwnershipDuration);
        items.forEach(this::populateCategory);
//        items.forEach((item -> {
//            item.setAttachment(attachmentService.getAttachmentById(item.getAttachmentId()));
//        }));
        items.forEach((item -> {
            item.setTimelineEvents(timelineEventService.getTimelineEvents(item.getId()));
        }));
        return items;
    }

    public Optional<Item> getItemById(Long id) {
//        return Optional.ofNullable(itemMapper.selectById(id));
        return Optional.ofNullable(getOne(id));
    }

    public Item addItem(Item item) {
        itemMapper.insert(item);
        return item;
    }

    public Optional<Item> updateItem(Long id, Item newItem) {
        newItem.setId(id);
        int result = itemMapper.updateById(newItem);
        return result > 0 ? Optional.of(newItem) : Optional.empty();
    }

    public void deleteItem(Long id) {
        itemMapper.deleteById(id);
    }

    public double getTotalValue() {
        List<Item> items = itemMapper.selectList(null);
        return items.stream().mapToDouble(Item::getPrice).sum();
    }

}
