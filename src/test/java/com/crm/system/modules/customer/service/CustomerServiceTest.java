package com.crm.system.modules.customer.service;

import com.crm.system.modules.customer.entity.Customer;
import com.crm.system.modules.customer.mapper.CustomerMapper;
import com.crm.system.modules.customer.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 客户服务单元测试
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testCustomer = new Customer();
        testCustomer.setCustomerId(1L);
        testCustomer.setCustomerName("测试客户");
        testCustomer.setPhone("13800138000");
        testCustomer.setLevel(1);
        testCustomer.setStatus(1);
    }

    @Test
    void testSave_Success() {
        // Arrange
        when(customerMapper.insert(any(Customer.class))).thenReturn(1);

        // Act
        boolean result = customerService.save(testCustomer);

        // Assert
        assertTrue(result);
        verify(customerMapper, times(1)).insert(any(Customer.class));
    }

    @Test
    void testSave_Fail() {
        // Arrange
        when(customerMapper.insert(any(Customer.class))).thenReturn(0);

        // Act
        boolean result = customerService.save(testCustomer);

        // Assert
        assertFalse(result);
        verify(customerMapper, times(1)).insert(any(Customer.class));
    }

    @Test
    void testGetById_Exists() {
        // Arrange
        when(customerMapper.selectById(1L)).thenReturn(testCustomer);

        // Act
        Customer result = customerService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("测试客户", result.getCustomerName());
        verify(customerMapper, times(1)).selectById(1L);
    }

    @Test
    void testGetById_NotExists() {
        // Arrange
        when(customerMapper.selectById(999L)).thenReturn(null);

        // Act
        Customer result = customerService.getById(999L);

        // Assert
        assertNull(result);
        verify(customerMapper, times(1)).selectById(999L);
    }

    @Test
    void testUpdate_Success() {
        // Arrange
        when(customerMapper.updateById(any(Customer.class))).thenReturn(1);

        // Act
        boolean result = customerService.update(testCustomer);

        // Assert
        assertTrue(result);
        verify(customerMapper, times(1)).updateById(any(Customer.class));
    }

    @Test
    void testDelete_Success() {
        // Arrange
        when(customerMapper.deleteById(1L)).thenReturn(1);

        // Act
        boolean result = customerService.delete(1L);

        // Assert
        assertTrue(result);
        verify(customerMapper, times(1)).deleteById(1L);
    }

    @Test
    void testListAll() {
        // Arrange
        List<Customer> customers = Arrays.asList(testCustomer, testCustomer);
        when(customerMapper.selectList(any())).thenReturn(customers);

        // Act
        List<Customer> result = customerService.listAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(customerMapper, times(1)).selectList(any());
    }
}
