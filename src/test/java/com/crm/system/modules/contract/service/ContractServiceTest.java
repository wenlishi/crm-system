package com.crm.system.modules.contract.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.modules.contract.entity.Contract;
import com.crm.system.modules.contract.mapper.ContractMapper;
import com.crm.system.modules.contract.service.impl.ContractServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 合同 Service 单元测试
 * 
 * @author wenlishi
 * @since 2026-03-16
 */
@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock
    private ContractMapper contractMapper;

    @InjectMocks
    private ContractServiceImpl contractService;

    private Contract testContract;
    private Long testCustomerId;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testCustomerId = 1001L;
        testContract = new Contract();
        testContract.setContractId(1L);
        testContract.setContractNo("HT-2026-001");
        testContract.setContractName("企业 CRM 系统开发合同");
        testContract.setCustomerId(testCustomerId);
        testContract.setOpportunityId(100L);
        testContract.setAmount(new BigDecimal("50000.00"));
        testContract.setSignDate(LocalDate.now());
        testContract.setStartDate(LocalDate.now());
        testContract.setEndDate(LocalDate.now().plusMonths(6));
        testContract.setStatus(4); // 执行中
        testContract.setOwnerId(100L);
    }

    @Test
    void testSave_Success() {
        // Arrange
        when(contractMapper.insert(any(Contract.class))).thenReturn(1);

        // Act
        boolean result = contractService.save(testContract);

        // Assert
        assertTrue(result, "保存合同应该成功");
        verify(contractMapper, times(1)).insert(any(Contract.class));
    }

    @Test
    void testSave_Fail() {
        // Arrange
        when(contractMapper.insert(any(Contract.class))).thenReturn(0);

        // Act
        boolean result = contractService.save(testContract);

        // Assert
        assertFalse(result, "保存合同应该失败");
        verify(contractMapper, times(1)).insert(any(Contract.class));
    }

    @Test
    void testUpdate_Success() {
        // Arrange
        when(contractMapper.updateById(any(Contract.class))).thenReturn(1);

        // Act
        boolean result = contractService.updateById(testContract);

        // Assert
        assertTrue(result, "更新合同应该成功");
        verify(contractMapper, times(1)).updateById(any(Contract.class));
    }

    @Test
    void testListByCustomer_WithRecords() {
        // Arrange
        List<Contract> contracts = Arrays.asList(
            createContract(1L, "合同 A", 4),
            createContract(2L, "合同 B", 5),
            createContract(3L, "合同 C", 4)
        );
        when(contractMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(contracts);

        // Act
        List<Contract> result = contractService.listByCustomer(testCustomerId);

        // Assert
        assertNotNull(result, "结果不应该为空");
        assertEquals(3, result.size(), "应该返回 3 个合同");
        assertEquals("合同 C", result.get(0).getContractName(), "应该按时间倒序排列");
        verify(contractMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void testListByCustomer_NoRecords() {
        // Arrange
        when(contractMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList());

        // Act
        List<Contract> result = contractService.listByCustomer(testCustomerId);

        // Assert
        assertNotNull(result, "结果不应该为 null");
        assertTrue(result.isEmpty(), "应该返回空列表");
        verify(contractMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void testPageByStatus_WithStatusFilter() {
        // Arrange
        Page<Contract> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(
            createContract(1L, "执行中合同 1", 4),
            createContract(2L, "执行中合同 2", 4)
        ));
        mockPage.setTotal(2);
        
        when(contractMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        // Act
        Page<Contract> result = contractService.pageByStatus(4, 1, 10);

        // Assert
        assertNotNull(result, "分页结果不应该为空");
        assertEquals(2, result.getRecords().size(), "应该返回 2 条记录");
        assertEquals(2, result.getTotal(), "总数应该是 2");
    }

    @Test
    void testPageByStatus_WithoutStatusFilter() {
        // Arrange
        Page<Contract> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(
            createContract(1L, "合同 1", 4),
            createContract(2L, "合同 2", 5),
            createContract(3L, "合同 3", 6)
        ));
        mockPage.setTotal(3);
        
        when(contractMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        // Act
        Page<Contract> result = contractService.pageByStatus(null, 1, 10);

        // Assert
        assertNotNull(result, "分页结果不应该为空");
        assertEquals(3, result.getRecords().size(), "应该返回 3 条记录（所有状态）");
    }

    @Test
    void testGetStats() {
        // Arrange
        List<Contract> allContracts = Arrays.asList(
            createContract(1L, "合同 1", 4), // 执行中
            createContract(2L, "合同 2", 5), // 已完成
            createContract(3L, "合同 3", 4), // 执行中
            createContract(4L, "合同 4", 6)  // 已终止
        );
        
        when(contractMapper.selectCount(any())).thenReturn(4L); // 总数
        when(contractMapper.selectCount(any(LambdaQueryWrapper.class)))
            .thenReturn(2L)  // 执行中
            .thenReturn(1L); // 已完成
        when(contractMapper.selectList(any())).thenReturn(allContracts);

        // Act
        ContractService.ContractStats stats = contractService.getStats();

        // Assert
        assertNotNull(stats, "统计结果不应该为空");
        assertEquals(4, stats.getTotal(), "合同总数应该正确");
        assertEquals(2, stats.getExecuting(), "执行中合同数应该正确");
        assertEquals(1, stats.getCompleted(), "已完成合同数应该正确");
        assertEquals(new BigDecimal("200000.00"), stats.getTotalAmount(), "合同总金额应该正确");
    }

    @Test
    void testGetStats_EmptyData() {
        // Arrange
        when(contractMapper.selectCount(any())).thenReturn(0L);
        when(contractMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(contractMapper.selectList(any())).thenReturn(Arrays.asList());

        // Act
        ContractService.ContractStats stats = contractService.getStats();

        // Assert
        assertNotNull(stats, "即使没有数据也应该返回统计结果");
        assertEquals(0, stats.getTotal(), "总数应该为 0");
        assertEquals(0, stats.getExecuting(), "执行中应该为 0");
        assertEquals(0, stats.getCompleted(), "已完成应该为 0");
        assertEquals(BigDecimal.ZERO, stats.getTotalAmount(), "总金额应该为 0");
    }

    @Test
    void testGetStats_WithNullAmount() {
        // Arrange
        Contract contractWithNullAmount = createContract(1L, "合同 1", 4);
        contractWithNullAmount.setAmount(null);
        
        when(contractMapper.selectCount(any())).thenReturn(1L);
        when(contractMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(contractMapper.selectList(any())).thenReturn(Arrays.asList(contractWithNullAmount));

        // Act
        ContractService.ContractStats stats = contractService.getStats();

        // Assert
        assertNotNull(stats, "统计结果不应该为空");
        assertEquals(BigDecimal.ZERO, stats.getTotalAmount(), "null 金额应该被跳过");
    }

    // ===== 边界测试 =====

    @Test
    void testSave_WithNullAmount() {
        // Arrange
        testContract.setAmount(null);
        when(contractMapper.insert(any(Contract.class))).thenReturn(1);

        // Act
        boolean result = contractService.save(testContract);

        // Assert
        assertTrue(result, "金额为 null 也应该能保存");
    }

    @Test
    void testSave_WithZeroAmount() {
        // Arrange
        testContract.setAmount(BigDecimal.ZERO);
        when(contractMapper.insert(any(Contract.class))).thenReturn(1);

        // Act
        boolean result = contractService.save(testContract);

        // Assert
        assertTrue(result, "金额为 0 也应该能保存");
    }

    @Test
    void testSave_WithLargeAmount() {
        // Arrange
        testContract.setAmount(new BigDecimal("999999999.99"));
        when(contractMapper.insert(any(Contract.class))).thenReturn(1);

        // Act
        boolean result = contractService.save(testContract);

        // Assert
        assertTrue(result, "大金额也应该能保存");
    }

    @Test
    void testUpdate_NotExists() {
        // Arrange
        when(contractMapper.updateById(any(Contract.class))).thenReturn(0);

        // Act
        boolean result = contractService.updateById(testContract);

        // Assert
        assertFalse(result, "更新不存在的合同应该失败");
    }

    @Test
    void testDelete_Success() {
        // Arrange
        when(contractMapper.deleteById(1L)).thenReturn(1);

        // Act
        boolean result = contractService.removeById(1L);

        // Assert
        assertTrue(result, "删除合同应该成功");
        verify(contractMapper, times(1)).deleteById(1L);
    }

    @Test
    void testGetById_Exists() {
        // Arrange
        when(contractMapper.selectById(1L)).thenReturn(testContract);

        // Act
        Contract result = contractService.getById(1L);

        // Assert
        assertNotNull(result, "合同不应该为 null");
        assertEquals("HT-2026-001", result.getContractNo(), "合同编号应该正确");
        verify(contractMapper, times(1)).selectById(1L);
    }

    @Test
    void testGetById_NotExists() {
        // Arrange
        when(contractMapper.selectById(999L)).thenReturn(null);

        // Act
        Contract result = contractService.getById(999L);

        // Assert
        assertNull(result, "不存在的合同应该返回 null");
    }

    // ===== 辅助方法 =====

    private Contract createContract(Long id, String name, Integer status) {
        Contract contract = new Contract();
        contract.setContractId(id);
        contract.setContractNo("HT-" + String.format("%03d", id));
        contract.setContractName(name);
        contract.setCustomerId(testCustomerId);
        contract.setAmount(new BigDecimal("50000.00"));
        contract.setStatus(status);
        contract.setOwnerId(100L);
        return contract;
    }
}
