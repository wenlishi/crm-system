package com.crm.system.modules.customer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.common.Result;
import com.crm.system.modules.customer.entity.Customer;
import com.crm.system.modules.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户 Controller
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 查询客户列表
     */
    @GetMapping
    public Result<List<Customer>> list() {
        List<Customer> list = customerService.listAll();
        return Result.success(list);
    }

    /**
     * 分页查询客户列表
     */
    @GetMapping("/page")
    public Result<Page<Customer>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Customer> page = new Page<>(current, size);
        Page<Customer> result = customerService.page(page);
        return Result.success(result);
    }

    /**
     * 根据 ID 查询客户
     */
    @GetMapping("/{id}")
    public Result<Customer> getById(@PathVariable Long id) {
        Customer customer = customerService.getById(id);
        if (customer == null) {
            return Result.error("客户不存在");
        }
        return Result.success(customer);
    }

    /**
     * 新增客户
     */
    @PostMapping
    public Result<Boolean> save(@RequestBody Customer customer) {
        boolean success = customerService.save(customer);
        return success ? Result.success("添加成功", true) : Result.error("添加失败");
    }

    /**
     * 更新客户
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody Customer customer) {
        boolean success = customerService.update(customer);
        return success ? Result.success("更新成功", true) : Result.error("更新失败");
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = customerService.delete(id);
        return success ? Result.success("删除成功", true) : Result.error("删除失败");
    }
}
