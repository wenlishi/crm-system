package com.crm.system.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.system.modules.system.entity.Dept;

import java.util.List;

/**
 * 部门服务接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
public interface DeptService extends IService<Dept> {

    /**
     * 查询部门树形结构
     * 
     * @return 部门树
     */
    List<Dept> listTree();

    /**
     * 查询子部门列表
     * 
     * @param parentId 父部门 ID
     * @return 子部门列表
     */
    List<Dept> listChildren(Long parentId);

    /**
     * 获取部门统计
     * 
     * @return 部门统计数据
     */
    DeptStats getStats();

    /**
     * 部门统计数据
     */
    class DeptStats {
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
