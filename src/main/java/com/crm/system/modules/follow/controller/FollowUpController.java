package com.crm.system.modules.follow.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.common.Result;
import com.crm.system.modules.follow.entity.FollowUp;
import com.crm.system.modules.follow.service.FollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 跟进记录 Controller
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/follow-ups")
public class FollowUpController {

    @Autowired
    private FollowUpService followUpService;

    /**
     * 查询客户的跟进记录列表
     */
    @GetMapping("/customer/{customerId}")
    public Result<List<FollowUp>> listByCustomerId(@PathVariable Long customerId) {
        List<FollowUp> list = followUpService.listByCustomerId(customerId);
        return Result.success(list);
    }

    /**
     * 分页查询跟进记录
     */
    @GetMapping("/page")
    public Result<Page<FollowUp>> page(
            @RequestParam Long customerId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<FollowUp> page = followUpService.pageByCustomerId(customerId, current, size);
        return Result.success(page);
    }

    /**
     * 根据 ID 查询跟进记录
     */
    @GetMapping("/{id}")
    public Result<FollowUp> getById(@PathVariable Long id) {
        FollowUp followUp = followUpService.getById(id);
        if (followUp == null) {
            return Result.error("跟进记录不存在");
        }
        return Result.success(followUp);
    }

    /**
     * 添加跟进记录
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody FollowUp followUp) {
        boolean success = followUpService.addFollowUp(followUp);
        return success ? Result.success("添加成功", true) : Result.error("添加失败");
    }

    /**
     * 更新跟进记录
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody FollowUp followUp) {
        boolean success = followUpService.updateById(followUp);
        return success ? Result.success("更新成功", true) : Result.error("更新失败");
    }

    /**
     * 删除跟进记录
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = followUpService.removeById(id);
        return success ? Result.success("删除成功", true) : Result.error("删除失败");
    }
}
