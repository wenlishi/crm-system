package com.crm.system.modules.lead.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.system.common.interceptor.CurrentUserContext;
import com.crm.system.modules.customer.entity.Customer;
import com.crm.system.modules.customer.mapper.CustomerMapper;
import com.crm.system.modules.lead.dto.LeadConversionRequest;
import com.crm.system.modules.lead.entity.Lead;
import com.crm.system.modules.lead.entity.LeadConversion;
import com.crm.system.modules.lead.mapper.LeadConversionMapper;
import com.crm.system.modules.lead.mapper.LeadMapper;
import com.crm.system.modules.lead.service.LeadService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 销售线索 Service 实现类
 * 
 * @author wenlishi
 * @since 2026-03-28
 */
@Service
public class LeadServiceImpl extends ServiceImpl<LeadMapper, Lead> implements LeadService {

    private final LeadMapper leadMapper;
    private final LeadConversionMapper leadConversionMapper;
    private final CustomerMapper customerMapper;

    public LeadServiceImpl(LeadMapper leadMapper, 
                          LeadConversionMapper leadConversionMapper,
                          CustomerMapper customerMapper) {
        this.leadMapper = leadMapper;
        this.leadConversionMapper = leadConversionMapper;
        this.customerMapper = customerMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long convertToCustomer(LeadConversionRequest request) {
        // 1. 查询线索
        Lead lead = leadMapper.selectById(request.getLeadId());
        if (lead == null) {
            throw new RuntimeException("线索不存在");
        }

        if (lead.getLeadStatus() == 3) {
            throw new RuntimeException("线索已转化，不能重复转化");
        }

        if (lead.getLeadStatus() == 4) {
            throw new RuntimeException("线索已关闭，不能转化");
        }

        // 2. 创建客户
        Customer customer = new Customer();
        BeanUtils.copyProperties(lead, customer);
        customer.setCustomerId(null); // 清空 ID，让 MyBatis-Plus 生成新 ID
        customer.setCustomerName(request.getCustomerName());
        customer.setCustomerType(request.getCustomerType());
        customer.setLevel(request.getCustomerLevel());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setCompanyName(request.getCompanyName());
        customer.setStatus(1); // 有效
        customer.setDeleted(0);

        customerMapper.insert(customer);

        // 3. 更新线索状态
        lead.setLeadStatus(3); // 已转化
        lead.setConvertedCustomerId(customer.getCustomerId());
        lead.setConvertedTime(LocalDateTime.now());
        leadMapper.updateById(lead);

        // 4. 创建转化记录
        LeadConversion conversion = new LeadConversion();
        conversion.setLeadId(lead.getLeadId());
        conversion.setCustomerId(customer.getCustomerId());
        // 从当前登录用户获取转化人 ID
        conversion.setConvertedBy(CurrentUserContext.getUserId());
        conversion.setLeadStatusBefore(lead.getLeadStatus());
        conversion.setLeadLevelBefore(lead.getLeadLevel());
        conversion.setCustomerLevelAfter(request.getCustomerLevel());
        conversion.setRemark(request.getRemark());

        leadConversionMapper.insert(conversion);

        return customer.getCustomerId();
    }
}
