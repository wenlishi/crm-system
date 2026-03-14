package com.crm.system.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.modules.system.entity.OperationLog;
import com.crm.system.modules.system.mapper.OperationLogMapper;
import com.crm.system.modules.system.service.OperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 操作日志服务实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public Page<OperationLog> pageLogs(String module, String type, Long userId, Integer current, Integer size) {
        Page<OperationLog> page = new Page<>(current, size);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        // 条件查询
        if (module != null && !module.isEmpty()) {
            wrapper.eq(OperationLog::getModule, module);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(OperationLog::getType, type);
        }
        if (userId != null) {
            wrapper.eq(OperationLog::getUserId, userId);
        }
        
        // 按时间倒序
        wrapper.orderByDesc(OperationLog::getCreateTime);
        
        return page(page, wrapper);
    }

    @Override
    public void logOperation(OperationLog log) {
        save(log);
    }

    @Override
    @Transactional
    public void clearLogs() {
        remove(new LambdaQueryWrapper<>());
    }
}
