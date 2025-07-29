package flc.things.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import flc.things.annotation.Translator;
import lombok.Data;

import java.text.DecimalFormat;
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

    @TableField(exist = false)
    private String statusStr;

    @TableField(exist = false)
    private String ownershipDuration;

    @TableField(value = "category_id")
    private Long categoryId;

    @TableField(value = "attachment_id")
    private Long attachmentId;

    @TableField(value = "parent_id")
    private Long parentId;

    @TableField(exist = false)
    private Category category;

    @TableField(exist = false)
    private List<TimelineEvent> timelineEvents;

    @TableField(exist = false)
    private List<Item> subItems;

    @TableField(exist = false)
    private List<Item> children;

    @TableField(exist = false)
    private Double averageDailyPrice;

    @TableField(exist = false)
    private Attachment attachment;

    public Long calcOwnershipDuration() {
        try {
            if (status.equals("SOLD")) {
                // 如果状态为“出售”，计算从购买日期到时间轴上第一个（最新的）事件的天数
                //TODO eventType为SALE
                if (timelineEvents != null && !timelineEvents.isEmpty()) {
                    // 假设 TimelineEvent 有一个方法获取事件日期
                    LocalDate latestEventDate = LocalDate.parse(timelineEvents.get(0).getDate());
                    LocalDate purchaseDate = LocalDate.parse(this.purchaseDate);
                    Duration duration = Duration.between(purchaseDate.atStartOfDay(), latestEventDate.atStartOfDay());
                    return duration.toDays();
                } else {
                    return null;
                }
            } else {
                LocalDateTime now = LocalDateTime.now();
                LocalDate parsedDate = LocalDate.parse(purchaseDate);
                Duration duration = Duration.between(parsedDate.atStartOfDay(), now);
                return duration.toDays();
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String getOwnershipDuration() {
        Long days = calcOwnershipDuration();
        if (days == null) {
            return null;
        }
        return days + " days";
    }

    public Double getAverageDailyPrice() {
        try {
            Long duration = calcOwnershipDuration();
            // 确保购买日期不为空且价格大于0
            if (duration != null && price != null && price > 0) {
                if (duration > 0) {
                    double dailyPrice = price / duration;
                    // 格式化结果为保留两位小数
                    DecimalFormat decimalFormat = new DecimalFormat("#.00");
                    return Double.parseDouble(decimalFormat.format(dailyPrice));
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

}

