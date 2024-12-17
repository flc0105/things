package flc.things.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import flc.things.entity.TimelineEvent;
import org.springframework.stereotype.Repository;

@Repository
public interface TimelineEventMapper extends BaseMapper<TimelineEvent> {
    // You can add custom queries here if needed
}
