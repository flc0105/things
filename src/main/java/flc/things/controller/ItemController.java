package flc.things.controller;

import flc.things.entity.Item;
import flc.things.entity.TimelineEvent;
import flc.things.service.ItemService;
import flc.things.service.TimelineEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private TimelineEventService timelineEventService;

    @GetMapping
    public List<Item> getAllItems() {
        List<Item> allItems = itemService.getAllItems();
        return allItems;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Optional<Item> item = itemService.getItemById(id);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Item addItem(@RequestBody Item item) {
        Item itemAdded = itemService.addItem(item);
        timelineEventService.addEvent(item.getId(), item.getPurchaseDate(), "购买");
        return itemAdded;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item newItem) {
        Optional<Item> updatedItem = itemService.updateItem(id, newItem);
        return updatedItem.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total-value")
    public ResponseEntity<Double> getTotalValue() {
        double totalValue = itemService.getTotalValue();
        return ResponseEntity.ok(totalValue);
    }

    @GetMapping("/timeline/{id}")
    public ResponseEntity<List<TimelineEvent>> getTimelineEvent(@PathVariable Long id) {
        return ResponseEntity.ok(timelineEventService.getTimelineEvents(id));
    }
}
