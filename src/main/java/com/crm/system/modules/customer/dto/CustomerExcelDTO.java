package com.crm.system.modules.customer.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户导入导出 DTO
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Data
public class CustomerExcelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "客户 ID", index = 0)
    private Long customerId;

    @ExcelProperty(value = "客户名称", index = 1)
    private String customerName;

    @ExcelProperty(value = "客户类型", index = 2)
    private String customerType;

    @ExcelProperty(value = "客户等级", index = 3)
    private String level;

    @ExcelProperty(value = "联系电话", index = 4)
    private String phone;

    @ExcelProperty(value = "邮箱", index = 5)
    private String email;

    @ExcelProperty(value = "公司名称", index = 6)
    private String companyName;

    @ExcelProperty(value = "公司规模", index = 7)
    private String companyScale;

    @ExcelProperty(value = "所属行业", index = 8)
    private String industry;

    @ExcelProperty(value = "地址", index = 9)
    private String address;

    @ExcelProperty(value = "微信", index = 10)
    private String wechat;

    @ExcelProperty(value = "QQ", index = 11)
    private String qq;

    @ExcelProperty(value = "负责人 ID", index = 12)
    private Long ownerId;

    @ExcelProperty(value = "状态", index = 13)
    private String status;

    @ExcelProperty(value = "下次跟进时间", index = 14)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextFollowTime;

    @ExcelProperty(value = "备注", index = 15)
    private String remark;

    @ExcelProperty(value = "创建时间", index = 16)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
