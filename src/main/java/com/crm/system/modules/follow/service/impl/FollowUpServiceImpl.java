package com.crm.system.modules.follow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.system.modules.follow.entity.FollowUp;
import com.crm.system.modules.follow.mapper.FollowUpMapper;
import com.crm.system.modules.follow.service.FollowUpService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 跟进记录 Service 实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Service
public class FollowUpServiceImpl extends ServiceImpl<FollowUpMapper, FollowUp> implements FollowUpService {

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
        wrapper.eq(FollowUp::getCustomerId, customerId)
               .orderByDesc(FollowUp::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public boolean addFollowUp(FollowUp followUp) {
        return super.save(followUp);
    }
}
