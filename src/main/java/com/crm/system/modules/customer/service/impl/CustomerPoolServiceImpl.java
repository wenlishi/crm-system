package com.crm.system.modules.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.common.annotation.OperationLog;
import com.crm.system.modules.customer.entity.Customer;
import com.crm.system.modules.customer.entity.CustomerPool;
import com.crm.system.modules.customer.mapper.CustomerPoolMapper;
import com.crm.system.modules.customer.service.CustomerPoolService;
import com.crm.system.modules.customer.service.CustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 公海池服务实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Service
public class CustomerPoolServiceImpl extends ServiceImpl<CustomerPoolMapper, CustomerPool> implements CustomerPoolService {

    @Autowired
    private CustomerService customerService;

    @Override
    public Page<CustomerPool> pagePool(Integer current, Integer size) {
        Page<CustomerPool> page = new Page<>(current, size);
        LambdaQueryWrapper<CustomerPool> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerPool::getStatus, 0) // 只在池中的
                .orderByDesc(CustomerPool::getDropTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional
    @OperationLog(module = "公海池", type = "掉入", description = "客户掉入公海池")
    public void dropToPool(Long customerId, Integer reason, String remark) {
        // 查询客户信息
        Customer customer = customerService.getById(customerId);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        // 检查是否已在公海池
        LambdaQueryWrapper<CustomerPool> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(CustomerPool::getCustomerId, customerId)
                .eq(CustomerPool::getStatus, 0);
        Long count = count(checkWrapper);
        if (count > 0) {
            throw new RuntimeException("客户已在公海池中");
        }

        // 创建公海池记录
        CustomerPool pool = new CustomerPool();
        pool.setCustomerId(customerId);
        pool.setCustomerName(customer.getCustomerName());
        pool.setPreviousOwnerId(customer.getOwnerId());
        pool.setPreviousOwnerName("用户-" + customer.getOwnerId()); // TODO: 查询用户名
        pool.setDropReason(reason);
        pool.setDropTime(LocalDateTime.now());
        pool.setStatus(0); // 在池中
        pool.setRemark(remark);
        save(pool);

        // 清空客户的负责人（变为公海客户）
        customer.setOwnerId(null);
        customer.setStatus(0); // 无效状态
        customer.setNextFollowTime(null);
        customerService.updateById(customer);
    }

    @Override
    @Transactional
    @OperationLog(module = "公海池", type = "领取", description = "领取公海池客户")
    public boolean claimCustomer(Long poolId, Long userId) {
        // 查询公海池记录
        CustomerPool pool = getById(poolId);
        if (pool == null || pool.getStatus() != 0) {
            throw new RuntimeException("公海池记录不存在或已被领取");
        }

        // 更新客户负责人
        Customer customer = customerService.getById(pool.getCustomerId());
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        customer.setOwnerId(userId);
        customer.setStatus(1); // 有效状态
        customerService.updateById(customer);

        // 更新公海池记录
        pool.setStatus(1); // 已领取
        updateById(pool);

        return true;
    }

    @Override
    @Transactional
    @OperationLog(module = "公海池", type = "分配", description = "分配公海池客户")
    public boolean assignCustomer(Long poolId, Long userId) {
        // 与领取类似，但由管理员操作
        return claimCustomer(poolId, userId);
    }

    @Override
    public PoolStats getStats() {
        PoolStats stats = new PoolStats();

        // 公海池总数
        LambdaQueryWrapper<CustomerPool> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(CustomerPool::getStatus, 0);
        Long total = count(totalWrapper);
        stats.setTotal(total.intValue());

        // 今日掉入
        LambdaQueryWrapper<CustomerPool> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(CustomerPool::getStatus, 0)
                .ge(CustomerPool::getDropTime, LocalDate.now().atStartOfDay());
        Long todayDrop = count(todayWrapper);
        stats.setTodayDrop(todayDrop.intValue());

        // 本周掉入
        LambdaQueryWrapper<CustomerPool> weekWrapper = new LambdaQueryWrapper<>();
        weekWrapper.eq(CustomerPool::getStatus, 0)
                .ge(CustomerPool::getDropTime, LocalDate.now().minusDays(7).atStartOfDay());
        Long weekDrop = count(weekWrapper);
        stats.setWeekDrop(weekDrop.intValue());

        // 已领取
        LambdaQueryWrapper<CustomerPool> claimedWrapper = new LambdaQueryWrapper<>();
        claimedWrapper.eq(CustomerPool::getStatus, 1);
        Long claimed = count(claimedWrapper);
        stats.setClaimed(claimed.intValue());

        return stats;
    }
}
