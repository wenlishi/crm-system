package com.crm.system.modules.system.controller;

import com.crm.system.common.Result;
import com.crm.system.common.annotation.OperationLog;
import com.crm.system.common.annotation.RequirePermission;
import com.crm.system.modules.system.entity.File;
import com.crm.system.modules.system.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 文件上传控制器
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    @RequirePermission("system:file:upload")
    @OperationLog(module = "文件管理", type = "上传", description = "上传文件")
    public Result<File> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String bizType,
            @RequestParam(required = false) Long bizId,
            @RequestParam(required = false) String description) {
        File fileEntity = fileService.upload(file, bizType, bizId, description);
        return Result.success("上传成功", fileEntity);
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{fileId}")
    @RequirePermission("system:file:download")
    @OperationLog(module = "文件管理", type = "下载", description = "下载文件")
    public void download(@PathVariable Long fileId, HttpServletResponse response) throws IOException {
        byte[] data = fileService.download(fileId);
        File file = fileService.getById(fileId);
        
        response.setContentType(file.getMimeType());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getOriginalName() + "\"");
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }

    /**
     * 预览文件（图片等）
     */
    @GetMapping("/preview/{fileId}")
    public void preview(@PathVariable Long fileId, HttpServletResponse response) throws IOException {
        byte[] data = fileService.download(fileId);
        File file = fileService.getById(fileId);
        
        response.setContentType(file.getMimeType());
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{fileId}")
    @RequirePermission("system:file:delete")
    @OperationLog(module = "文件管理", type = "删除", description = "删除文件")
    public Result<Void> delete(@PathVariable Long fileId) {
        fileService.delete(fileId);
        return Result.success("删除成功");
    }

    /**
     * 查询业务相关文件列表
     */
    @GetMapping("/biz/{bizType}/{bizId}")
    @RequirePermission("system:file:query")
    public Result<List<File>> listByBiz(@PathVariable String bizType, @PathVariable Long bizId) {
        List<File> list = fileService.listByBiz(bizType, bizId);
        return Result.success(list);
    }

    /**
     * 查询文件详情
     */
    @GetMapping("/{fileId}")
    @RequirePermission("system:file:query")
    public Result<File> getById(@PathVariable Long fileId) {
        File file = fileService.getById(fileId);
        if (file == null) {
            return Result.error("文件不存在");
        }
        return Result.success(file);
    }
}
