package flc.things.controller;

import flc.things.entity.Attachment;
import flc.things.entity.Item;
import flc.things.entity.TimelineEvent;
import flc.things.service.AttachmentService;
import flc.things.service.TimelineEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/timeline")
public class TimelineEventController {

    @Autowired
    private TimelineEventService timelineEventService;

    @PostMapping
    public void addEvent(@RequestBody TimelineEvent timelineEvent) {
        timelineEventService.addEvent(timelineEvent.getItemId(), timelineEvent.getDate(), timelineEvent.getEvent());
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        timelineEventService.deleteEvent(id);
    }
}
