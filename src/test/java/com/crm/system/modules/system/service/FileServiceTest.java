package com.crm.system.modules.system.service;

import com.crm.system.modules.system.entity.File;
import com.crm.system.modules.system.mapper.FileMapper;
import com.crm.system.modules.system.service.impl.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 文件服务单元测试
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileMapper fileMapper;

    @InjectMocks
    private FileServiceImpl fileService;

    private MultipartFile testFile;

    @BeforeEach
    void setUp() {
        // 准备测试文件
        testFile = new MockMultipartFile(
            "file",
            "test.pdf",
            "application/pdf",
            "test content".getBytes()
        );
    }

    @Test
    void testUpload_Success() {
        // Arrange
        when(fileMapper.insert(any(File.class))).thenReturn(1);

        // Act & Assert
        assertDoesNotThrow(() -> {
            File result = fileService.upload(testFile, "contract", 1L, "测试文件");
            assertNotNull(result);
        });

        verify(fileMapper, times(1)).insert(any(File.class));
    }

    @Test
    void testUpload_EmptyFile() {
        // Arrange
        MultipartFile emptyFile = new MockMultipartFile("file", "", "application/pdf", new byte[0]);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            fileService.upload(emptyFile, "contract", 1L, "测试文件");
        });
    }

    @Test
    void testGetById_Exists() {
        // Arrange
        File testFileEntity = new File();
        testFileEntity.setFileId(1L);
        testFileEntity.setFileName("test.pdf");
        
        when(fileMapper.selectById(1L)).thenReturn(testFileEntity);

        // Act
        File result = fileService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("test.pdf", result.getFileName());
        verify(fileMapper, times(1)).selectById(1L);
    }

    @Test
    void testGetById_NotExists() {
        // Arrange
        when(fileMapper.selectById(999L)).thenReturn(null);

        // Act
        File result = fileService.getById(999L);

        // Assert
        assertNull(result);
        verify(fileMapper, times(1)).selectById(999L);
    }

    @Test
    void testDelete_Success() {
        // Arrange
        File testFileEntity = new File();
        testFileEntity.setFileId(1L);
        testFileEntity.setFilePath("/tmp/test.pdf");
        
        when(fileMapper.selectById(1L)).thenReturn(testFileEntity);
        when(fileMapper.deleteById(1L)).thenReturn(1);

        // Act
        boolean result = fileService.delete(1L);

        // Assert
        assertTrue(result);
        verify(fileMapper, times(1)).deleteById(1L);
    }

    @Test
    void testListByBiz() {
        // Arrange
        // 这里需要实现 listByBiz 的 mock

        // Act & Assert
        // 待实现
    }
}
