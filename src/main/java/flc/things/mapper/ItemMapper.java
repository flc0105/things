package flc.things.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import flc.things.entity.Item;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemMapper extends BaseMapper<Item> {
    // You can add custom queries here if needed
}
