package flc.things.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import flc.things.entity.Category;
import flc.things.entity.Item;
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
    private CategoryService categoryService;

    @Autowired
    private TimelineEventService timelineEventService;

    @Autowired
    private AttachmentService attachmentService;


    public ItemService(ItemMapper itemMapper) {
        super(itemMapper);
    }

    public Item addItem(Item item) {
        itemMapper.insert(item);
        timelineEventService.addEvent(item.getId(), item.getPurchaseDate(), "购买");
        return item;
    }

    public void deleteItem(Long id) {
        itemMapper.deleteById(id);
    }

    public Item updateItem(Long id, Item newItem) {
        newItem.setId(id);
        UpdateWrapper<Item> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set(true, "attachment_id", newItem.getAttachmentId());
        itemMapper.update(newItem, updateWrapper);
        return newItem;
    }

    public List<Item> getAllItems() {
        List<Item> items = list(new LambdaQueryWrapper<Item>().isNull(Item::getParentId)); // 查询所有父物品
        items.forEach((item -> {
            item.setTimelineEvents(timelineEventService.getTimelineEvents(item.getId()));
            item.setAttachment(attachmentService.getAttachmentById(item.getAttachmentId()));
            item.setSubItems(getSubItems(item.getId()));
            setTotalPrice(item);
        }));
        items.forEach(this::populateCategory);
        return items;
    }

    public Optional<Item> getItemById(Long id) {
        Item item = getOne(id);
        item.setTimelineEvents(timelineEventService.getTimelineEvents(item.getId()));
        item.setAttachment(attachmentService.getAttachmentById(item.getAttachmentId()));
        item.setSubItems(getSubItems(id));
        setTotalPrice(item);
        populateCategory(item);
        return Optional.of(item);
    }

    public List<Item> getSubItems(Long itemId) {
        return list(new LambdaQueryWrapper<Item>().eq(Item::getParentId, itemId));
    }

    public void populateCategory(Item item) {
        if (item.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(item.getCategoryId());
            item.setCategory(category);
        }
    }

    public void setTotalPrice(Item item) {
        // 计算总价格（包括子物品）
        if (item.getSubItems() != null && !item.getSubItems().isEmpty()) {
            double totalPrice = item.getPrice() != null ? item.getPrice() : 0.0;
            totalPrice += item.getSubItems().stream().mapToDouble(subItem -> subItem.getPrice() != null ? subItem.getPrice() : 0.0).sum();
            item.setPrice(totalPrice);
        }
    }


}
