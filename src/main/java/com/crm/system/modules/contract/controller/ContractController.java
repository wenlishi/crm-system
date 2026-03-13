package com.crm.system.modules.contract.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.common.Result;
import com.crm.system.modules.contract.entity.Contract;
import com.crm.system.modules.contract.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 合同控制器
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    /**
     * 查询客户的合同列表
     */
    @GetMapping("/customer/{customerId}")
    public Result<List<Contract>> listByCustomer(@PathVariable Long customerId) {
        List<Contract> list = contractService.listByCustomer(customerId);
        return Result.success(list);
    }

    /**
     * 分页查询合同
     */
    @GetMapping("/page")
    public Result<Page<Contract>> page(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Contract> page = contractService.pageByStatus(status, current, size);
        return Result.success(page);
    }

    /**
     * 查询合同详情
     */
    @GetMapping("/{id}")
    public Result<Contract> getById(@PathVariable Long id) {
        Contract contract = contractService.getById(id);
        if (contract == null) {
            return Result.error("合同不存在");
        }
        return Result.success(contract);
    }

    /**
     * 新增合同
     */
    @PostMapping
    public Result<Contract> add(@RequestBody Contract contract) {
        contractService.save(contract);
        return Result.success(contract);
    }

    /**
     * 更新合同
     */
    @PutMapping
    public Result<Contract> update(@RequestBody Contract contract) {
        contractService.updateById(contract);
        return Result.success(contract);
    }

    /**
     * 删除合同
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        contractService.removeById(id);
        return Result.success();
    }

    /**
     * 获取合同统计
     */
    @GetMapping("/stats")
    public Result<ContractService.ContractStats> getStats() {
        ContractService.ContractStats stats = contractService.getStats();
        return Result.success(stats);
    }

    /**
     * 更新合同状态
     */
    @PutMapping("/{id}/status")
    public Result<Contract> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        Contract contract = contractService.getById(id);
        if (contract == null) {
            return Result.error("合同不存在");
        }
        contract.setStatus(status);
        contractService.updateById(contract);
        return Result.success(contract);
    }
}
