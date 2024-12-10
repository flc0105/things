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

    public List<DictData> getAllDictData() {
        return dictDataMapper.selectList(null);
    }

    public DictData getDictDataById(Long id) {
        return dictDataMapper.selectById(id);
    }

    public DictData addDictData(DictData dictData) {
        dictDataMapper.insert(dictData);
        return dictData;
    }

    public DictData updateDictData(Long id, DictData newDictData) {
        newDictData.setId(id);
        dictDataMapper.updateById(newDictData);
        return new DictData();
    }

    public void deleteDictData(Long id) {
        dictDataMapper.deleteById(id);
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
