package flc.things.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import flc.things.annotation.Translator;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("items")
public class Item {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private Double price;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "purchase_date")
    private String purchaseDate;

    private String remark;

    @Translator(targetField = "statusStr", dictCode = "ITEM_STATUS")
    private String status;

    // 新增字段用于存储翻译后的值
    @TableField(exist = false)
    private String statusStr;

    @TableField(exist = false)
    private String ownershipDuration;

    @TableField(value = "category_id")
    private Long categoryId;

    @TableField(value = "attachment_id")
    private Long attachmentId;

    @TableField(exist = false)
    private Category category;

    @TableField(exist = false)
    private List<TimelineEvent> timelineEvents;


    // 添加计算拥有时间的方法
    public void calculateOwnershipDuration() {


//        try {
//            LocalDateTime now = LocalDateTime.now();
//            LocalDate parsedDate = LocalDate.parse(purchaseDate);
//            Duration duration = Duration.between(parsedDate.atStartOfDay(), now);
//            long days = duration.toDays();
//            this.ownershipDuration = days + " days";
//        } catch (Exception e) {
//            this.ownershipDuration = null;
//        }

        try {
            if (status.equals("SOLD")) {
                // 如果状态为“出售”，计算从购买日期到时间轴上第一个（最新的）事件的天数
                if (timelineEvents != null && !timelineEvents.isEmpty()) {
                    // 假设 TimelineEvent 有一个方法获取事件日期
                    LocalDate latestEventDate = LocalDate.parse(timelineEvents.get(0).getDate());
                    LocalDate purchaseDate = LocalDate.parse(this.purchaseDate);
                    Duration duration = Duration.between(purchaseDate.atStartOfDay(), latestEventDate.atStartOfDay());
                    long days = duration.toDays();
                    this.ownershipDuration = days + " days";
                } else {
                    this.ownershipDuration = null;
                }
            } else {
                LocalDateTime now = LocalDateTime.now();
                LocalDate parsedDate = LocalDate.parse(purchaseDate);
                Duration duration = Duration.between(parsedDate.atStartOfDay(), now);
                long days = duration.toDays();
                this.ownershipDuration = days + " days";
            }
        } catch (Exception e) {
            this.ownershipDuration = null;
        }
    }

}

