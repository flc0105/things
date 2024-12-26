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
        QueryWrapper<TimelineEvent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id", itemId);
        queryWrapper.orderByDesc("date");
        return timelineEventMapper.selectList(queryWrapper);
    }

    public void addEvent(Long itemId, String date, String eventDescription) {
        TimelineEvent timelineEvent = new TimelineEvent();
        timelineEvent.setItemId(itemId);
        timelineEvent.setDate(date);
        timelineEvent.setEvent(eventDescription);
        timelineEventMapper.insert(timelineEvent);
    }

    public void deleteEvent(Long eventId) {
        timelineEventMapper.deleteById(eventId);
    }
}
