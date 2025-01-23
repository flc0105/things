package flc.things.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dict_data")
public class DictData {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String dictCode;

    private String code;

    private String name;
}
