package com.crm.system.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.system.modules.system.entity.Dict;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 数据字典 Mapper 接口
 * 
 * @author wenlishi
 * @since 2026-03-15
 */
@Mapper
public interface DictMapper extends BaseMapper<Dict> {

    /**
     * 根据字典类型查询字典列表
     */
    List<Dict> selectByDictType(String dictType);
}
