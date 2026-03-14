package com.crm.system.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.modules.system.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 角色服务接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
public interface RoleService extends IService<Role> {

    /**
     * 分页查询角色
     * 
     * @param current 当前页码
     * @param size 每页数量
     * @return 分页结果
     */
    Page<Role> pageRoles(Integer current, Integer size);

    /**
     * 查询所有可用角色
     * 
     * @return 角色列表
     */
    List<Role> listAvailableRoles();

    /**
     * 根据用户 ID 查询角色列表
     * 
     * @param userId 用户 ID
     * @return 角色列表
     */
    List<Role> listByUserId(Long userId);

    /**
     * 获取角色统计
     * 
     * @return 角色统计数据
     */
    RoleStats getStats();

    /**
     * 角色统计数据
     */
    class RoleStats {
        private Integer total;
        private Integer active;
        private Integer disabled;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getActive() {
            return active;
        }

        public void setActive(Integer active) {
            this.active = active;
        }

        public Integer getDisabled() {
            return disabled;
        }

        public void setDisabled(Integer disabled) {
            this.disabled = disabled;
        }
    }
}
