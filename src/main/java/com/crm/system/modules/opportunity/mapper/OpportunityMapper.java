package com.crm.system.modules.opportunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.system.modules.opportunity.entity.Opportunity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商机 Mapper 接口
 */
@Mapper
public interface OpportunityMapper extends BaseMapper<Opportunity> {
}
