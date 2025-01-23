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

@Service
public class CustomFieldService {

    @Autowired
    private CustomFieldMapper customFieldMapper;

    @Autowired
    private ItemCustomFieldValueMapper itemCustomFieldValueMapper;

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

    public CustomField setEnabled(Long id, boolean isEnabled) {
        CustomField customField = customFieldMapper.selectById(id);
        if (customField != null) {
            customField.setEnabled(isEnabled);
            customFieldMapper.updateById(customField);
            return customField;
        }
        return null;
    }

    public List<CustomField> getAllCustomFields() {
        return customFieldMapper.selectList(null);
    }

    public List<CustomField> getEnabledCustomFields() {
        return customFieldMapper.selectList(new LambdaQueryWrapper<CustomField>().eq(CustomField::isEnabled, true));
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
                result.add(new ItemCustomFieldValue(itemId, computedCustomField.getId(), String.valueOf(o)));
            } catch (Exception e) {
                result.add(new ItemCustomFieldValue(itemId, computedCustomField.getId(), "运算失败(" + e.getMessage() + ")"));
            }
        }
        return result;
    }
}
