package flc.things.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import flc.things.entity.CustomField;
import flc.things.entity.DictData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomFieldMapper extends BaseMapper<CustomField> {
    // Add custom methods if needed
}