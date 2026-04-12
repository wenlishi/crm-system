package com.crm.system.modules.lead.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.system.modules.lead.entity.Lead;
import org.apache.ibatis.annotations.Mapper;

/**
 * 销售线索 Mapper 接口
 * 
 * @author wenlishi
 * @since 2026-03-28
 */
@Mapper
public interface LeadMapper extends BaseMapper<Lead> {

}
