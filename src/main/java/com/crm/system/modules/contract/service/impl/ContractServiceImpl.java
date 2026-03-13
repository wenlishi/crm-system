package com.crm.system.modules.contract.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.modules.contract.entity.Contract;
import com.crm.system.modules.contract.mapper.ContractMapper;
import com.crm.system.modules.contract.service.ContractService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 合同服务实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements ContractService {

    @Override
    public List<Contract> listByCustomer(Long customerId) {
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contract::getCustomerId, customerId)
                .orderByDesc(Contract::getCreateTime);
        return list(wrapper);
    }

    @Override
    public Page<Contract> pageByStatus(Integer status, Integer current, Integer size) {
        Page<Contract> page = new Page<>(current, size);
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Contract::getStatus, status);
        }
        wrapper.orderByDesc(Contract::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public ContractStats getStats() {
        ContractStats stats = new ContractStats();
        
        // 合同总数
        Long total = count();
        stats.setTotal(total.intValue());
        
        // 执行中合同数（状态 4）
        LambdaQueryWrapper<Contract> executingWrapper = new LambdaQueryWrapper<>();
        executingWrapper.eq(Contract::getStatus, 4);
        Long executing = count(executingWrapper);
        stats.setExecuting(executing.intValue());
        
        // 已完成合同数（状态 5）
        LambdaQueryWrapper<Contract> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(Contract::getStatus, 5);
        Long completed = count(completedWrapper);
        stats.setCompleted(completed.intValue());
        
        // 合同总金额
        List<Contract> allContracts = list();
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Contract contract : allContracts) {
            if (contract.getAmount() != null) {
                totalAmount = totalAmount.add(contract.getAmount());
            }
        }
        stats.setTotalAmount(totalAmount);
        
        return stats;
    }
}
