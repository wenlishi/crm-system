package com.crm.system.modules.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.system.modules.customer.entity.Customer;
import com.crm.system.modules.customer.mapper.CustomerMapper;
import com.crm.system.modules.customer.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 客户 Service 实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public List<Customer> listAll() {
        // 查询所有有效客户
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getStatus, 1)
               .orderByDesc(Customer::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Customer getById(Long customerId) {
        return this.getById(customerId);
    }

    @Override
    public boolean save(Customer customer) {
        // 设置默认状态为有效
        if (customer.getStatus() == null) {
            customer.setStatus(1);
        }
        return this.save(customer);
    }

    @Override
    public boolean update(Customer customer) {
        return this.updateById(customer);
    }

    @Override
    public boolean delete(Long customerId) {
        return this.removeById(customerId);
    }
}
