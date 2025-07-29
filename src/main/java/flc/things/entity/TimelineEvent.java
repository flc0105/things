package flc.things.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Map;

@Data
@TableName("timeline_events")
public class TimelineEvent {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "item_id")
    private Long itemId; // 关联的物品ID

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String date; // 事件发生的日期

    @TableField(value = "event_type")
    private String eventType;

    @TableField(value = "event_description")
    private String eventDescription; // 事件描述
}
