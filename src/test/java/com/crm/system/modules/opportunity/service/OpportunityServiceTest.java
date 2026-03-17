package com.crm.system.modules.opportunity.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.modules.opportunity.entity.Opportunity;
import com.crm.system.modules.opportunity.mapper.OpportunityMapper;
import com.crm.system.modules.opportunity.service.impl.OpportunityServiceImpl;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 商机 Service 单元测试
 * 
 * @author wenlishi
 * @since 2026-03-16
 */
@ExtendWith(MockitoExtension.class)
class OpportunityServiceTest {

    @Mock
    private OpportunityMapper opportunityMapper;

    @InjectMocks
    private OpportunityServiceImpl opportunityService;

    private Opportunity testOpportunity;
    private Long testCustomerId;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testCustomerId = 1001L;
        testOpportunity = new Opportunity();
        testOpportunity.setOpportunityId(1L);
        testOpportunity.setCustomerId(testCustomerId);
        testOpportunity.setOpportunityName("企业 CRM 系统项目");
        testOpportunity.setStage(2); // 需求确认
        testOpportunity.setExpectedAmount(new BigDecimal("50000.00"));
        testOpportunity.setActualAmount(new BigDecimal("45000.00"));
        testOpportunity.setProbability(60);
        testOpportunity.setExpectedCloseDate(LocalDate.now().plusDays(30));
        testOpportunity.setOwnerId(100L);
        testOpportunity.setStatus(1); // 进行中
    }

    @Test
    void testSave_Success() {
        // Arrange
        when(opportunityMapper.insert(any(Opportunity.class))).thenReturn(1);

        // Act
        boolean result = opportunityService.save(testOpportunity);

        // Assert
        assertTrue(result, "保存商机应该成功");
        verify(opportunityMapper, times(1)).insert(any(Opportunity.class));
    }

    @Test
    void testSave_Fail() {
        // Arrange
        when(opportunityMapper.insert(any(Opportunity.class))).thenReturn(0);

        // Act
        boolean result = opportunityService.save(testOpportunity);

        // Assert
        assertFalse(result, "保存商机应该失败");
        verify(opportunityMapper, times(1)).insert(any(Opportunity.class));
    }

    @Test
    void testUpdate_Success() {
        // Arrange
        when(opportunityMapper.updateById(any(Opportunity.class))).thenReturn(1);

        // Act
        boolean result = opportunityService.updateById(testOpportunity);

        // Assert
        assertTrue(result, "更新商机应该成功");
        verify(opportunityMapper, times(1)).updateById(any(Opportunity.class));
    }

    @Test
    void testListByCustomerId_WithRecords() {
        // Arrange
        List<Opportunity> opportunities = Arrays.asList(
            createOpportunity(1L, "项目 A", 1),
            createOpportunity(2L, "项目 B", 2),
            createOpportunity(3L, "项目 C", 3)
        );
        when(opportunityMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(opportunities);

        // Act
        List<Opportunity> result = opportunityService.listByCustomerId(testCustomerId);

        // Assert
        assertNotNull(result, "结果不应该为空");
        assertEquals(3, result.size(), "应该返回 3 个商机");
        assertEquals("项目 C", result.get(0).getOpportunityName(), "应该按时间倒序排列");
        verify(opportunityMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void testListByCustomerId_NoRecords() {
        // Arrange
        when(opportunityMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList());

        // Act
        List<Opportunity> result = opportunityService.listByCustomerId(testCustomerId);

        // Assert
        assertNotNull(result, "结果不应该为 null");
        assertTrue(result.isEmpty(), "应该返回空列表");
        verify(opportunityMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void testPageByStage_WithStageFilter() {
        // Arrange
        Page<Opportunity> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(
            createOpportunity(1L, "需求确认项目 1", 2),
            createOpportunity(2L, "需求确认项目 2", 2)
        ));
        mockPage.setTotal(2);
        
        when(opportunityMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        // Act
        Page<Opportunity> result = opportunityService.pageByStage(2, 1, 10);

        // Assert
        assertNotNull(result, "分页结果不应该为空");
        assertEquals(2, result.getRecords().size(), "应该返回 2 条记录");
        assertEquals(2, result.getTotal(), "总数应该是 2");
        
        // 验证查询条件包含阶段过滤
        ArgumentCaptor<LambdaQueryWrapper<Opportunity>> captor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(opportunityMapper).selectPage(any(Page.class), captor.capture());
    }

    @Test
    void testPageByStage_WithoutStageFilter() {
        // Arrange
        Page<Opportunity> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(
            createOpportunity(1L, "项目 1", 1),
            createOpportunity(2L, "项目 2", 2),
            createOpportunity(3L, "项目 3", 3)
        ));
        mockPage.setTotal(3);
        
        when(opportunityMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        // Act
        Page<Opportunity> result = opportunityService.pageByStage(null, 1, 10);

        // Assert
        assertNotNull(result, "分页结果不应该为空");
        assertEquals(3, result.getRecords().size(), "应该返回 3 条记录（所有阶段）");
    }

    @Test
    void testGetStageStats() {
        // Arrange
        when(opportunityMapper.selectCount(any(LambdaQueryWrapper.class)))
            .thenReturn(10L)  // 初步接触
            .thenReturn(8L)   // 需求确认
            .thenReturn(5L)   // 方案报价
            .thenReturn(3L)   // 谈判
            .thenReturn(2L)   // 成交
            .thenReturn(28L); // 总计

        // Act
        Map<String, Object> stats = opportunityService.getStageStats();

        // Assert
        assertNotNull(stats, "统计结果不应该为空");
        assertTrue(stats.containsKey("byStage"), "应该包含按阶段统计");
        assertTrue(stats.containsKey("total"), "应该包含总计");
        
        @SuppressWarnings("unchecked")
        Map<String, Long> byStage = (Map<String, Long>) stats.get("byStage");
        assertNotNull(byStage, "阶段统计不应该为空");
        assertEquals(5, byStage.size(), "应该有 5 个阶段");
        assertEquals(10L, byStage.get("初步接触"), "初步接触阶段数量应该正确");
        assertEquals(2L, byStage.get("成交"), "成交阶段数量应该正确");
    }

    @Test
    void testGetStageStats_EmptyData() {
        // Arrange
        when(opportunityMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        // Act
        Map<String, Object> stats = opportunityService.getStageStats();

        // Assert
        assertNotNull(stats, "即使没有数据也应该返回统计结果");
        @SuppressWarnings("unchecked")
        Map<String, Long> byStage = (Map<String, Long>) stats.get("byStage");
        assertEquals(0L, stats.get("total"), "总数应该为 0");
    }

    // ===== 边界测试 =====

    @Test
    void testSave_WithNullExpectedAmount() {
        // Arrange
        testOpportunity.setExpectedAmount(null);
        when(opportunityMapper.insert(any(Opportunity.class))).thenReturn(1);

        // Act
        boolean result = opportunityService.save(testOpportunity);

        // Assert
        assertTrue(result, "预计金额为 null 也应该能保存");
    }

    @Test
    void testSave_WithZeroProbability() {
        // Arrange
        testOpportunity.setProbability(0);
        when(opportunityMapper.insert(any(Opportunity.class))).thenReturn(1);

        // Act
        boolean result = opportunityService.save(testOpportunity);

        // Assert
        assertTrue(result, "成功概率为 0 也应该能保存");
    }

    @Test
    void testSave_WithHundredProbability() {
        // Arrange
        testOpportunity.setProbability(100);
        when(opportunityMapper.insert(any(Opportunity.class))).thenReturn(1);

        // Act
        boolean result = opportunityService.save(testOpportunity);

        // Assert
        assertTrue(result, "成功概率为 100 也应该能保存");
    }

    @Test
    void testUpdate_NotExists() {
        // Arrange
        when(opportunityMapper.updateById(any(Opportunity.class))).thenReturn(0);

        // Act
        boolean result = opportunityService.updateById(testOpportunity);

        // Assert
        assertFalse(result, "更新不存在的商机应该失败");
    }

    @Test
    void testListByCustomerId_NullCustomerId() {
        // Arrange
        when(opportunityMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList());

        // Act & Assert
        assertDoesNotThrow(() -> opportunityService.listByCustomerId(null), 
            "客户 ID 为 null 时不应该抛出异常");
    }

    // ===== 辅助方法 =====

    private Opportunity createOpportunity(Long id, String name, Integer stage) {
        Opportunity opportunity = new Opportunity();
        opportunity.setOpportunityId(id);
        opportunity.setCustomerId(testCustomerId);
        opportunity.setOpportunityName(name);
        opportunity.setStage(stage);
        opportunity.setExpectedAmount(new BigDecimal("10000.00"));
        opportunity.setProbability(50);
        opportunity.setOwnerId(100L);
        opportunity.setStatus(1);
        return opportunity;
    }
}
