package flc.things.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import flc.things.entity.CustomField;
import flc.things.entity.Item;
import flc.things.entity.ItemCustomFieldValue;
import flc.things.enums.CustomFieldType;
import flc.things.mapper.CustomFieldMapper;
import flc.things.mapper.ItemCustomFieldValueMapper;
import flc.things.util.ComputedFieldUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemCustomFieldValueService {

    @Autowired
    private ItemCustomFieldValueMapper itemCustomFieldValueMapper;

    @Autowired
    private CustomFieldMapper customFieldMapper;

    @Autowired
    @Lazy
    private ItemService itemService;

    /**
     * 添加或更新物品自定义字段值
     *
     * @param icfvs 自定义字段值列表
     * @return 操作成功与否
     */
    @Transactional
    public boolean addOrUpdateItemCustomFieldValue(List<ItemCustomFieldValue> icfvs) {
        // 查询是否已存在自定义字段值
        for (ItemCustomFieldValue icfv : icfvs) {
            ItemCustomFieldValue existingRecord = itemCustomFieldValueMapper.selectOne(
                    new QueryWrapper<ItemCustomFieldValue>()
                            .eq("item_id", icfv.getItemId())
                            .eq("custom_field_id", icfv.getCustomFieldId())
            );
            if (existingRecord != null) {
                // 更新已存在的记录
                if (icfv.getValue() == null || icfv.getValue().isEmpty()) {
                    // 如果更新后字段为空，直接删除该字段
                    int deleteCount = deleteItemCustomFieldValue(existingRecord.getId());
                    System.out.println(deleteCount);
                    continue;
                }
                existingRecord.setValue(icfv.getValue());
                itemCustomFieldValueMapper.updateById(existingRecord);
            } else {
                // 判断字段类型为公式类型时跳过插入
                CustomField customField = customFieldMapper.selectById(icfv.getCustomFieldId());
                if (Objects.equals(customField.getFieldType(), CustomFieldType.CODE.getCode())) {
                    continue;
                }
                if (icfv.getValue() != null && !icfv.getValue().isEmpty()) {
                    itemCustomFieldValueMapper.insert(icfv);
                }
            }
        }
        return true;
    }


    public int deleteItemCustomFieldValue(Long icfvId) {
        return itemCustomFieldValueMapper.delete(new LambdaQueryWrapper<ItemCustomFieldValue>().eq(ItemCustomFieldValue::getId, icfvId));
    }

    /**
     * 根据物品ID删除关联的自定义字段值
     *
     * @param itemId 物品ID
     */
    public void deleteItemCustomFieldValueByItemId(Long itemId) {
        itemCustomFieldValueMapper.delete(new LambdaQueryWrapper<ItemCustomFieldValue>().eq(ItemCustomFieldValue::getItemId, itemId));
    }

    /**
     * 根据物品ID获取自定义字段值列表
     *
     * @param itemId 物品ID
     * @return 物品自定义字段值列表
     */
    public List<ItemCustomFieldValue> getItemCustomFieldValueListByItemId(Long itemId) {
        List<ItemCustomFieldValue> result = new ArrayList<>(); // 初始化返回值列表
        Map<String, Object> customFieldsContext = new HashMap<>(); // 初始化自定义字段列表上下文

        // 查询物品信息作为上下文
        Optional<Item> itemOptional = getItemById(itemId);
        if (!itemOptional.isPresent()) {
            return result; // 如果物品不存在，返回空列表
        }
        Item itemContext = itemOptional.get();

        // 查询该物品所有的自定义字段值
        List<ItemCustomFieldValue> icfvs = itemCustomFieldValueMapper.selectList(new LambdaQueryWrapper<ItemCustomFieldValue>().eq(ItemCustomFieldValue::getItemId, itemId));
        for (ItemCustomFieldValue icfv : icfvs) {
            CustomField customField = customFieldMapper.selectById(icfv.getCustomFieldId());
            if (customField == null) {
                System.out.println("Custom field with ID " + icfv.getCustomFieldId() + " does not exist, it may have been deleted.");
                continue;
            }
            if (!customField.isEnabled()) {
                continue;
            }
            customFieldsContext.put(customField.getFieldName(), icfv.getValue()); // 存储字段名和值
            icfv.setCustomFieldName(customField.getFieldName()); // 设置自定义字段名称
            result.add(icfv);
        }

        // 查询所有公式字段并计算结果
        List<CustomField> computedCustomFields = customFieldMapper.selectList(new LambdaQueryWrapper<CustomField>().eq(CustomField::getFieldType, CustomFieldType.CODE.getCode()).eq(CustomField::isEnabled, true));
        for (CustomField computedCustomField : computedCustomFields) {
            String expression = computedCustomField.getFormula(); // 获取公式
            // 存储物品信息和自定义字段上下文信息
            Map<String, Object> context = new HashMap<>();
            itemContext.setItemCustomFieldValueList(icfvs); ////为item set icfv，但是不支持计算字段
            context.put("item", itemContext);
            context.put("customFields", customFieldsContext);
            try {
                ComputedFieldUtil util = new ComputedFieldUtil();
                Object o = util.evaluateExpression(expression, context);
                customFieldsContext.put(computedCustomField.getFieldName(), String.valueOf(o)); // 计算完后将公式字段同步更新到上下文
                result.add(new ItemCustomFieldValue(itemId, computedCustomField.getId(), computedCustomField.getFieldName(), String.valueOf(o)));
            } catch (Exception e) {
                result.add(new ItemCustomFieldValue(itemId, computedCustomField.getId(), computedCustomField.getFieldName(), "运算失败(" + e.getMessage() + ")"));
            }
        }
        return result;
    }

    /**
     * 查询所有自定义字段名及其对应的值
     *
     * @return 所有自定义字段名及值
     */
    public Map<String, List<String>> findAllFieldNamesAndValues() {
        // 查询所有记录
        List<ItemCustomFieldValue> allValues = itemCustomFieldValueMapper.selectList(null);

        // 使用Map来收集每个customFieldId的所有value
        Map<Long, Set<String>> groupedValuesMap = allValues.stream()
                .collect(Collectors.groupingBy(
                        ItemCustomFieldValue::getCustomFieldId, // 分组依据是customFieldId
                        Collectors.mapping(ItemCustomFieldValue::getValue, Collectors.toSet()) // 收集唯一的value
                ));

        // 将Map的键转换为String类型
        Map<String, List<String>> result = new HashMap<>();
        groupedValuesMap.forEach((customFieldId, values) -> result.put(String.valueOf(customFieldId), new ArrayList<>(values)));
        return result;
    }

    /**
     * 根据物品ID获取物品信息
     *
     * @param itemId 物品ID
     * @return 物品信息
     */
    private Optional<Item> getItemById(Long itemId) {
        return itemService.getItemByIdNoIcfvs(itemId);
    }

    /**
     * 根据自定义字段ID和字段值查询物品
     *
     * @param customFieldId 自定义字段ID
     * @param fieldValue    自定义字段值
     * @return 物品列表
     */
    public List<Item> getItemsByCustomFieldIdAndValue(String customFieldId, String fieldValue) {
        List<ItemCustomFieldValue> itemCustomFieldValues = itemCustomFieldValueMapper.selectList(new LambdaQueryWrapper<ItemCustomFieldValue>()
                .eq(ItemCustomFieldValue::getCustomFieldId, customFieldId)
                .eq(ItemCustomFieldValue::getValue, fieldValue)); //TODO: if icfv
        return itemCustomFieldValues.stream()
                .map(value -> getItemById(value.getItemId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


}
