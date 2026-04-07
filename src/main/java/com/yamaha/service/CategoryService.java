package com.yamaha.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yamaha.entity.Category;
import com.yamaha.common.PageDTO;
import com.yamaha.common.PageResult;

import java.util.List;
import java.util.Map;

public interface CategoryService extends IService<Category> {
    PageResult<Category> page(PageDTO pageDTO);
    List<Category> listAll();
    Category getById(Integer id);
    boolean save(Category category);
    boolean updateById(Category category);
    boolean removeById(Integer id);
    List<Map<String, Object>> tree();
}