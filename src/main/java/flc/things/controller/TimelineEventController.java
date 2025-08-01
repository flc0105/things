package flc.things.controller;

import flc.things.entity.TimelineEvent;
import flc.things.service.TimelineEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeline")
public class TimelineEventController {

    @Autowired
    private TimelineEventService timelineEventService;

//    @Autowired
//    private ItemService itemService;

    @PostMapping
    public void addEvent(@RequestBody TimelineEvent timelineEvent) {
        timelineEventService.addEvent(timelineEvent.getItemId(), timelineEvent.getDate(), timelineEvent.getEventDescription(), timelineEvent.getEventType());
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        timelineEventService.deleteEvent(id);
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<List<TimelineEvent>> getTimelineEvent(@PathVariable Long id) {
        return ResponseEntity.ok(timelineEventService.getTimelineEventsByItemId(id));
    }

//    @PostMapping("/init")
//    public void init() {
//        List<Item> allItems = itemService.getAllItems();
//        for (Item item : allItems) {
//            timelineEventService.addEvent(item.getId(), item.getPurchaseDate(), "购买");
//        }
//    }
}
