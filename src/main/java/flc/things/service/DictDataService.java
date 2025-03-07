package flc.things.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import flc.things.entity.Category;
import flc.things.entity.DictData;
import flc.things.mapper.DictDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictDataService {

    @Autowired
    private DictDataMapper dictDataMapper;

    public DictData addDictData(DictData dictData) {
        dictDataMapper.insert(dictData);
        return dictData;
    }

    public List<DictData> getAllDictData() {
        return dictDataMapper.selectList(null);
    }

    public List<DictData> getDictDataByDictCode(String dictCode) {
        return dictDataMapper.selectList(new LambdaQueryWrapper<DictData>().eq(DictData::getDictCode, dictCode));

    }

    public DictData getDictDataByDictCodeAndCode(String dictCode, String code) {
        return dictDataMapper.selectOne(new LambdaQueryWrapper<DictData>()
                .eq(DictData::getDictCode, dictCode)
                .eq(DictData::getCode, code));
    }

}
