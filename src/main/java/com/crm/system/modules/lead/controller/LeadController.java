package com.crm.system.modules.lead.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.common.Result;
import com.crm.system.modules.lead.dto.LeadConversionRequest;
import com.crm.system.modules.lead.entity.Lead;
import com.crm.system.modules.lead.service.LeadService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 销售线索 Controller
 * 
 * @author wenlishi
 * @since 2026-03-28
 */
@RestController
@RequestMapping("/lead")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    /**
     * 线索列表
     */
    @GetMapping("/list")
    // @PreAuthorize("hasAuthority('lead:list')")  // 临时禁用权限检查
    public Result<Page<Lead>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer level) {
        
        Page<Lead> page = new Page<>(current, size);
        // TODO: 添加查询条件
        Page<Lead> result = leadService.page(page);
        return Result.success(result);
    }

    /**
     * 线索详情
     */
    @GetMapping("/{id}")
    // @PreAuthorize("hasAuthority('lead:view')")
    public Result<Lead> getById(@PathVariable Long id) {
        Lead lead = leadService.getById(id);
        return Result.success(lead);
    }

    /**
     * 创建线索
     */
    @PostMapping
    // @PreAuthorize("hasAuthority('lead:add')")
    public Result<Lead> add(@RequestBody Lead lead) {
        lead.setLeadStatus(1); // 待联系
        lead.setDeleted(0);
        leadService.save(lead);
        return Result.success(lead);
    }

    /**
     * 更新线索
     */
    @PutMapping("/{id}")
    // @PreAuthorize("hasAuthority('lead:update')")
    public Result<Lead> update(@PathVariable Long id, @RequestBody Lead lead) {
        lead.setLeadId(id);
        leadService.updateById(lead);
        return Result.success(lead);
    }

    /**
     * 删除线索
     */
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasAuthority('lead:delete')")
    public Result<Boolean> delete(@PathVariable Long id) {
        leadService.removeById(id);
        return Result.success(true);
    }

    /**
     * 线索转化为客户
     */
    @PostMapping("/{id}/convert")
    // @PreAuthorize("hasAuthority('lead:convert')")
    public Result<Long> convertToCustomer(
            @PathVariable Long id,
            @RequestBody @Validated LeadConversionRequest request) {
        
        request.setLeadId(id);
        Long customerId = leadService.convertToCustomer(request);
        return Result.success(customerId);
    }

    /**
     * 关闭线索
     */
    @PostMapping("/{id}/close")
    // @PreAuthorize("hasAuthority('lead:close')")
    public Result<Boolean> close(
            @PathVariable Long id,
            @RequestParam String reason) {
        
        Lead lead = leadService.getById(id);
        lead.setLeadStatus(4); // 已关闭
        lead.setClosedReason(reason);
        leadService.updateById(lead);
        
        return Result.success(true);
    }

    /**
     * 待跟进线索列表
     */
    @GetMapping("/follow-up/today")
    // @PreAuthorize("hasAuthority('lead:list')")
    public Result<List<Lead>> getTodayFollowUp() {
        // TODO: 查询今天需要跟进的线索
        return Result.success(List.of());
    }

    /**
     * 线索统计
     */
    @GetMapping("/stats")
    // @PreAuthorize("hasAuthority('lead:list')")
    public Result<Object> getStats() {
        // TODO: 实现线索统计
        return Result.success(null);
    }
}
