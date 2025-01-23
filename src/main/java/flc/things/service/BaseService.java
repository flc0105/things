package flc.things.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import flc.things.util.TranslatorUtil;

import java.util.List;

public abstract class BaseService<T> {

    // 假设有一个通用的Mapper接口，所有具体的Mapper应该实现这个接口
    protected BaseMapper<T> baseMapper;

    // 构造函数，用于注入具体的Mapper和翻译工具类
    public BaseService(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    // 通用的查询方法，查询所有记录，并对每个记录调用translate方法
    public List<T> getAll() {
        List<T> entities = baseMapper.selectList(null);
        entities.forEach(TranslatorUtil::translate);
        return entities;
    }

    public List<T> list(LambdaQueryWrapper<T> qw) {
        List<T> entities = baseMapper.selectList(qw);
        entities.forEach(TranslatorUtil::translate);
        return entities;
    }

    public T getOne(Long id) {
        T t = baseMapper.selectById(id);
        TranslatorUtil.translate(t);
        return t;
    }
}