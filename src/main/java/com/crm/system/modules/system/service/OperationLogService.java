package com.crm.system.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.modules.system.entity.OperationLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 操作日志服务接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 分页查询操作日志
     * 
     * @param module 操作模块（可选）
     * @param type 操作类型（可选）
     * @param userId 用户 ID（可选）
     * @param current 当前页码
     * @param size 每页数量
     * @return 分页结果
     */
    Page<OperationLog> pageLogs(String module, String type, Long userId, Integer current, Integer size);

    /**
     * 记录操作日志
     * 
     * @param log 操作日志
     */
    void logOperation(OperationLog log);

    /**
     * 清空操作日志
     */
    void clearLogs();
}
