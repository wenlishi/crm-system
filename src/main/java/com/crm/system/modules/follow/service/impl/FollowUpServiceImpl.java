package com.crm.system.modules.follow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.system.modules.customer.entity.Customer;
import com.crm.system.modules.customer.service.CustomerService;
import com.crm.system.modules.follow.entity.FollowUp;
import com.crm.system.modules.follow.mapper.FollowUpMapper;
import com.crm.system.modules.follow.service.FollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 跟进记录 Service 实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Service
public class FollowUpServiceImpl extends ServiceImpl<FollowUpMapper, FollowUp> implements FollowUpService {

    @Autowired
    private CustomerService customerService;

    @Override
    public List<FollowUp> listByCustomerId(Long customerId) {
        LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FollowUp::getCustomerId, customerId)
               .orderByDesc(FollowUp::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Page<FollowUp> pageByCustomerId(Long customerId, Integer current, Integer size) {
        Page<FollowUp> page = new Page<>(current, size);
        LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
        // 如果传入了 customerId，则按 customerId 查询，否则查询所有
        if (customerId != null) {
            wrapper.eq(FollowUp::getCustomerId, customerId);
        }
        wrapper.orderByDesc(FollowUp::getCreateTime);
        Page<FollowUp> result = this.page(page, wrapper);
        
        // 填充客户名称和转换跟进方式
        if (!result.getRecords().isEmpty()) {
            // 获取所有客户 ID
            List<Long> customerIds = result.getRecords().stream()
                .map(FollowUp::getCustomerId)
                .distinct()
                .collect(Collectors.toList());
            
            // 批量查询客户信息
            if (!customerIds.isEmpty()) {
                List<Customer> customers = customerService.listByIds(customerIds);
                Map<Long, Customer> customerMap = new HashMap<>();
                for (Customer c : customers) {
                    customerMap.put(c.getCustomerId(), c);
                }
                
                // 填充客户名称和转换跟进方式
                result.getRecords().forEach(f -> {
                    Customer customer = customerMap.get(f.getCustomerId());
                    if (customer != null) {
                        f.setCustomerName(customer.getCustomerName());
                    }
                    // 转换 followType 为 contactType（phone/wechat/email/visit）
                    f.setContactType(convertFollowType(f.getFollowType()));
                });
            }
        }
        
        return result;
    }
    
    /**
     * 转换 followType 为 contactType：1=phone, 2=wechat, 3=email, 4=visit
     */
    private String convertFollowType(Integer followType) {
        if (followType == null) return "phone";
        switch (followType) {
            case 1: return "phone";    // 电话
            case 2: return "wechat";   // 微信
            case 3: return "email";    // 邮件
            case 4: return "visit";    // 拜访
            default: return "phone";
        }
    }

    @Override
    public boolean addFollowUp(FollowUp followUp) {
        // 处理前端传来的 contactType 字段，转换为 followType
        if (followUp.getContactType() != null) {
            followUp.setFollowType(convertContactType(followUp.getContactType()));
        }
        return super.save(followUp);
    }
    
    /**
     * 转换 contactType 为 followType：phone=1, wechat=2, email=3, visit=4
     */
    private Integer convertContactType(String contactType) {
        if (contactType == null) return 1;
        switch (contactType) {
            case "phone": return 1;   // 电话
            case "wechat": return 2;  // 微信
            case "email": return 3;   // 邮件
            case "visit": return 4;   // 拜访
            default: return 1;
        }
    }
}
