package com.crm.system.modules.customer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.modules.customer.entity.CustomerPool;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 公海池服务接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
public interface CustomerPoolService extends IService<CustomerPool> {

    /**
     * 分页查询公海池客户
     * 
     * @param current 当前页码
     * @param size 每页数量
     * @return 分页结果
     */
    Page<CustomerPool> pagePool(Integer current, Integer size);

    /**
     * 客户掉入公海池
     * 
     * @param customerId 客户 ID
     * @param reason 掉入原因
     * @param remark 备注
     */
    void dropToPool(Long customerId, Integer reason, String remark);

    /**
     * 领取公海池客户
     * 
     * @param poolId 公海池 ID
     * @param userId 领取人 ID
     * @return 是否成功
     */
    boolean claimCustomer(Long poolId, Long userId);

    /**
     * 分配公海池客户
     * 
     * @param poolId 公海池 ID
     * @param userId 分配给的用户 ID
     * @return 是否成功
     */
    boolean assignCustomer(Long poolId, Long userId);

    /**
     * 查询公海池统计
     * 
     * @return 统计数据
     */
    PoolStats getStats();

    /**
     * 公海池统计数据
     */
    class PoolStats {
        private Integer total;
        private Integer todayDrop;
        private Integer weekDrop;
        private Integer claimed;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getTodayDrop() {
            return todayDrop;
        }

        public void setTodayDrop(Integer todayDrop) {
            this.todayDrop = todayDrop;
        }

        public Integer getWeekDrop() {
            return weekDrop;
        }

        public void setWeekDrop(Integer weekDrop) {
            this.weekDrop = weekDrop;
        }

        public Integer getClaimed() {
            return claimed;
        }

        public void setClaimed(Integer claimed) {
            this.claimed = claimed;
        }
    }
}
