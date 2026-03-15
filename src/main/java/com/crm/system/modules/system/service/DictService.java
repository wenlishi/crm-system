package com.crm.system.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.system.modules.system.entity.Dict;

import java.util.List;
import java.util.Map;

/**
 * 数据字典 Service 接口
 * 
 * @author wenlishi
 * @since 2026-03-15
 */
public interface DictService extends IService<Dict> {

    /**
     * 分页查询字典
     */
    Page<Dict> pageQuery(String dictType, String dictLabel, Integer status, 
                         Integer current, Integer size);

    /**
     * 根据字典类型查询字典列表
     */
    List<Dict> getByDictType(String dictType);

    /**
     * 获取所有字典类型（分组）
     */
    List<Map<String, Object>> getAllDictTypes();

    /**
     * 根据字典类型和字典值查询字典标签
     */
    String getDictLabel(String dictType, String dictValue);

    /**
     * 刷新字典缓存
     */
    void refreshCache();
}
