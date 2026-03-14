package com.crm.system.modules.customer.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.crm.system.modules.customer.dto.CustomerExcelDTO;
import com.crm.system.modules.customer.entity.Customer;
import com.crm.system.modules.customer.service.CustomerImportExportService;
import com.crm.system.modules.customer.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户导入导出服务实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Slf4j
@Service
public class CustomerImportExportServiceImpl implements CustomerImportExportService {

    @Autowired
    private CustomerService customerService;

    @Override
    public void exportCustomers(HttpServletResponse response, List<Long> customerIds) {
        try {
            // 查询客户数据
            List<Customer> customers;
            if (customerIds == null || customerIds.isEmpty()) {
                customers = customerService.listAll();
            } else {
                customers = customerService.listByIds(customerIds);
            }

            // 转换为 Excel DTO
            List<CustomerExcelDTO> dtoList = customers.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("客户数据_" + LocalDateTime.now().toLocalDate(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 导出 Excel
            EasyExcel.write(response.getOutputStream(), CustomerExcelDTO.class)
                    .sheet("客户列表")
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .doWrite(dtoList);

            log.info("导出客户数据成功，共 {} 条", customers.size());

        } catch (IOException e) {
            log.error("导出客户数据失败", e);
            throw new RuntimeException("导出失败：" + e.getMessage());
        }
    }

    @Override
    public ImportResult importCustomers(MultipartFile file, Long userId) {
        ImportResult result = new ImportResult();
        List<String> errorMessages = new ArrayList<>();
        List<CustomerExcelDTO> successList = new ArrayList<>();

        try {
            // 读取 Excel
            EasyExcel.read(file.getInputStream(), CustomerExcelDTO.class, new AnalysisEventListener<CustomerExcelDTO>() {
                
                @Override
                public void invoke(CustomerExcelDTO dto, AnalysisContext context) {
                    try {
                        // 验证数据
                        validateDTO(dto);
                        
                        // 转换为实体并保存
                        Customer customer = convertToEntity(dto);
                        customer.setOwnerId(userId);
                        customerService.save(customer);
                        
                        successList.add(dto);
                    } catch (Exception e) {
                        int rowNum = context.readRowHolder().getRowIndex() + 1;
                        errorMessages.add("第" + rowNum + "行：" + e.getMessage());
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("所有数据解析完成");
                }
            }).sheet().doRead();

            // 设置结果
            result.setTotal(successList.size() + errorMessages.size());
            result.setSuccess(successList.size());
            result.setFail(errorMessages.size());
            result.setErrorMessages(errorMessages);

            log.info("导入客户数据成功，成功 {} 条，失败 {} 条", successList.size(), errorMessages.size());

        } catch (IOException e) {
            log.error("导入客户数据失败", e);
            throw new RuntimeException("导入失败：" + e.getMessage());
        }

        return result;
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("客户导入模板", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 创建空模板
            List<CustomerExcelDTO> templateList = new ArrayList<>();
            
            // 添加示例数据
            CustomerExcelDTO example = new CustomerExcelDTO();
            example.setCustomerName("示例客户");
            example.setCustomerType("企业");
            example.setLevel("普通");
            example.setPhone("13800138000");
            example.setEmail("example@test.com");
            example.setCompanyName("示例公司");
            templateList.add(example);

            // 写入 Excel
            EasyExcel.write(response.getOutputStream(), CustomerExcelDTO.class)
                    .sheet("模板")
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .doWrite(templateList);

            log.info("下载导入模板成功");

        } catch (IOException e) {
            log.error("下载模板失败", e);
            throw new RuntimeException("下载模板失败：" + e.getMessage());
        }
    }

    /**
     * 转换为 DTO
     */
    private CustomerExcelDTO convertToDTO(Customer customer) {
        CustomerExcelDTO dto = new CustomerExcelDTO();
        BeanUtils.copyProperties(customer, dto);
        
        // 转换枚举值
        dto.setCustomerType(convertCustomerType(customer.getCustomerType()));
        dto.setLevel(convertLevel(customer.getLevel()));
        dto.setStatus(convertStatus(customer.getStatus()));
        
        return dto;
    }

    /**
     * 转换为实体
     */
    private Customer convertToEntity(CustomerExcelDTO dto) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(dto, customer);
        
        // 转换枚举值
        customer.setCustomerType(convertCustomerTypeReverse(dto.getCustomerType()));
        customer.setLevel(convertLevelReverse(dto.getLevel()));
        
        return customer;
    }

    /**
     * 验证 DTO 数据
     */
    private void validateDTO(CustomerExcelDTO dto) {
        if (dto.getCustomerName() == null || dto.getCustomerName().trim().isEmpty()) {
            throw new RuntimeException("客户名称不能为空");
        }
        
        if (dto.getPhone() != null && !dto.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new RuntimeException("手机号格式不正确");
        }
        
        if (dto.getEmail() != null && !dto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("邮箱格式不正确");
        }
    }

    /**
     * 转换客户类型
     */
    private String convertCustomerType(Integer type) {
        if (type == null) return "个人";
        return type == 1 ? "个人" : "企业";
    }

    /**
     * 转换客户类型（反向）
     */
    private Integer convertCustomerTypeReverse(String type) {
        if (type == null) return 1;
        return "企业".equals(type) ? 2 : 1;
    }

    /**
     * 转换客户等级
     */
    private String convertLevel(Integer level) {
        if (level == null) return "普通";
        switch (level) {
            case 1: return "普通";
            case 2: return "VIP";
            case 3: return "重要";
            default: return "普通";
        }
    }

    /**
     * 转换客户等级（反向）
     */
    private Integer convertLevelReverse(String level) {
        if (level == null) return 1;
        switch (level) {
            case "VIP": return 2;
            case "重要": return 3;
            default: return 1;
        }
    }

    /**
     * 转换状态
     */
    private String convertStatus(Integer status) {
        if (status == null) return "有效";
        switch (status) {
            case 0: return "失效";
            case 1: return "有效";
            case 2: return "已成交";
            default: return "有效";
        }
    }
}
