package com.crm.system.modules.lead.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.system.modules.lead.entity.Lead;
import com.crm.system.modules.lead.dto.LeadConversionRequest;

/**
 * 销售线索 Service 接口
 * 
 * @author wenlishi
 * @since 2026-03-28
 */
public interface LeadService extends IService<Lead> {

    /**
     * 线索转化为客户
     * 
     * @param request 转化请求
     * @return 转化后的客户 ID
     */
    Long convertToCustomer(LeadConversionRequest request);
}
