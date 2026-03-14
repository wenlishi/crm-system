package com.crm.system.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.system.modules.system.entity.Dept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 部门 Mapper 接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Mapper
public interface DeptMapper extends BaseMapper<Dept> {

    /**
     * 查询部门树形结构
     * 
     * @param parentId 父部门 ID
     * @return 部门列表
     */
    List<Dept> selectDeptTree(Long parentId);
}
