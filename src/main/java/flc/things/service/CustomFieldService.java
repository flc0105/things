package flc.things.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import flc.things.entity.CustomField;
import flc.things.entity.Item;
import flc.things.entity.ItemCustomFieldValue;
import flc.things.mapper.CustomFieldMapper;
import flc.things.mapper.ItemCustomFieldValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomFieldService {

    @Autowired
    private CustomFieldMapper customFieldMapper;

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
        return itemCustomFieldValueMapper.selectList(new LambdaQueryWrapper<ItemCustomFieldValue>().eq(ItemCustomFieldValue::getItemId, itemId));
//        return itemCustomFieldValueMapper.selectCustomFieldValuesWithFieldNameByItemId(itemId);
    }


    public void addCustomFieldValueForItem(Long itemId, Long customFieldId, String text) {
        ItemCustomFieldValue itemCustomFieldValue = new ItemCustomFieldValue();
        itemCustomFieldValue.setItemId(itemId);
        itemCustomFieldValue.setCustomFieldId(customFieldId);
        itemCustomFieldValue.setValue(text);
        itemCustomFieldValueMapper.insert(itemCustomFieldValue);

    }

    public int addCustomFieldValue(ItemCustomFieldValue customFieldValue) {
        // TODO: 如果itemId已经存在相同fieldId的值 则修改而不是新增。
        return itemCustomFieldValueMapper.insert(customFieldValue);
    }



}
