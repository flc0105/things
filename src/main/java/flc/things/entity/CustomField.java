package flc.things.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("custom_fields")
public class CustomField {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "field_name")
    private String fieldName;

    @TableField(value = "field_type")
    private String fieldType;

    @TableField(value = "formula")
    private String formula;

    @TableField(value = "enabled")
    private boolean enabled;
}
