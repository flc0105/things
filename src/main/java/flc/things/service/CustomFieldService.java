package flc.things.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import flc.things.entity.CustomField;
import flc.things.entity.ItemCustomFieldValue;
import flc.things.mapper.CustomFieldMapper;
import flc.things.mapper.ItemCustomFieldValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomFieldService {

    @Autowired
    private CustomFieldMapper customFieldMapper;

    @Autowired
    private ItemCustomFieldValueMapper itemCustomFieldValueMapper;

    /**
     * 添加一个新的自定义字段
     *
     * @param customField 自定义字段对象
     * @return 添加后的自定义字段对象
     */
    public CustomField addCustomField(CustomField customField) {
        customField.setEnabled(true);
        customFieldMapper.insert(customField);
        return customField;
    }

    /**
     * 删除指定ID的自定义字段及其关联的自定义字段值
     *
     * @param id 自定义字段ID
     */
    public void deleteCustomField(Long id) {
        int deleteCount = customFieldMapper.deleteById(id);
        if (deleteCount > 0) {
            // 如果自定义字段删除成功，删除关联表中的数据
            itemCustomFieldValueMapper.delete(new LambdaQueryWrapper<ItemCustomFieldValue>().eq(ItemCustomFieldValue::getCustomFieldId, id));
        }
    }

    /**
     * 更新自定义字段信息
     *
     * @param id             自定义字段ID
     * @param newCustomField 新的自定义字段对象
     * @return 更新后的自定义字段对象
     */
    public CustomField updateCustomField(Long id, CustomField newCustomField) {
        newCustomField.setId(id);
        customFieldMapper.updateById(newCustomField);
        return newCustomField;
    }

    /**
     * 获取所有自定义字段列表
     *
     * @return 所有自定义字段的列表
     */
    public List<CustomField> getAllCustomFields() {
        return customFieldMapper.selectList(null);
    }

    public List<CustomField> getEnabledCustomFields() {
        return customFieldMapper.selectList(new LambdaQueryWrapper<CustomField>().eq(CustomField::isEnabled, true));
    }

    /**
     * 设置自定义字段的启用状态
     *
     * @param id        自定义字段ID
     * @param isEnabled 启用状态
     * @return 更新后的自定义字段对象
     */
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