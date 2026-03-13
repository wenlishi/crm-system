package com.crm.system.modules.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.system.common.cache.RedisCache;
import com.crm.system.modules.customer.entity.Customer;
import com.crm.system.modules.customer.mapper.CustomerMapper;
import com.crm.system.modules.customer.service.CustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 客户 Service 实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private RedisCache redisCache;

    private static final String CUSTOMER_CACHE_KEY = "customer:";
    private static final String CUSTOMER_LIST_CACHE_KEY = "customer:list";

    @Override
    public List<Customer> listAll() {
        // 1. 先查缓存
        @SuppressWarnings("unchecked")
        List<Customer> cachedList = redisCache.get(CUSTOMER_LIST_CACHE_KEY, List.class);
        if (cachedList != null) {
            return cachedList;
        }

        // 2. 缓存未命中，查数据库
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getStatus, 1)
               .orderByDesc(Customer::getCreateTime);
        List<Customer> list = this.list(wrapper);

        // 3. 写入缓存（30 分钟）
        if (list != null && !list.isEmpty()) {
            redisCache.set(CUSTOMER_LIST_CACHE_KEY, list, 30, TimeUnit.MINUTES);
        }

        return list;
    }

    @Override
    public Customer getById(Long customerId) {
        String key = CUSTOMER_CACHE_KEY + customerId;
        
        // 1. 先查缓存
        Customer cached = redisCache.get(key, Customer.class);
        if (cached != null) {
            return cached;
        }

        // 2. 缓存未命中，查数据库
        Customer customer = super.getById(customerId);

        // 3. 写入缓存（30 分钟）
        if (customer != null) {
            redisCache.set(key, customer, 30, TimeUnit.MINUTES);
        }

        return customer;
    }

    @Override
    public boolean save(Customer customer) {
        // 设置默认状态为有效
        if (customer.getStatus() == null) {
            customer.setStatus(1);
        }
        boolean success = super.save(customer);
        
        // 清除缓存
        if (success) {
            redisCache.delete(CUSTOMER_LIST_CACHE_KEY);
        }
        
        return success;
    }

    @Override
    public boolean update(Customer customer) {
        boolean success = super.updateById(customer);
        
        // 清除缓存
        if (success) {
            String key = CUSTOMER_CACHE_KEY + customer.getCustomerId();
            redisCache.delete(key);
            redisCache.delete(CUSTOMER_LIST_CACHE_KEY);
        }
        
        return success;
    }

    @Override
    public boolean delete(Long customerId) {
        boolean success = super.removeById(customerId);
        
        // 清除缓存
        if (success) {
            String key = CUSTOMER_CACHE_KEY + customerId;
            redisCache.delete(key);
            redisCache.delete(CUSTOMER_LIST_CACHE_KEY);
        }
        
        return success;
    }
}
