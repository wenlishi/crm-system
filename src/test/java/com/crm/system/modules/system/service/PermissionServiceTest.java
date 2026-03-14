package com.crm.system.modules.system.service;

import com.crm.system.common.utils.PermissionChecker;
import com.crm.system.modules.system.entity.Permission;
import com.crm.system.modules.system.service.impl.PermissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 权限服务单元测试
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionServiceImpl permissionService;

    @InjectMocks
    private PermissionChecker permissionChecker;

    private Permission testPermission;

    @BeforeEach
    void setUp() {
        testPermission = new Permission();
        testPermission.setPermissionId(1L);
        testPermission.setPermissionName("客户查询");
        testPermission.setPermissionCode("customer:query");
    }

    @Test
    void testHasPermission_SuperAdmin() {
        // Arrange
        Long superAdminId = 1L;
        String permissionCode = "customer:add";

        // Act
        boolean result = permissionChecker.hasPermission(superAdminId, permissionCode);

        // Assert
        assertTrue(result);
    }

    @Test
    void testHasPermission_HasPermission() {
        // Arrange
        Long userId = 2L;
        String permissionCode = "customer:query";
        List<Permission> permissions = Arrays.asList(testPermission);
        
        when(permissionService.listByUserId(userId)).thenReturn(permissions);

        // Act
        boolean result = permissionChecker.hasPermission(userId, permissionCode);

        // Assert
        assertTrue(result);
        verify(permissionService, times(1)).listByUserId(userId);
    }

    @Test
    void testHasPermission_NoPermission() {
        // Arrange
        Long userId = 2L;
        String permissionCode = "customer:add";
        List<Permission> permissions = Arrays.asList(testPermission);
        
        when(permissionService.listByUserId(userId)).thenReturn(permissions);

        // Act
        boolean result = permissionChecker.hasPermission(userId, permissionCode);

        // Assert
        assertFalse(result);
    }

    @Test
    void testHasAnyPermission_HasOne() {
        // Arrange
        Long userId = 2L;
        String[] permissionCodes = {"customer:add", "customer:query"};
        List<Permission> permissions = Arrays.asList(testPermission);
        
        when(permissionService.listByUserId(userId)).thenReturn(permissions);

        // Act
        boolean result = permissionChecker.hasAnyPermission(userId, permissionCodes);

        // Assert
        assertTrue(result);
    }

    @Test
    void testHasAnyPermission_HasNone() {
        // Arrange
        Long userId = 2L;
        String[] permissionCodes = {"customer:add", "customer:edit"};
        List<Permission> permissions = Arrays.asList(testPermission);
        
        when(permissionService.listByUserId(userId)).thenReturn(permissions);

        // Act
        boolean result = permissionChecker.hasAnyPermission(userId, permissionCodes);

        // Assert
        assertFalse(result);
    }

    @Test
    void testHasAllPermissions_HasAll() {
        // Arrange
        Long userId = 2L;
        String[] permissionCodes = {"customer:query"};
        List<Permission> permissions = Arrays.asList(testPermission);
        
        when(permissionService.listByUserId(userId)).thenReturn(permissions);

        // Act
        boolean result = permissionChecker.hasAllPermissions(userId, permissionCodes);

        // Assert
        assertTrue(result);
    }

    @Test
    void testHasAllPermissions_MissingOne() {
        // Arrange
        Long userId = 2L;
        String[] permissionCodes = {"customer:query", "customer:add"};
        List<Permission> permissions = Arrays.asList(testPermission);
        
        when(permissionService.listByUserId(userId)).thenReturn(permissions);

        // Act
        boolean result = permissionChecker.hasAllPermissions(userId, permissionCodes);

        // Assert
        assertFalse(result);
    }
}
