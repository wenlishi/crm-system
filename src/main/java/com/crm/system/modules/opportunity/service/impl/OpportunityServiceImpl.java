package com.crm.system.modules.opportunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.system.modules.customer.entity.Customer;
import com.crm.system.modules.customer.service.CustomerService;
import com.crm.system.modules.opportunity.entity.Opportunity;
import com.crm.system.modules.opportunity.mapper.OpportunityMapper;
import com.crm.system.modules.opportunity.service.OpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商机 Service 实现类
 */
@Service
public class OpportunityServiceImpl extends ServiceImpl<OpportunityMapper, Opportunity> implements OpportunityService {

    @Autowired
    private CustomerService customerService;

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
        Page<Opportunity> result = this.page(page, wrapper);
        
        // 填充客户名称
        if (!result.getRecords().isEmpty()) {
            List<Long> customerIds = result.getRecords().stream()
                .map(Opportunity::getCustomerId)
                .distinct()
                .collect(Collectors.toList());
            
            if (!customerIds.isEmpty()) {
                List<Customer> customers = customerService.listByIds(customerIds);
                Map<Long, Customer> customerMap = new HashMap<>();
                for (Customer c : customers) {
                    customerMap.put(c.getCustomerId(), c);
                }
                
                result.getRecords().forEach(o -> {
                    Customer customer = customerMap.get(o.getCustomerId());
                    if (customer != null) {
                        o.setCustomerName(customer.getCustomerName());
                    }
                });
            }
        }
        
        return result;
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
