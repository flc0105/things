package flc.things.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("item_custom_field_values")
public class ItemCustomFieldValue {

    public ItemCustomFieldValue() {
    }

    public ItemCustomFieldValue(Long itemId, Long customFieldId, String customFieldName, String value) {
        this.itemId = itemId;
        this.customFieldId = customFieldId;
        this.customFieldName = customFieldName;
        this.value = value;
    }

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("item_id")
    private Long itemId;

    @TableField("custom_field_id")
    private Long customFieldId;

    // add 2025.07.29
    @TableField(exist = false)
    private String customFieldName;

    @TableField("value")
    private String value;
}
