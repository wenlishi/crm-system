package com.crm.system.modules.customer.service;

import com.crm.system.modules.customer.dto.CustomerExcelDTO;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 客户导入导出服务接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
public interface CustomerImportExportService {

    /**
     * 导出客户数据为 Excel
     * 
     * @param response HTTP 响应
     * @param customerIds 客户 ID 列表（可选，为空导出全部）
     */
    void exportCustomers(HttpServletResponse response, List<Long> customerIds);

    /**
     * 导入客户数据
     * 
     * @param file Excel 文件
     * @param userId 导入人 ID
     * @return 导入结果
     */
    ImportResult importCustomers(MultipartFile file, Long userId);

    /**
     * 下载导入模板
     * 
     * @param response HTTP 响应
     */
    void downloadTemplate(HttpServletResponse response);

    /**
     * 导入结果
     */
    class ImportResult {
        private Integer total;
        private Integer success;
        private Integer fail;
        private List<String> errorMessages;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getSuccess() {
            return success;
        }

        public void setSuccess(Integer success) {
            this.success = success;
        }

        public Integer getFail() {
            return fail;
        }

        public void setFail(Integer fail) {
            this.fail = fail;
        }

        public List<String> getErrorMessages() {
            return errorMessages;
        }

        public void setErrorMessages(List<String> errorMessages) {
            this.errorMessages = errorMessages;
        }
    }
}
