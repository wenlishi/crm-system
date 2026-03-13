package com.crm.system.modules.opportunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.system.modules.opportunity.entity.Opportunity;
import com.crm.system.modules.opportunity.mapper.OpportunityMapper;
import com.crm.system.modules.opportunity.service.OpportunityService;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 商机 Service 实现类
 */
@Service
public class OpportunityServiceImpl extends ServiceImpl<OpportunityMapper, Opportunity> implements OpportunityService {

    @Override
    public List<Opportunity> listByCustomerId(Long customerId) {
        LambdaQueryWrapper<Opportunity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Opportunity::getCustomerId, customerId)
               .orderByDesc(Opportunity::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Page<Opportunity> pageByStage(Integer stage, Integer current, Integer size) {
        Page<Opportunity> page = new Page<>(current, size);
        LambdaQueryWrapper<Opportunity> wrapper = new LambdaQueryWrapper<>();
        if (stage != null) {
            wrapper.eq(Opportunity::getStage, stage);
        }
        wrapper.eq(Opportunity::getStatus, 1)
               .orderByDesc(Opportunity::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public Map<String, Object> getStageStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 按阶段统计
        Map<String, Long> byStage = new LinkedHashMap<>();
        byStage.put("初步接触", countByStage(1));
        byStage.put("需求确认", countByStage(2));
        byStage.put("方案报价", countByStage(3));
        byStage.put("谈判", countByStage(4));
        byStage.put("成交", countByStage(5));
        
        stats.put("byStage", byStage);
        
        // 总计
        LambdaQueryWrapper<Opportunity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Opportunity::getStatus, 1);
        stats.put("total", this.count(wrapper));
        
        return stats;
    }

    private Long countByStage(Integer stage) {
        LambdaQueryWrapper<Opportunity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Opportunity::getStage, stage)
               .eq(Opportunity::getStatus, 1);
        return this.count(wrapper);
    }
}
