package com.crm.system.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.system.modules.system.entity.Dict;
import com.crm.system.modules.system.mapper.DictMapper;
import com.crm.system.modules.system.service.DictService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据字典 Service 实现类
 * 
 * @author wenlishi
 * @since 2026-03-15
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    private final RedisTemplate<String, Object> redisTemplate;

    public DictServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Page<Dict> pageQuery(String dictType, String dictLabel, Integer status, 
                                Integer current, Integer size) {
        Page<Dict> page = new Page<>(current, size);
        
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(dictType), Dict::getDictType, dictType)
               .like(StringUtils.hasText(dictLabel), Dict::getDictLabel, dictLabel)
               .eq(status != null, Dict::getStatus, status)
               .orderByAsc(Dict::getSortOrder);
        
        return this.page(page, wrapper);
    }

    @Override
    public List<Dict> getByDictType(String dictType) {
        // 先从缓存获取
        String cacheKey = "dict:" + dictType;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof List) {
            return (List<Dict>) cached;
        }

        // 缓存未命中，查询数据库
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getDictType, dictType)
               .eq(Dict::getStatus, 1)
               .orderByAsc(Dict::getSortOrder);
        
        List<Dict> list = this.list(wrapper);
        
        // 写入缓存（30 分钟）
        redisTemplate.opsForValue().set(cacheKey, list, 30, java.util.concurrent.TimeUnit.MINUTES);
        
        return list;
    }

    @Override
    public List<Map<String, Object>> getAllDictTypes() {
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Dict::getDictType, Dict::getRemark)
               .eq(Dict::getStatus, 1)
               .groupBy(Dict::getDictType);
        
        List<Dict> list = this.list(wrapper);
        
        return list.stream()
            .map(dict -> {
                Map<String, Object> map = new HashMap<>();
                map.put("dictType", dict.getDictType());
                map.put("remark", dict.getRemark());
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public String getDictLabel(String dictType, String dictValue) {
        List<Dict> dicts = getByDictType(dictType);
        for (Dict dict : dicts) {
            if (dict.getDictValue().equals(dictValue)) {
                return dict.getDictLabel();
            }
        }
        return dictValue;
    }

    @Override
    public void refreshCache() {
        // 清除所有字典缓存
        Set<String> keys = redisTemplate.keys("dict:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
