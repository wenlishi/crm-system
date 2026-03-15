package com.crm.system.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.common.Result;
import com.crm.system.modules.system.entity.Dict;
import com.crm.system.modules.system.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据字典 Controller
 * 
 * @author wenlishi
 * @since 2026-03-15
 */
@RestController
@RequestMapping("/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    /**
     * 分页查询字典
     */
    @GetMapping("/page")
    public Result<Page<Dict>> page(
            @RequestParam(required = false) String dictType,
            @RequestParam(required = false) String dictLabel,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Page<Dict> page = dictService.pageQuery(dictType, dictLabel, status, current, size);
        return Result.success(page);
    }

    /**
     * 根据字典类型查询字典列表
     */
    @GetMapping("/{dictType}")
    public Result<List<Dict>> getByDictType(@PathVariable String dictType) {
        List<Dict> list = dictService.getByDictType(dictType);
        return Result.success(list);
    }

    /**
     * 获取所有字典类型
     */
    @GetMapping("/types")
    public Result<List<Map<String, Object>>> getAllDictTypes() {
        List<Map<String, Object>> types = dictService.getAllDictTypes();
        return Result.success(types);
    }

    /**
     * 根据字典类型和值查询标签
     */
    @GetMapping("/label")
    public Result<String> getDictLabel(
            @RequestParam String dictType,
            @RequestParam String dictValue) {
        
        String label = dictService.getDictLabel(dictType, dictValue);
        return Result.success(label);
    }

    /**
     * 新增字典
     */
    @PostMapping
    public Result<Boolean> save(@RequestBody Dict dict) {
        boolean success = dictService.save(dict);
        if (success) {
            dictService.refreshCache();
        }
        return success ? Result.success("添加成功", true) : Result.error("添加失败");
    }

    /**
     * 修改字典
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody Dict dict) {
        if (dict.getDictId() == null) {
            return Result.error("字典 ID 不能为空");
        }
        
        boolean success = dictService.updateById(dict);
        if (success) {
            dictService.refreshCache();
        }
        return success ? Result.success("修改成功", true) : Result.error("修改失败");
    }

    /**
     * 删除字典
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = dictService.removeById(id);
        if (success) {
            dictService.refreshCache();
        }
        return success ? Result.success("删除成功", true) : Result.error("删除失败");
    }

    /**
     * 刷新字典缓存
     */
    @PostMapping("/refresh")
    public Result<Boolean> refreshCache() {
        dictService.refreshCache();
        return Result.success("缓存已刷新", true);
    }
}
