package com.crm.system.modules.system.service;

import com.crm.system.modules.system.entity.File;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
public interface FileService {

    /**
     * 上传文件
     * 
     * @param file 上传的文件
     * @param bizType 业务类型
     * @param bizId 业务 ID
     * @param description 文件描述
     * @return 文件信息
     */
    File upload(MultipartFile file, String bizType, Long bizId, String description);

    /**
     * 下载文件
     * 
     * @param fileId 文件 ID
     * @return 文件字节数组
     */
    byte[] download(Long fileId);

    /**
     * 删除文件
     * 
     * @param fileId 文件 ID
     * @return 是否成功
     */
    boolean delete(Long fileId);

    /**
     * 根据业务类型和 ID 查询文件列表
     * 
     * @param bizType 业务类型
     * @param bizId 业务 ID
     * @return 文件列表
     */
    java.util.List<File> listByBiz(String bizType, Long bizId);

    /**
     * 根据 ID 查询文件
     * 
     * @param fileId 文件 ID
     * @return 文件信息
     */
    File getById(Long fileId);
}
