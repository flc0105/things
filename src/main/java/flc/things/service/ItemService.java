package flc.things.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import flc.things.entity.Category;
import flc.things.entity.Item;
import flc.things.entity.TimelineEvent;
import flc.things.mapper.CategoryMapper;
import flc.things.mapper.ItemMapper;
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
    private TimelineEventService timelineEventService;

    @Autowired
    private AttachmentService attachmentService;


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
        List<Item> items = list(new LambdaQueryWrapper<Item>().isNull(Item::getParentId));
        items.forEach((item -> {
            item.setTimelineEvents(timelineEventService.getTimelineEvents(item.getId()));
            item.setAttachment(attachmentService.getAttachmentById(item.getAttachmentId()));
            item.setSubItems(list(new LambdaQueryWrapper<Item>().eq(Item::getParentId, item.getId())));

            // 如果有子物品，需要加上子物品的价格
            if (item.getSubItems() != null && !item.getSubItems().isEmpty()) {
                Double totalPrice = item.getPrice();
                for (Item subItem : item.getSubItems()) {
                    totalPrice += subItem.getPrice();
                }
                item.setPrice(totalPrice);
            }
        }));

        items.forEach(this::populateCategory);
        return items;
    }


    public List<Item> getSubItems(Long itemId) {
        return list(new LambdaQueryWrapper<Item>().eq(Item::getParentId, itemId));
    }


    public Optional<Item> getItemById(Long id) {
//        return Optional.ofNullable(itemMapper.selectById(id));


        Item item = getOne(id);
        item.setTimelineEvents(timelineEventService.getTimelineEvents(item.getId()));
        item.setAttachment(attachmentService.getAttachmentById(item.getAttachmentId()));
        item.setSubItems(list(new LambdaQueryWrapper<Item>().eq(Item::getParentId, id)));
        populateCategory(item);
        return Optional.ofNullable(item);
    }

    public Item addItem(Item item) {
        itemMapper.insert(item);
        return item;
    }

    public Item updateItem(Long id, Item newItem) {
        newItem.setId(id);

        UpdateWrapper<Item> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set(true, "attachment_id", newItem.getAttachmentId());
        itemMapper.update(newItem, updateWrapper);
//        int result = itemMapper.updateById(newItem);
        return newItem;
//        return result > 0 ? Optional.of(newItem) : Optional.empty();
    }

    public void deleteItem(Long id) {
        itemMapper.deleteById(id);
    }


}
