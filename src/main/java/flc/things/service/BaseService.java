package flc.things.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import flc.things.util.TranslatorUtil;

import java.util.List;

public abstract class BaseService<T> {
    protected BaseMapper<T> baseMapper;

    public BaseService(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    // 通用的查询方法，查询所有记录，并对每个记录调用translate方法
    public List<T> getAll() {
        List<T> entities = baseMapper.selectList(null);
        entities.forEach(TranslatorUtil::translate);
        return entities;
    }

    public List<T> list(LambdaQueryWrapper<T> lambdaQueryWrapper) {
        List<T> entities = baseMapper.selectList(lambdaQueryWrapper);
        entities.forEach(TranslatorUtil::translate);
        return entities;
    }

    public T getOne(Long id) {
        T t = baseMapper.selectById(id);
        TranslatorUtil.translate(t);
        return t;
    }
}