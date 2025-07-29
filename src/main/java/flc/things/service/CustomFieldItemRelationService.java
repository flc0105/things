package flc.things.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import flc.things.entity.ItemCustomFieldValue;
import flc.things.mapper.ItemCustomFieldValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomFieldItemRelationService {


    @Autowired
    private ItemCustomFieldValueMapper itemCustomFieldValueMapper;

    public void deleteDataByItemId(Long itemId) {
        itemCustomFieldValueMapper.delete(new LambdaQueryWrapper<ItemCustomFieldValue>().eq(ItemCustomFieldValue::getItemId, itemId));
    }


}
