package com.yamaha.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yamaha.entity.Category;
import com.yamaha.mapper.CategoryMapper;
import com.yamaha.service.CategoryService;
import com.yamaha.common.PageDTO;
import com.yamaha.common.PageResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public PageResult<Category> page(PageDTO pageDTO) {
        IPage<Category> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        wrapper.orderByAsc("parent_id").orderByAsc("id");
        page = baseMapper.selectPage(page, wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public List<Category> listAll() {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        wrapper.orderByAsc("parent_id").orderByAsc("id");
        return baseMapper.selectList(wrapper);
    }

    @Override
    public Category getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public boolean save(Category category) {
        category.setDeleted(0);
        category.setStatus(1);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        return baseMapper.insert(category) > 0;
    }

    @Override
    public boolean updateById(Category category) {
        category.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(category) > 0;
    }

    @Override
    public boolean removeById(Long id) {
        Category category = new Category();
        category.setId(id);
        category.setDeleted(1);
        category.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(category) > 0;
    }

    @Override
    public List<Map<String, Object>> tree() {
        List<Category> categories = listAll();
        List<Map<String, Object>> result = new ArrayList<>();
        Map<Long, Map<String, Object>> map = new HashMap<>();

        // 构建所有分类的Map
        for (Category category : categories) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", category.getId());
            item.put("name", category.getName());
            item.put("level", category.getLevel());
            item.put("children", new ArrayList<Map<String, Object>>());
            map.put(category.getId(), item);
        }

        // 构建树形结构
        for (Category category : categories) {
            if (category.getParentId() == 0) {
                // 一级分类
                result.add(map.get(category.getId()));
            } else {
                // 子分类
                Map<String, Object> parent = map.get(category.getParentId());
                if (parent != null) {
                    List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get("children");
                    children.add(map.get(category.getId()));
                }
            }
        }

        return result;
    }
}