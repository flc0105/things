package flc.things.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import flc.things.entity.Attachment;
import flc.things.entity.Category;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface AttachmentMapper extends BaseMapper<Attachment> {
    // 这里可以定义一些自定义的查询方法，比如根据Item ID查询所有附件
}
