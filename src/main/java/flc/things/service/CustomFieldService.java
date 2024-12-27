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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CustomFieldService {

    @Autowired
    private CustomFieldMapper customFieldMapper;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemCustomFieldValueMapper itemCustomFieldValueMapper;

    //    public DictData getDictDataById(Long id) {
//        return dictDataMapper.selectById(id);
//    }
//
    public CustomField addCustomField(CustomField customField) {
        customFieldMapper.insert(customField);
        return customField;
    }

    public List<CustomField> getAllCustomFields() {
        return customFieldMapper.selectList(null);
    }

//    @Transactional
//    public ItemCustomFieldValue addOrUpdateCustomField(ItemCustomFieldValue icfv) {
//        // 查询是否存在相同itemId和customFieldId的记录
//        ItemCustomFieldValue existingRecord = itemCustomFieldValueMapper.selectOne(
//                new QueryWrapper<ItemCustomFieldValue>()
//                        .eq("item_id", icfv.getItemId())
//                        .eq("custom_field_id", icfv.getCustomFieldId())
//        );
//
//        // 如果存在，则更新value
//        if (existingRecord != null) {
//            existingRecord.setValue(icfv.getValue());
//            itemCustomFieldValueMapper.updateById(existingRecord);
//        } else {
//            // 如果不存在，则创建新的ItemCustomFieldValue对象并插入
//            itemCustomFieldValueMapper.insert(icfv);
//        }
//        // 返回原始的CustomField对象
//        return icfv;
//    }

    @Transactional
    public boolean addOrUpdateCustomField(List<ItemCustomFieldValue> icfvs) {
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

                // 判断如果是公式类型不生成新纪录
                CustomField customField = customFieldMapper.selectById(icfv.getCustomFieldId());
                if (Objects.equals(customField.getFieldType(), CustomFieldType.CODE.getCode())) {
                    continue;
                }

                itemCustomFieldValueMapper.insert(icfv);
            }
        }
        return true;
    }

//
//    public DictData updateDictData(Long id, DictData newDictData) {
//        newDictData.setId(id);
//        dictDataMapper.updateById(newDictData);
//        return new DictData();
//    }
//
//    public void deleteDictData(Long id) {
//        dictDataMapper.deleteById(id);
//    }
//

    public List<ItemCustomFieldValue> getCustomFieldValueListByItemId(Long itemId) {
        // 查询所有该物品的自定义字段
        List<ItemCustomFieldValue> values = itemCustomFieldValueMapper.selectList(new LambdaQueryWrapper<ItemCustomFieldValue>().eq(ItemCustomFieldValue::getItemId, itemId));

        // 新建一个map用来存储自定义字段键值对
        Map<String, Object> customFieldValues = new HashMap<>();

        // 遍历该物品所有自定义字段
        for (ItemCustomFieldValue value : values) {
            // 设置自定义字段
            value.setCustomField(customFieldMapper.selectById(value.getCustomFieldId()));
            // 设置物品信息
            value.setItem(itemService.getItemById(value.getItemId()).get());
            // 存储键值对信息
            customFieldValues.put(value.getCustomField().getFieldName(), value.getValue());
        }

        // 查询关联物品信息
        Item item = itemService.getItemById(itemId).get();

        // 查询所有需要计算的公式自定义字段
        List<CustomField> computedCustomFields = customFieldMapper.selectList(new LambdaQueryWrapper<CustomField>().eq(CustomField::getFieldType, CustomFieldType.CODE.getCode()));
        for (CustomField computedCustomField : computedCustomFields) {
            // 获取公式
            String expression = computedCustomField.getFormula();
            // 在公式上下文存储物品本身信息和自定义字段信息
            Map<String, Object> context = new HashMap<>();
            context.put("item", item);
            context.put("customFields", customFieldValues);

            try {
                // 在自定义字段返回结果添加计算后的值
                ComputedFieldUtil util = new ComputedFieldUtil();
                Object o = util.evaluateExpression(expression, context);
                ItemCustomFieldValue newComputedFieldVal = new ItemCustomFieldValue();
                newComputedFieldVal.setItemId(itemId);
                newComputedFieldVal.setValue(String.valueOf(o));
                newComputedFieldVal.setCustomFieldId(computedCustomField.getId());
                values.add(newComputedFieldVal);
            } catch (Exception e) {
                ItemCustomFieldValue newComputedFieldVal = new ItemCustomFieldValue();
                newComputedFieldVal.setItemId(itemId);
                newComputedFieldVal.setValue("运算失败");
                newComputedFieldVal.setCustomFieldId(computedCustomField.getId());
                values.add(newComputedFieldVal);
            }
        }

//        for (ItemCustomFieldValue value : values) {
//
//            if (Objects.equals(value.getCustomField().getFieldType(), CustomFieldType.CODE.getCode())) {
//                // 运算字段
//                //获取公式
//                String compression = value.getValue();
//
//                // 获取变量
//                Map<String, Object> context = new HashMap<>();
//                context.put("item", value.getItem());
//                context.put("customFields", customFieldValues);
//
//                try {
//                    ComputedFieldUtil util = new ComputedFieldUtil();
//                    Object o = util.evaluateExpression(compression, context);
//                    value.setValue(String.valueOf(o));
//                } catch (Exception e) {
//                    value.setValue("运算失败");
//                }
//            }
//        }

        return values;
//        return itemCustomFieldValueMapper.selectCustomFieldValuesWithFieldNameByItemId(itemId);
    }


}
