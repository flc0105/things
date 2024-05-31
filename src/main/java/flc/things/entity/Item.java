package flc.things.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @TableField(exist = false)
    private String ownershipDuration;

    private static final Logger logger = LoggerFactory.getLogger(Item.class);


    // 省略其他方法

    // 添加计算拥有时间的方法
    public void calculateOwnershipDuration() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDate parsedDate = LocalDate.parse(purchaseDate);
            Duration duration = Duration.between(parsedDate.atStartOfDay(), now);
            long days = duration.toDays();
            this.ownershipDuration = days + " days";
        } catch (Exception e) {
            logger.error("Error calculating ownership duration for item with purchase date: {}", purchaseDate, e);
            this.ownershipDuration = null;
        }
    }



    // getters and setters

    // Add constructors as needed
}

