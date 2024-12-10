package flc.things.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import flc.things.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    // Add custom methods if needed
}