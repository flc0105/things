package flc.things.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import flc.things.entity.Category;
import flc.things.entity.TimelineEvent;
import flc.things.mapper.CategoryMapper;
import flc.things.mapper.TimelineEventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimelineEventService {

    @Autowired
    private TimelineEventMapper timelineEventMapper;

    public List<TimelineEvent> getTimelineEvents(Long itemId) {
        // 这里应该有一个数据库查询来获取事件列表
        // 例如，使用MyBatis-Plus的查询
        QueryWrapper<TimelineEvent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id", itemId);
        return timelineEventMapper.selectList(queryWrapper);
    }

    public void addEvent(Long itemId, String date, String eventDescription) {
        TimelineEvent timelineEvent = new TimelineEvent();
        timelineEvent.setItemId(itemId);
        timelineEvent.setDate(date);
        timelineEvent.setEvent(eventDescription);
        timelineEventMapper.insert(timelineEvent);
    }
}
