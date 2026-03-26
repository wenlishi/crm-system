package com.crm.system.modules.contract.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.modules.contract.entity.Contract;
import com.crm.system.modules.contract.mapper.ContractMapper;
import com.crm.system.modules.contract.service.ContractService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.system.modules.customer.entity.Customer;
import com.crm.system.modules.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 合同服务实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements ContractService {

    @Autowired
    private CustomerService customerService;

    @Override
    public List<Contract> listByCustomer(Long customerId) {
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contract::getCustomerId, customerId)
                .orderByDesc(Contract::getCreateTime);
        return list(wrapper);
    }

    @Override
    public boolean saveContract(Contract contract) {
        // 处理前端传来的 effectiveDate 和 expiryDate 字段
        if (contract.getEffectiveDate() != null) {
            contract.setStartDate(contract.getEffectiveDate());
        }
        if (contract.getExpiryDate() != null) {
            contract.setEndDate(contract.getExpiryDate());
        }
        
        // 自动生成合同编号（如果前端没有传）
        if (contract.getContractNo() == null || contract.getContractNo().isEmpty()) {
            contract.setContractNo(generateContractNo());
        }
        
        return super.save(contract);
    }
    
    /**
     * 自动生成合同编号：HT + 年月日 + 4 位随机数
     */
    private String generateContractNo() {
        String dateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = String.format("%04d", (int)(Math.random() * 10000));
        return "HT" + dateStr + randomStr;
    }

    @Override
    public boolean updateContract(Contract contract) {
        // 处理前端传来的 effectiveDate 和 expiryDate 字段
        if (contract.getEffectiveDate() != null) {
            contract.setStartDate(contract.getEffectiveDate());
        }
        if (contract.getExpiryDate() != null) {
            contract.setEndDate(contract.getExpiryDate());
        }
        return super.updateById(contract);
    }

    @Override
    public Contract getById(Long id) {
        Contract contract = super.getById(id);
        if (contract != null) {
            // 填充 effectiveDate 和 expiryDate（前端使用）
            contract.setEffectiveDate(contract.getStartDate());
            contract.setExpiryDate(contract.getEndDate());
        }
        return contract;
    }

    @Override
    public Page<Contract> pageByStatus(Integer status, Integer current, Integer size) {
        return pageByCondition(null, null, status, current, size);
    }

    @Override
    public Page<Contract> pageByCondition(String contractName, String customerName, Integer status, Integer current, Integer size) {
        Page<Contract> page = new Page<>(current, size);
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        
        // 合同名称模糊查询
        if (contractName != null && !contractName.isEmpty()) {
            wrapper.like(Contract::getContractName, contractName);
        }
        
        // 客户名称模糊查询（需要先查询客户表获取客户 ID）
        if (customerName != null && !customerName.isEmpty()) {
            LambdaQueryWrapper<Customer> customerWrapper = new LambdaQueryWrapper<>();
            customerWrapper.like(Customer::getCustomerName, customerName);
            List<Customer> matchingCustomers = customerService.list(customerWrapper);
            
            if (!matchingCustomers.isEmpty()) {
                List<Long> customerIds = matchingCustomers.stream()
                    .map(Customer::getCustomerId)
                    .collect(Collectors.toList());
                wrapper.in(Contract::getCustomerId, customerIds);
            } else {
                // 如果没有匹配的客户，返回空结果
                return new Page<>(current, size, 0);
            }
        }
        
        // 合同状态查询
        if (status != null) {
            wrapper.eq(Contract::getStatus, status);
        }
        
        wrapper.orderByDesc(Contract::getCreateTime);
        Page<Contract> result = page(page, wrapper);
        
        // 填充客户名称和日期字段
        if (!result.getRecords().isEmpty()) {
            List<Long> customerIds = result.getRecords().stream()
                .map(Contract::getCustomerId)
                .distinct()
                .collect(Collectors.toList());
            
            if (!customerIds.isEmpty()) {
                List<Customer> customers = customerService.listByIds(customerIds);
                Map<Long, Customer> customerMap = new HashMap<>();
                for (Customer c : customers) {
                    customerMap.put(c.getCustomerId(), c);
                }
                
                result.getRecords().forEach(c -> {
                    Customer customer = customerMap.get(c.getCustomerId());
                    if (customer != null) {
                        c.setCustomerName(customer.getCustomerName());
                    }
                    c.setEffectiveDate(c.getStartDate());
                    c.setExpiryDate(c.getEndDate());
                });
            }
        }
        
        return result;
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
