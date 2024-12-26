package flc.things.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("item_custom_field_values")
public class ItemCustomFieldValue {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("item_id")
    private Long itemId;

    @TableField("custom_field_id")
    private Long customFieldId;

    @TableField("value")
    private String value;

    @TableField(exist = false)
    private CustomField customField;

    @TableField(exist = false)
    private String fieldName;
}
