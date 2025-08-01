package flc.things.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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

    @Autowired
    private ItemCustomFieldValueService itemCustomFieldValueService;

    public ItemService(ItemMapper itemMapper) {
        super(itemMapper);
    }

    /**
     * 添加物品
     *
     * @param item 要添加的物品对象
     * @return 添加后的物品对象
     */
    public Item addItem(Item item) {
        itemMapper.insert(item);
        timelineEventService.addEvent(item.getId(), item.getPurchaseDate(), "购买", "PURCHASE");
        return item;
    }

    /**
     * 删除物品及其相关数据
     *
     * @param itemId 物品ID
     */
    public void deleteItem(Long itemId) {
        timelineEventService.deleteTimelineEventsByItemId(itemId);
        itemCustomFieldValueService.deleteItemCustomFieldValueByItemId(itemId);
        itemMapper.deleteById(itemId);
    }

    /**
     * 更新物品信息
     *
     * @param id      物品ID
     * @param newItem 包含新数据的物品对象
     * @return 更新后的物品对象
     */
    public Item updateItem(Long id, Item newItem) {
        newItem.setId(id);
        UpdateWrapper<Item> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set(true, "attachment_id", newItem.getAttachmentId());
        itemMapper.update(newItem, updateWrapper);
        return newItem;
    }

    /**
     * 获取所有物品信息
     *
     * @return 所有物品的列表
     */
    public List<Item> getAllItems() {
        List<Item> items = list(new LambdaQueryWrapper<Item>().isNull(Item::getParentId)); // 查询所有父物品
        loadRelatedData(items);
        return items;
    }

    public List<Item> getAllItemsWithCustomFields() {
        List<Item> items = list(new LambdaQueryWrapper<Item>().isNull(Item::getParentId)); // 查询所有父物品
        loadRelatedData(items);
        items.forEach(item ->
                item.setItemCustomFieldValueList(
                        itemCustomFieldValueService.getItemCustomFieldValueListByItemId(item.getId())
                )
        );
        return items;
    }



    /**
     * 根据物品ID获取物品信息
     *
     * @param id 物品ID
     * @return 包含物品信息的Optional对象
     */
    public Optional<Item> getItemById(Long id) {
        Item item = getOne(id);
        loadRelatedData(item);
        return Optional.of(item);
    }

    public List<Item> queryItems(LambdaQueryWrapper<Item> qw) {
        if (qw == null) {
            qw = new LambdaQueryWrapper<>();
        }
        qw.isNull(Item::getParentId);
//        List<Item> items = itemMapper.selectList(qw);
        List<Item> items = list(qw);
        loadRelatedData(items);
        return items;
    }

    public Optional<Item> getItemByIdNoIcfvs(Long id) {
        Item item = getOne(id);
        Optional.ofNullable(item.getCategoryId())
                .ifPresent(categoryId -> item.setCategory(categoryService.getCategoryById(categoryId)));
        Optional.ofNullable(item.getAttachmentId())
                .ifPresent(attachmentId -> item.setAttachment(attachmentService.getAttachmentById(attachmentId)));
        item.setTimelineEvents(timelineEventService.getTimelineEventsByItemId(item.getId()));
//        item.setItemCustomFieldValueList(itemCustomFieldValueService.getItemCustomFieldValueListByItemId(item.getId()));
        item.setSubItems(getSubItemsByItemId(item.getId()));
        return Optional.of(item);
    }

    /**
     * 获取指定物品的子物品列表
     *
     * @param itemId 父物品ID
     * @return 子物品列表
     */
    public List<Item> getSubItemsByItemId(Long itemId) {
        return list(new LambdaQueryWrapper<Item>().eq(Item::getParentId, itemId));
    }

    /**
     * 加载物品的关联数据
     *
     * @param item 物品对象
     */
    private void loadRelatedData(Item item) {
        Optional.ofNullable(item.getCategoryId())
                .ifPresent(categoryId -> item.setCategory(categoryService.getCategoryById(categoryId)));
        Optional.ofNullable(item.getAttachmentId())
                .ifPresent(attachmentId -> item.setAttachment(attachmentService.getAttachmentById(attachmentId)));
        item.setTimelineEvents(timelineEventService.getTimelineEventsByItemId(item.getId()));
        item.setSubItems(getSubItemsByItemId(item.getId()));
    }

    /**
     * 批量加载物品的关联数据
     *
     * @param items 物品列表
     */
    private void loadRelatedData(List<Item> items) {
        items.forEach(this::loadRelatedData); // 遍历物品，加载关联数据
    }

//    /**
//     * 根据给定的表达式进行计算，支持物品数据操作
//     *
//     * @param expression 计算表达式
//     * @return 计算结果的字符串表示
//     */
//    public String processItemData(String expression) {
//        Map<String, Object> context = new HashMap<>();
//        List<Item> items = getAllItems();
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(items);;
//            context.put("items", jsonString);
//        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
//            return e.getMessage();
//        }
//        try {
//            ComputedFieldUtil util = new ComputedFieldUtil();
//            Object o = util.evaluateExpression(expression, context);
//            System.out.println(o);
//            return String.valueOf(o);
//        } catch (ScriptException e) {
//            return e.getMessage();
//        }
//    }

}
