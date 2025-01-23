package flc.things.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import flc.things.entity.CustomField;
import flc.things.entity.ItemCustomFieldValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ItemCustomFieldValueMapper extends BaseMapper<ItemCustomFieldValue> {
    // Add custom methods if needed
//
//    @Select("SELECT icf.*, cf.field_name " +
//            "FROM item_custom_field_values icf " +
//            "LEFT JOIN custom_fields cf ON icf.custom_field_id = cf.id " +
//            "WHERE icf.item_id = #{itemId}")
//    List<ItemCustomFieldValue> selectCustomFieldValuesWithFieldNameByItemId(Long itemId);


}