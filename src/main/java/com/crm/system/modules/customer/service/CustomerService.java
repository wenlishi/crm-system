package com.crm.system.modules.customer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.system.modules.customer.entity.Customer;

import java.util.List;

/**
 * 客户 Service 接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
public interface CustomerService extends IService<Customer> {

    /**
     * 查询所有客户
     */
    List<Customer> listAll();

    /**
     * 根据 ID 查询客户
     */
    Customer getById(Long customerId);

    /**
     * 新增客户
     */
    boolean save(Customer customer);

    /**
     * 更新客户
     */
    boolean update(Customer customer);

    /**
     * 删除客户
     */
    boolean delete(Long customerId);
}
