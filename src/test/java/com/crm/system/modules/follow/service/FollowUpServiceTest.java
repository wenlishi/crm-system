package com.crm.system.modules.follow.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.modules.follow.entity.FollowUp;
import com.crm.system.modules.follow.mapper.FollowUpMapper;
import com.crm.system.modules.follow.service.impl.FollowUpServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 跟进记录 Service 单元测试
 * 
 * @author wenlishi
 * @since 2026-03-16
 */
@ExtendWith(MockitoExtension.class)
class FollowUpServiceTest {

    @Mock
    private FollowUpMapper followUpMapper;

    @InjectMocks
    private FollowUpServiceImpl followUpService;

    private FollowUp testFollowUp;
    private Long testCustomerId;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testCustomerId = 1001L;
        testFollowUp = new FollowUp();
        testFollowUp.setFollowId(1L);
        testFollowUp.setCustomerId(testCustomerId);
        testFollowUp.setUserId(100L);
        testFollowUp.setFollowType(1); // 电话
        testFollowUp.setContent("与客户沟通产品需求");
        testFollowUp.setNextPlan("发送产品资料");
        testFollowUp.setNextFollowTime(LocalDateTime.now().plusDays(3));
    }

    @Test
    void testAddFollowUp_Success() {
        // Arrange
        when(followUpMapper.insert(any(FollowUp.class))).thenReturn(1);

        // Act
        boolean result = followUpService.addFollowUp(testFollowUp);

        // Assert
        assertTrue(result, "添加跟进记录应该成功");
        verify(followUpMapper, times(1)).insert(any(FollowUp.class));
    }

    @Test
    void testAddFollowUp_Fail() {
        // Arrange
        when(followUpMapper.insert(any(FollowUp.class))).thenReturn(0);

        // Act
        boolean result = followUpService.addFollowUp(testFollowUp);

        // Assert
        assertFalse(result, "添加跟进记录应该失败");
        verify(followUpMapper, times(1)).insert(any(FollowUp.class));
    }

    @Test
    void testAddFollowUp_VerifyData() {
        // Arrange
        ArgumentCaptor<FollowUp> captor = ArgumentCaptor.forClass(FollowUp.class);
        when(followUpMapper.insert(captor.capture())).thenReturn(1);

        // Act
        followUpService.addFollowUp(testFollowUp);

        // Assert
        FollowUp captured = captor.getValue();
        assertEquals(testCustomerId, captured.getCustomerId(), "客户 ID 应该一致");
        assertEquals("与客户沟通产品需求", captured.getContent(), "跟进内容应该一致");
        assertEquals(1, captured.getFollowType(), "跟进方式应该一致");
    }

    @Test
    void testListByCustomerId_WithRecords() {
        // Arrange
        List<FollowUp> followUps = Arrays.asList(
            createFollowUp(1L, "第一次跟进"),
            createFollowUp(2L, "第二次跟进"),
            createFollowUp(3L, "第三次跟进")
        );
        when(followUpMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(followUps);

        // Act
        List<FollowUp> result = followUpService.listByCustomerId(testCustomerId);

        // Assert
        assertNotNull(result, "结果不应该为空");
        assertEquals(3, result.size(), "应该返回 3 条跟进记录");
        assertEquals("第三次跟进", result.get(0).getContent(), "应该按时间倒序排列");
        verify(followUpMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void testListByCustomerId_NoRecords() {
        // Arrange
        when(followUpMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList());

        // Act
        List<FollowUp> result = followUpService.listByCustomerId(testCustomerId);

        // Assert
        assertNotNull(result, "结果不应该为 null，应该返回空列表");
        assertTrue(result.isEmpty(), "应该返回空列表");
        verify(followUpMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void testListByCustomerId_VerifyQueryCondition() {
        // Arrange
        ArgumentCaptor<LambdaQueryWrapper<FollowUp>> captor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        when(followUpMapper.selectList(captor.capture())).thenReturn(Arrays.asList());

        // Act
        followUpService.listByCustomerId(testCustomerId);

        // Assert
        // 验证查询条件正确（通过验证 wrapper 不为空来间接验证）
        assertNotNull(captor.getValue(), "应该使用查询条件");
    }

    @Test
    void testPageByCustomerId_WithRecords() {
        // Arrange
        Page<FollowUp> mockPage = new Page<>(1, 10);
        mockPage.setRecords(Arrays.asList(
            createFollowUp(1L, "跟进 1"),
            createFollowUp(2L, "跟进 2")
        ));
        mockPage.setTotal(2);
        
        when(followUpMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        // Act
        Page<FollowUp> result = followUpService.pageByCustomerId(testCustomerId, 1, 10);

        // Assert
        assertNotNull(result, "分页结果不应该为空");
        assertEquals(2, result.getRecords().size(), "应该返回 2 条记录");
        assertEquals(2, result.getTotal(), "总数应该是 2");
        verify(followUpMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void testPageByCustomerId_EmptyPage() {
        // Arrange
        Page<FollowUp> mockPage = new Page<>(5, 10);
        mockPage.setRecords(Arrays.asList());
        mockPage.setTotal(0);
        
        when(followUpMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        // Act
        Page<FollowUp> result = followUpService.pageByCustomerId(testCustomerId, 5, 10);

        // Assert
        assertNotNull(result, "分页结果不应该为 null");
        assertTrue(result.getRecords().isEmpty(), "第 5 页应该没有数据");
        assertEquals(0, result.getTotal(), "总数应该是 0");
    }

    @Test
    void testPageByCustomerId_VerifyPagination() {
        // Arrange
        ArgumentCaptor<Page<FollowUp>> pageCaptor = ArgumentCaptor.forClass(Page.class);
        Page<FollowUp> mockPage = new Page<>(1, 10);
        when(followUpMapper.selectPage(pageCaptor.capture(), any(LambdaQueryWrapper.class))).thenReturn(mockPage);

        // Act
        followUpService.pageByCustomerId(testCustomerId, 2, 20);

        // Assert
        Page<FollowUp> capturedPage = pageCaptor.getValue();
        assertEquals(2, capturedPage.getCurrent(), "当前页应该是 2");
        assertEquals(20, capturedPage.getSize(), "每页大小应该是 20");
    }

    // ===== 边界测试 =====

    @Test
    void testAddFollowUp_WithNullContent() {
        // Arrange
        testFollowUp.setContent(null);
        when(followUpMapper.insert(any(FollowUp.class))).thenReturn(1);

        // Act
        boolean result = followUpService.addFollowUp(testFollowUp);

        // Assert
        assertTrue(result, "即使内容为 null 也应该能保存（数据库约束处理）");
    }

    @Test
    void testAddFollowUp_WithLongContent() {
        // Arrange
        String longContent = "A".repeat(2000); // 2000 字符
        testFollowUp.setContent(longContent);
        when(followUpMapper.insert(any(FollowUp.class))).thenReturn(1);

        // Act
        boolean result = followUpService.addFollowUp(testFollowUp);

        // Assert
        assertTrue(result, "长内容也应该能保存");
    }

    @Test
    void testListByCustomerId_NullCustomerId() {
        // Arrange
        when(followUpMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList());

        // Act & Assert
        assertDoesNotThrow(() -> followUpService.listByCustomerId(null), 
            "客户 ID 为 null 时不应该抛出异常");
    }

    // ===== 辅助方法 =====

    private FollowUp createFollowUp(Long id, String content) {
        FollowUp followUp = new FollowUp();
        followUp.setFollowId(id);
        followUp.setCustomerId(testCustomerId);
        followUp.setUserId(100L);
        followUp.setFollowType(1);
        followUp.setContent(content);
        followUp.setNextPlan("下一步计划");
        return followUp;
    }
}
