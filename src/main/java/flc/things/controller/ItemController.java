package flc.things.controller;

import flc.things.entity.Item;
import flc.things.entity.ItemCustomFieldValue;
import flc.things.entity.TimelineEvent;
import flc.things.service.CustomFieldService;
import flc.things.service.ItemService;
import flc.things.service.TimelineEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private TimelineEventService timelineEventService;

    @Autowired
    private CustomFieldService customFieldService;

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

    @GetMapping("/timeline/{id}")
    public ResponseEntity<List<TimelineEvent>> getTimelineEvent(@PathVariable Long id) {
        return ResponseEntity.ok(timelineEventService.getTimelineEvents(id));
    }

    @GetMapping("/customFields/{id}")
    public ResponseEntity<List<ItemCustomFieldValue>> getCustomFields(@PathVariable Long id) {
        return ResponseEntity.ok(customFieldService.getCustomFieldValueListByItemId(id));
    }

//    @PostMapping("/customFields")
//    public ResponseEntity<ItemCustomFieldValue> addCustomFieldValue(@RequestBody ItemCustomFieldValue itemCustomFieldValue) {
//        return ResponseEntity.ok(customFieldService.addOrUpdateCustomField(itemCustomFieldValue));
//    }

    @PostMapping("/customFields")
    public ResponseEntity<Boolean> addCustomFieldValue(@RequestBody List<ItemCustomFieldValue> itemCustomFieldValue) {
        return ResponseEntity.ok(customFieldService.addOrUpdateCustomField(itemCustomFieldValue));
    }


//    @PostMapping("/customFields/{id}")
//    public ResponseEntity<Boolean> addCustomFieldValue(@PathVariable Long id, Map<String, Objects> param) {
//
//        List<ItemCustomFieldValue> icfvs = new ArrayList<>();
//        param.forEach((k,v)-> {
//            ItemCustomFieldValue icfv = new ItemCustomFieldValue();
//            icfv.setItemId(id);
//            icfv.setCustomFieldId(Long.valueOf(String.valueOf(k)));
//            icfv.setValue(String.valueOf(v));
//            icfvs.add(icfv);
//        });
//
//        return ResponseEntity.ok(customFieldService.addOrUpdateCustomField(icfvs));
//    }
}
