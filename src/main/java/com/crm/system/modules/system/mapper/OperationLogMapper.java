package com.crm.system.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.system.modules.system.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper 接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

}
