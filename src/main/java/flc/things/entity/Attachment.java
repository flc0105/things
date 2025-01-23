package flc.things.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("attachments")
public class Attachment {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "file_path")
    private String filePath;

    @TableField(value = "original_file_name")
    private String originalFileName;
}
