package com.yamaha.controller;

import com.yamaha.common.Result;
import com.yamaha.entity.Category;
import com.yamaha.service.CategoryService;
import com.yamaha.common.PageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/page")
    public Result<?> page(PageDTO pageDTO) {
        return Result.success(categoryService.page(pageDTO));
    }

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.listAll());
    }

    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Integer id) {
        return Result.success(categoryService.getById(id));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Category category) {
        return Result.success(categoryService.save(category));
    }

    @PutMapping("/{id}")
    public Result<Boolean> updateById(@PathVariable Integer id, @RequestBody Category category) {
        category.setId(id);
        return Result.success(categoryService.updateById(category));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> removeById(@PathVariable Integer id) {
        return Result.success(categoryService.removeById(id));
    }

    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> tree() {
        return Result.success(categoryService.tree());
    }
}