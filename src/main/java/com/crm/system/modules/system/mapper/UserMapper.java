package com.crm.system.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.system.modules.system.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
