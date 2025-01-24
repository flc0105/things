package flc.things.service;

import flc.things.entity.Category;
import flc.things.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public Category addCategory(Category category) {
        categoryMapper.insert(category);
        return category;
    }

    public List<Category> getAllCategories() {
        return categoryMapper.selectList(null);
    }

    public Category getCategoryById(Long id) {
        return categoryMapper.selectById(id);
    }


}
