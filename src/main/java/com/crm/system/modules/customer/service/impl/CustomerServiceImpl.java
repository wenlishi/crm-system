package com.crm.system.modules.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.common.annotation.DataPermission;
import com.crm.system.common.annotation.DataType;
import com.crm.system.modules.customer.entity.Customer;
import com.crm.system.modules.customer.mapper.CustomerMapper;
import com.crm.system.modules.customer.service.CustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 客户服务实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    @DataPermission(value = DataType.CUSTOMER, field = "owner_id")
    @Cacheable(value = "customer:list", key = "'all'")
    public List<Customer> listAll() {
        return list();
    }

    @Override
    @DataPermission(value = DataType.CUSTOMER, field = "owner_id")
    @Cacheable(value = "customer:page", key = "#page.current + ':' + #page.size")
    public Page<Customer> page(Page<Customer> page) {
        return super.page(page);
    }

    @Override
    @Cacheable(value = "customer:id", key = "#id")
    public Customer getById(Long id) {
        return super.getById(id);
    }

    @Override
    @CacheEvict(value = "customer:*", allEntries = true)
    public boolean save(Customer customer) {
        return super.save(customer);
    }

    @Override
    @CacheEvict(value = "customer:*", allEntries = true)
    public boolean update(Customer customer) {
        return super.updateById(customer);
    }

    @Override
    @CacheEvict(value = "customer:*", allEntries = true)
    public boolean delete(Long id) {
        return super.removeById(id);
    }
}
