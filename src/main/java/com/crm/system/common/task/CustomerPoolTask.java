package com.crm.system.common.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.system.modules.customer.entity.Customer;
import com.crm.system.modules.customer.entity.CustomerPool;
import com.crm.system.modules.customer.service.CustomerPoolService;
import com.crm.system.modules.customer.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公海池定时任务
 * 
 * 自动将超期未跟进的客户掉入公海池
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Component
public class CustomerPoolTask {

    private static final Logger log = LoggerFactory.getLogger(CustomerPoolTask.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerPoolService customerPoolService;

    /**
     * 每天凌晨 2 点执行
     * 检查超期未跟进的客户，自动掉入公海池
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void autoDropToPool() {
        log.info("开始执行公海池定时任务：检查超期未跟进客户");

        // 查询超期未跟进的客户（下次跟进时间 < 现在，且状态为有效）
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(Customer::getNextFollowTime, LocalDateTime.now())
                .eq(Customer::getStatus, 1) // 有效客户
                .isNotNull(Customer::getOwnerId); // 有负责人的

        List<Customer> customers = customerService.list(wrapper);
        
        if (customers.isEmpty()) {
            log.info("没有找到超期未跟进的客户");
            return;
        }

        log.info("找到 {} 个超期未跟进的客户", customers.size());

        int successCount = 0;
        int failCount = 0;

        // 逐个掉入公海池
        for (Customer customer : customers) {
            try {
                customerPoolService.dropToPool(
                    customer.getCustomerId(),
                    1, // 超期未跟进
                    "系统自动掉入：超期 " + 
                    java.time.Duration.between(customer.getNextFollowTime(), LocalDateTime.now()).toDays() + 
                    " 天未跟进"
                );
                successCount++;
                log.info("客户 {} 已掉入公海池", customer.getCustomerName());
            } catch (Exception e) {
                failCount++;
                log.error("客户 {} 掉入公海池失败：{}", customer.getCustomerName(), e.getMessage());
            }
        }

        log.info("公海池定时任务执行完成：成功 {} 个，失败 {} 个", successCount, failCount);
    }

    /**
     * 每周一早上 8 点执行
     * 发送公海池周报（待实现）
     */
    @Scheduled(cron = "0 0 8 ? * MON")
    public void weeklyReport() {
        log.info("生成公海池周报（待实现）");
        // TODO: 发送周报邮件
    }
}
