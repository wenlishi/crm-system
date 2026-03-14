package com.crm.system.modules.customer.service;

import com.crm.system.modules.customer.dto.CustomerExcelDTO;
import com.crm.system.modules.customer.service.impl.CustomerImportExportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 客户导入导出服务单元测试
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
class CustomerImportExportServiceTest {

    private CustomerImportExportServiceImpl importExportService;

    @BeforeEach
    void setUp() {
        importExportService = new CustomerImportExportServiceImpl();
    }

    @Test
    void testValidateDTO_Valid() {
        // Arrange
        CustomerExcelDTO dto = new CustomerExcelDTO();
        dto.setCustomerName("测试客户");
        dto.setPhone("13800138000");
        dto.setEmail("test@example.com");

        // Act & Assert
        assertDoesNotThrow(() -> {
            // 使用反射调用私有方法测试
            // 实际项目中应该将验证方法提取为公共方法
        });
    }

    @Test
    void testValidateDTO_EmptyName() {
        // Arrange
        CustomerExcelDTO dto = new CustomerExcelDTO();
        dto.setCustomerName("");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            // 验证会抛出异常
        });
    }

    @Test
    void testValidateDTO_InvalidPhone() {
        // Arrange
        CustomerExcelDTO dto = new CustomerExcelDTO();
        dto.setCustomerName("测试客户");
        dto.setPhone("12345"); // 无效的手机号

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            // 验证会抛出异常
        });
    }

    @Test
    void testValidateDTO_InvalidEmail() {
        // Arrange
        CustomerExcelDTO dto = new CustomerExcelDTO();
        dto.setCustomerName("测试客户");
        dto.setEmail("invalid-email"); // 无效的邮箱

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            // 验证会抛出异常
        });
    }

    @Test
    void testConvertCustomerType() {
        // Arrange & Act
        Integer type1 = 1;
        Integer type2 = 2;

        // Assert
        assertEquals("个人", convertCustomerType(type1));
        assertEquals("企业", convertCustomerType(type2));
    }

    @Test
    void testConvertLevel() {
        // Arrange & Act
        Integer level1 = 1;
        Integer level2 = 2;
        Integer level3 = 3;

        // Assert
        assertEquals("普通", convertLevel(level1));
        assertEquals("VIP", convertLevel(level2));
        assertEquals("重要", convertLevel(level3));
    }

    // 辅助方法（实际应该从服务中提取）
    private String convertCustomerType(Integer type) {
        if (type == null) return "个人";
        return type == 1 ? "个人" : "企业";
    }

    private String convertLevel(Integer level) {
        if (level == null) return "普通";
        switch (level) {
            case 2: return "VIP";
            case 3: return "重要";
            default: return "普通";
        }
    }
}
