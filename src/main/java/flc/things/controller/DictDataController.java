package flc.things.controller;

import flc.things.entity.DictData;
import flc.things.service.DictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dict_data")
public class DictDataController {

    @Autowired
    private DictDataService dictDataService;

    @GetMapping
    public List<DictData> getAllDictData() {
        return dictDataService.getAllDictData();
    }

    @PostMapping
    public DictData addDictData(@RequestBody DictData dictData) {
        return dictDataService.addDictData(dictData);
    }

    @GetMapping("getByDictCode")
    public List<DictData> getByDictCode(String dictCode) {
        return dictDataService.getDictDataByDictCode(dictCode);
    }

}
