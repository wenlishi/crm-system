package com.crm.system.modules.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.system.modules.customer.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户 Mapper 接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

}
