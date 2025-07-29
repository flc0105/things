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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomFieldService {

    @Autowired
    private CustomFieldMapper customFieldMapper;

    @Autowired
    private ItemCustomFieldValueMapper itemCustomFieldValueMapper;

//    @Autowired
//    private ItemServiceImpl itemService;

    @Autowired
    private ItemService itemService;

    public CustomField addCustomField(CustomField customField) {
        customField.setEnabled(true);
        customFieldMapper.insert(customField);
        return customField;
    }

    public void deleteCustomField(Long id) {
        int i = customFieldMapper.deleteById(id);
        if (i > 0) {
            // 删除自定义字段同时删除关联表中的数据
            itemCustomFieldValueMapper.delete(new LambdaQueryWrapper<ItemCustomFieldValue>().eq(ItemCustomFieldValue::getCustomFieldId, id));
        }
    }

    public CustomField updateCustomField(Long id, CustomField newCustomField) {
        newCustomField.setId(id);
        customFieldMapper.updateById(newCustomField);
        return newCustomField;
    }


    public List<CustomField> getAllCustomFields() {
        return customFieldMapper.selectList(null);
    }


    @Transactional
    public boolean addOrUpdateCustomFieldValue(List<ItemCustomFieldValue> icfvs) {
        // 查询是否存在相同itemId和customFieldId的记录
        for (ItemCustomFieldValue icfv : icfvs) {
            ItemCustomFieldValue existingRecord = itemCustomFieldValueMapper.selectOne(
                    new QueryWrapper<ItemCustomFieldValue>()
                            .eq("item_id", icfv.getItemId())
                            .eq("custom_field_id", icfv.getCustomFieldId())
            );
            // 如果存在，则更新value
            if (existingRecord != null) {
                existingRecord.setValue(icfv.getValue());
                itemCustomFieldValueMapper.updateById(existingRecord);
            } else {
                // 如果不存在，则创建新的ItemCustomFieldValue对象并插入
                // 判断如果是公式类型不生成新记录
                CustomField customField = customFieldMapper.selectById(icfv.getCustomFieldId());
                if (Objects.equals(customField.getFieldType(), CustomFieldType.CODE.getCode())) {
                    continue;
                }
                itemCustomFieldValueMapper.insert(icfv);
            }
        }
        return true;
    }

    public List<ItemCustomFieldValue> getCustomFieldValueByItemId(Long itemId) {
        // 初始化返回值列表
        List<ItemCustomFieldValue> result = new ArrayList<>();
        // 初始化自定义字段上下文列表
        Map<String, Object> customFieldsContext = new HashMap<>();

        // 查询物品信息作为上下文
        Optional<Item> itemOptional = itemService.getItemById(itemId);
        if (!itemOptional.isPresent()) {
            return result; // 如果物品不存在，直接返回空列表
        }
        Item itemContext = itemOptional.get();

        // 先查询关联表中该物品的自定义字段
        List<ItemCustomFieldValue> customFieldValues = itemCustomFieldValueMapper.selectList(new LambdaQueryWrapper<ItemCustomFieldValue>().eq(ItemCustomFieldValue::getItemId, itemId));
        for (ItemCustomFieldValue value : customFieldValues) {
            CustomField customField = customFieldMapper.selectById(value.getCustomFieldId());
            if (customField == null) {
                System.out.println("Custom field with ID " + value.getCustomFieldId() + " does not exist, it may have been deleted.");
                continue;
            }
            customFieldsContext.put(customField.getFieldName(), value.getValue()); // 存储键值对信息作为上下文
            result.add(value);
        }

        // 再查询所有公式字段
        List<CustomField> computedCustomFields = customFieldMapper.selectList(new LambdaQueryWrapper<CustomField>().eq(CustomField::getFieldType, CustomFieldType.CODE.getCode()));
        for (CustomField computedCustomField : computedCustomFields) {
            // 获取公式
            String expression = computedCustomField.getFormula();
            // 存储物品信息和自定义字段上下文信息
            Map<String, Object> context = new HashMap<>();
            context.put("item", itemContext);
            context.put("customFields", customFieldsContext);
            try {
                ComputedFieldUtil util = new ComputedFieldUtil();
                Object o = util.evaluateExpression(expression, context);

                // add 2025.07.29 计算完后同步更新到上下文
                customFieldsContext.put(computedCustomField.getFieldName(), String.valueOf(o)); // 存储键值对信息作为上下文
//                context.put("customFields", customFieldsContext);

                result.add(new ItemCustomFieldValue(itemId, computedCustomField.getId(), String.valueOf(o)));
            } catch (Exception e) {
                result.add(new ItemCustomFieldValue(itemId, computedCustomField.getId(), "运算失败(" + e.getMessage() + ")"));
            }
        }
        return result;
    }

    // 查询指定customField所有已有的值
    public List<ItemCustomFieldValue> findAllValuesByCustomFieldId(Long customFieldId) {
        List<ItemCustomFieldValue> allValues = itemCustomFieldValueMapper.selectList(new LambdaQueryWrapper<ItemCustomFieldValue>().eq(ItemCustomFieldValue::getCustomFieldId, customFieldId));
        // 使用Map来过滤出唯一的value，并保留第一个出现的ItemCustomFieldValue对象
        Map<String, ItemCustomFieldValue> uniqueValuesMap = allValues.stream()
                .collect(Collectors.toMap(ItemCustomFieldValue::getValue, value -> value, (existing, replacement) -> existing));

        // 返回唯一value的列表
        return new ArrayList<>(uniqueValuesMap.values());
//        return uniqueValuesMap.values().stream().collect(Collectors.toList());
    }

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

    public List<Item> getItemsByCustomFieldIdAndValue(String customFieldId, String fieldValue) {
        List<ItemCustomFieldValue> itemCustomFieldValues = itemCustomFieldValueMapper.selectList(new LambdaQueryWrapper<ItemCustomFieldValue>().eq(ItemCustomFieldValue::getCustomFieldId, customFieldId).eq(ItemCustomFieldValue::getValue, fieldValue));
        List<Item> result = new ArrayList<>();
        for (ItemCustomFieldValue value : itemCustomFieldValues) {
            Long itemId = value.getItemId();
            Optional<Item> itemOptional = itemService.getItemById(itemId);
            itemOptional.ifPresent(result::add);
//            if (itemOptional.isPresent()) {
//                result.add(itemOptional.get());
//            }
        }
        return result;
    }



    public CustomField setEnabled(Long id, boolean isEnabled) {
        CustomField customField = customFieldMapper.selectById(id);
        if (customField != null) {
            customField.setEnabled(isEnabled);
            customFieldMapper.updateById(customField);
            return customField;
        }
        return null;
    }
}
