package flc.things.service;

import flc.things.entity.Item;
import flc.things.mapper.ItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemMapper itemMapper;

    public List<Item> getAllItems() {
        List<Item> items = itemMapper.selectList(null);
        items.forEach(Item::calculateOwnershipDuration);
        return items;
//        return itemMapper.selectList(null);
    }

    public Optional<Item> getItemById(Long id) {
        return Optional.ofNullable(itemMapper.selectById(id));
    }

    public Item addItem(Item item) {
        itemMapper.insert(item);
        return item;
    }

    public Optional<Item> updateItem(Long id, Item newItem) {
        newItem.setId(id);
        int result = itemMapper.updateById(newItem);
        return result > 0 ? Optional.of(newItem) : Optional.empty();
    }

    public void deleteItem(Long id) {
        itemMapper.deleteById(id);
    }
}
