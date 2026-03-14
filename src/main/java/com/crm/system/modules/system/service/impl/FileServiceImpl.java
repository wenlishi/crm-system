package com.crm.system.modules.system.service.impl;

import com.crm.system.common.config.FileUploadProperties;
import com.crm.system.common.utils.UserContext;
import com.crm.system.modules.system.entity.File;
import com.crm.system.modules.system.mapper.FileMapper;
import com.crm.system.modules.system.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileUploadProperties uploadProperties;

    @Autowired
    private FileMapper fileMapper;

    @Override
    public File upload(MultipartFile file, String bizType, Long bizId, String description) {
        try {
            // 1. 验证文件
            validateFile(file);

            // 2. 生成文件路径
            String subDir = generateSubDir();
            String fileName = generateFileName(file);
            String filePath = uploadProperties.getLocation() + "/" + subDir + "/" + fileName;

            // 3. 确保目录存在
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());

            // 4. 保存文件
            file.transferTo(path);

            // 5. 获取文件信息
            long fileSize = file.getSize();
            String mimeType = file.getContentType();
            String originalName = file.getOriginalFilename();
            String extension = getFileExtension(originalName);
            String fileType = getFileType(extension);

            // 6. 生成访问 URL
            String fileUrl = uploadProperties.getUrlPrefix() + "/" + subDir + "/" + fileName;

            // 7. 保存到数据库
            File fileEntity = new File();
            fileEntity.setFileName(fileName);
            fileEntity.setOriginalName(originalName);
            fileEntity.setFilePath(filePath);
            fileEntity.setFileUrl(fileUrl);
            fileEntity.setFileType(fileType);
            fileEntity.setFileSize(fileSize);
            fileEntity.setMimeType(mimeType);
            fileEntity.setBizType(bizType);
            fileEntity.setBizId(bizId);
            fileEntity.setUploadUserId(UserContext.getUserId());
            fileEntity.setUploadUserName(UserContext.getUsername());
            fileEntity.setDescription(description);
            fileEntity.setStatus(1);
            fileMapper.insert(fileEntity);

            log.info("文件上传成功：{} ({} bytes)", originalName, fileSize);
            return fileEntity;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public byte[] download(Long fileId) {
        File file = fileMapper.selectById(fileId);
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }

        try {
            Path path = Paths.get(file.getFilePath());
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("文件下载失败", e);
            throw new RuntimeException("文件下载失败：" + e.getMessage());
        }
    }

    @Override
    public boolean delete(Long fileId) {
        File file = fileMapper.selectById(fileId);
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }

        try {
            // 删除物理文件
            Path path = Paths.get(file.getFilePath());
            Files.deleteIfExists(path);

            // 删除数据库记录
            fileMapper.deleteById(fileId);

            log.info("文件删除成功：{}", file.getOriginalName());
            return true;
        } catch (IOException e) {
            log.error("文件删除失败", e);
            throw new RuntimeException("文件删除失败：" + e.getMessage());
        }
    }

    @Override
    public List<File> listByBiz(String bizType, Long bizId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<File> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(File::getBizType, bizType)
               .eq(File::getBizId, bizId)
               .orderByDesc(File::getCreateTime);
        return fileMapper.selectList(wrapper);
    }

    @Override
    public File getById(Long fileId) {
        return fileMapper.selectById(fileId);
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > uploadProperties.getMaxSize()) {
            throw new RuntimeException("文件大小不能超过 " + (uploadProperties.getMaxSize() / 1024 / 1024) + "MB");
        }

        // 检查文件扩展名
        String originalName = file.getOriginalFilename();
        String extension = getFileExtension(originalName);
        boolean allowed = false;
        for (String allowedExt : uploadProperties.getAllowedExtensions()) {
            if (allowedExt.equalsIgnoreCase(extension)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new RuntimeException("不支持的文件类型：" + extension);
        }
    }

    /**
     * 生成子目录（按日期）
     */
    private String generateSubDir() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }

    /**
     * 生成文件名
     */
    private String generateFileName(MultipartFile file) {
        String extension = getFileExtension(file.getOriginalFilename());
        return UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot == -1) {
            return "";
        }
        return fileName.substring(lastDot + 1).toLowerCase();
    }

    /**
     * 获取文件类型
     */
    private String getFileType(String extension) {
        if (extension == null || extension.isEmpty()) {
            return "other";
        }
        
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
            case "webp":
                return "image";
            case "pdf":
                return "pdf";
            case "doc":
            case "docx":
                return "doc";
            case "xls":
            case "xlsx":
                return "xls";
            case "ppt":
            case "pptx":
                return "ppt";
            case "txt":
            case "csv":
                return "text";
            case "zip":
            case "rar":
            case "7z":
                return "archive";
            default:
                return "other";
        }
    }
}
