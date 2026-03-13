package com.crm.system.modules.contract.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.modules.contract.entity.Contract;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 合同服务接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
public interface ContractService extends IService<Contract> {

    /**
     * 查询客户的合同列表
     * 
     * @param customerId 客户 ID
     * @return 合同列表
     */
    List<Contract> listByCustomer(Long customerId);

    /**
     * 分页查询合同
     * 
     * @param status 合同状态（可选）
     * @param current 当前页码
     * @param size 每页数量
     * @return 分页结果
     */
    Page<Contract> pageByStatus(Integer status, Integer current, Integer size);

    /**
     * 获取合同统计
     * 
     * @return 合同统计数据
     */
    ContractStats getStats();

    /**
     * 合同统计数据
     */
    class ContractStats {
        private Integer total;
        private Integer executing;
        private Integer completed;
        private BigDecimal totalAmount;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getExecuting() {
            return executing;
        }

        public void setExecuting(Integer executing) {
            this.executing = executing;
        }

        public Integer getCompleted() {
            return completed;
        }

        public void setCompleted(Integer completed) {
            this.completed = completed;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }
    }
}
