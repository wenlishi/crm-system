package com.crm.system.modules.customer.controller;

import com.crm.system.common.Result;
import com.crm.system.common.annotation.OperationLog;
import com.crm.system.common.annotation.RequirePermission;
import com.crm.system.common.utils.UserContext;
import com.crm.system.modules.customer.service.CustomerImportExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 客户导入导出控制器
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerImportExportController {

    @Autowired
    private CustomerImportExportService importExportService;

    /**
     * 导出客户数据
     */
    @GetMapping("/export")
    @RequirePermission("customer:export")
    @OperationLog(module = "客户管理", type = "导出", description = "导出客户数据")
    public void export(@RequestParam(required = false) List<Long> ids, HttpServletResponse response) {
        importExportService.exportCustomers(response, ids);
    }

    /**
     * 导入客户数据
     */
    @PostMapping("/import")
    @RequirePermission("customer:import")
    @OperationLog(module = "客户管理", type = "导入", description = "导入客户数据")
    public Result<CustomerImportExportService.ImportResult> importData(
            @RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        Long userId = UserContext.getUserId();
        CustomerImportExportService.ImportResult result = importExportService.importCustomers(file, userId);
        return Result.success("导入完成，成功 " + result.getSuccess() + " 条，失败 " + result.getFail() + " 条", result);
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/download-template")
    @RequirePermission("customer:import")
    @OperationLog(module = "客户管理", type = "下载模板", description = "下载客户导入模板")
    public void downloadTemplate(HttpServletResponse response) {
        importExportService.downloadTemplate(response);
    }
}
