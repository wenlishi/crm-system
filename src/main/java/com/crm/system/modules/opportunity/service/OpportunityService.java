package com.crm.system.modules.opportunity.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.system.modules.opportunity.entity.Opportunity;
import java.util.List;
import java.util.Map;

/**
 * 商机 Service 接口
 */
public interface OpportunityService extends IService<Opportunity> {
    List<Opportunity> listByCustomerId(Long customerId);
    Page<Opportunity> pageByStage(Integer stage, Integer current, Integer size);
    Map<String, Object> getStageStats();
}
