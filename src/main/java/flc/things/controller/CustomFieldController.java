package flc.things.controller;

import flc.things.entity.CustomField;
import flc.things.service.CustomFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/custom_fields")
public class CustomFieldController {

    @Autowired
    private CustomFieldService customFieldService;

    @PostMapping
    public CustomField add(@RequestBody CustomField customField) {
        return customFieldService.addCustomField(customField);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomField> update(@PathVariable Long id, @RequestBody CustomField newCustomField) {
        return ResponseEntity.ok(customFieldService.update(id, newCustomField));
    }

    @PutMapping("/{id}/enabled/{enabled}")
    public ResponseEntity<CustomField> setEnabled(@PathVariable Long id, @PathVariable boolean enabled) {
        return ResponseEntity.ok(customFieldService.setEnabled(id, enabled));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customFieldService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<CustomField> getAllCustomFields() {
        return customFieldService.getAllCustomFields();
    }

    @GetMapping("/suggestions/{customFieldId}")
    public List<String> getSuggestions(@PathVariable Long customFieldId) {
        return customFieldService.getSuggestions(customFieldId);
    }


}
