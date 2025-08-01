package flc.things.controller;

import flc.things.entity.CustomField;
import flc.things.entity.Item;
import flc.things.entity.ItemCustomFieldValue;
import flc.things.service.CustomFieldService;
import flc.things.service.ItemCustomFieldValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/custom_fields")
public class CustomFieldController {

    @Autowired
    private CustomFieldService customFieldService;

    @Autowired
    private ItemCustomFieldValueService itemCustomFieldValueService;

    @PostMapping
    public CustomField add(@RequestBody CustomField customField) {
        return customFieldService.addCustomField(customField);
    }

    @PostMapping("/values")
    public ResponseEntity<Boolean> addOrUpdateCustomFieldValue(@RequestBody List<ItemCustomFieldValue> itemCustomFieldValue) {
        return ResponseEntity.ok(itemCustomFieldValueService.addOrUpdateItemCustomFieldValue(itemCustomFieldValue));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customFieldService.deleteCustomField(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomField> update(@PathVariable Long id, @RequestBody CustomField newCustomField) {
        return ResponseEntity.ok(customFieldService.updateCustomField(id, newCustomField));
    }

    @PutMapping("/{id}/enabled/{enabled}")
    public ResponseEntity<CustomField> setEnabled(@PathVariable Long id, @PathVariable boolean enabled) {
        return ResponseEntity.ok(customFieldService.setEnabled(id, enabled));
    }

    @GetMapping
    public List<CustomField> getAllCustomFields() {
        return customFieldService.getAllCustomFields();
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<List<ItemCustomFieldValue>> getCustomFieldValueByItemId(@PathVariable Long id) {
        return ResponseEntity.ok(itemCustomFieldValueService.getItemCustomFieldValueListByItemId(id));
    }

    @GetMapping("/values")
    public Map<String, List<String>> getAllValues() {
        return itemCustomFieldValueService.findAllFieldNamesAndValues();
    }

    @GetMapping("/items")
    public List<Item> getItemsByCustomFieldIdAndValue(@RequestParam  Map<String, String> map) {
        return itemCustomFieldValueService.getItemsByCustomFieldIdAndValue(map.get("fieldId"), map.get("fieldValue"));
    }


}
