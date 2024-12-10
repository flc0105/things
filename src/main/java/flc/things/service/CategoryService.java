package flc.things.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import flc.things.entity.Category;
import flc.things.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> getAllCategories() {
        return categoryMapper.selectList(null);
    }

    public Category getCategoryById(Long id) {
        return categoryMapper.selectById(id);
    }

    public Category addCategory(Category category) {
        categoryMapper.insert(category);
        return category;
    }

    public Category updateCategory(Long id, Category newCategory) {
        newCategory.setId(id);
        categoryMapper.updateById(newCategory);
        return newCategory;
    }

    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

}
