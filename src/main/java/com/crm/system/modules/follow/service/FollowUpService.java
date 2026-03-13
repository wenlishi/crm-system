package com.crm.system.modules.follow.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.system.modules.follow.entity.FollowUp;

import java.util.List;

/**
 * 跟进记录 Service 接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
public interface FollowUpService extends IService<FollowUp> {

    /**
     * 查询客户的跟进记录
     */
    List<FollowUp> listByCustomerId(Long customerId);

    /**
     * 分页查询跟进记录
     */
    Page<FollowUp> pageByCustomerId(Long customerId, Integer current, Integer size);

    /**
     * 添加跟进记录
     */
    boolean addFollowUp(FollowUp followUp);
}
