package flc.things.controller;

import flc.things.entity.CustomField;
import flc.things.entity.DictData;
import flc.things.service.CustomFieldService;
import flc.things.service.DictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/custom_fields")
public class CustomFieldController {

    @Autowired
    private CustomFieldService customFieldService;

    @GetMapping
    public List<CustomField> getAllCustomFields() {
        return customFieldService.getAllCustomFields();
    }

//    @PostMapping
//    public DictData addDictData(@RequestBody DictData dictData) {
//        return dictDataService.addDictData(dictData);
//    }
//
//    @GetMapping("getByDictCode")
//    public List<DictData> getByDictCode(String dictCode) {
//        return dictDataService.getDictDataByDictCode(dictCode);
//    }

}
