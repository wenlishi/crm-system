# 📥 Excel 导入导出使用指南

**版本**：v1.11.0  
**更新时间**：2026-03-14

---

## 📋 概述

Excel 导入导出模块已实现，支持客户数据的批量处理！

### 核心功能

- ✅ 导出客户数据为 Excel
- ✅ 导入 Excel 客户数据
- ✅ 下载导入模板
- ✅ 数据验证
- ✅ 错误提示
- ✅ 自动调整列宽

---

## 🚀 快速开始

### 1. 下载导入模板

```bash
curl http://localhost:8080/api/customers/download-template \
  -H "Authorization: $TOKEN" \
  -o 客户导入模板.xlsx
```

**模板包含字段**：
- 客户名称（必填）
- 客户类型（个人/企业）
- 客户等级（普通/VIP/重要）
- 联系电话
- 邮箱
- 公司名称
- 公司规模
- 所属行业
- 地址
- 微信
- QQ
- 备注

---

### 2. 导入客户数据

```bash
curl -X POST http://localhost:8080/api/customers/import \
  -H "Authorization: $TOKEN" \
  -F "file=@客户数据.xlsx"
```

**响应**：
```json
{
  "code": 200,
  "message": "导入完成，成功 98 条，失败 2 条",
  "data": {
    "total": 100,
    "success": 98,
    "fail": 2,
    "errorMessages": [
      "第 5 行：客户名称不能为空",
      "第 23 行：手机号格式不正确"
    ]
  }
}
```

---

### 3. 导出客户数据

```bash
# 导出全部客户
curl http://localhost:8080/api/customers/export \
  -H "Authorization: $TOKEN" \
  -o 客户数据_2026-03-14.xlsx

# 导出指定客户
curl "http://localhost:8080/api/customers/export?ids=1,2,3" \
  -H "Authorization: $TOKEN" \
  -o 选中客户.xlsx
```

---

## 📊 导入模板说明

### 必填字段

| 字段 | 说明 | 示例 |
|------|------|------|
| 客户名称 | 客户姓名或公司名 | 张三 / 测试公司 |

### 选填字段

| 字段 | 说明 | 示例 |
|------|------|------|
| 客户类型 | 个人/企业 | 个人、企业 |
| 客户等级 | 普通/VIP/重要 | 普通、VIP、重要 |
| 联系电话 | 11 位手机号 | 13800138000 |
| 邮箱 | 邮箱地址 | test@example.com |
| 公司名称 | 企业客户填写 | 测试科技有限公司 |
| 公司规模 | 公司人数规模 | 1-50 人、50-100 人 |
| 所属行业 | 行业分类 | 互联网、金融、教育 |
| 地址 | 详细地址 | 北京市朝阳区 xxx |
| 微信 | 微信号 | wechat123 |
| QQ | QQ 号码 | 123456789 |
| 备注 | 其他信息 | 意向客户 |

---

## 🛠️ API 接口

### 1. 下载导入模板

**接口**：`GET /api/customers/download-template`

**权限**：`customer:import`

**说明**：下载 Excel 导入模板，包含示例数据

---

### 2. 导入客户数据

**接口**：`POST /api/customers/import`

**权限**：`customer:import`

**参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | MultipartFile | 是 | Excel 文件 |

**响应**：
```json
{
  "code": 200,
  "message": "导入完成",
  "data": {
    "total": 100,      // 总行数
    "success": 98,     // 成功数量
    "fail": 2,         // 失败数量
    "errorMessages": [ // 错误信息列表
      "第 5 行：客户名称不能为空",
      "第 23 行：手机号格式不正确"
    ]
  }
}
```

---

### 3. 导出客户数据

**接口**：`GET /api/customers/export`

**权限**：`customer:export`

**参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | List<Long> | 否 | 客户 ID 列表（逗号分隔） |

**说明**：
- 不传 ids 参数：导出全部客户
- 传 ids 参数：导出指定客户

---

## ✅ 数据验证规则

### 1. 客户名称
- ✅ 不能为空
- ✅ 最大长度 100 字符

### 2. 联系电话
- ✅ 可选
- ✅ 必须是 11 位手机号
- ✅ 格式：1[3-9]\d{9}

### 3. 邮箱
- ✅ 可选
- ✅ 必须符合邮箱格式
- ✅ 正则：^[A-Za-z0-9+_.-]+@(.+)$

### 4. 客户类型
- ✅ 可选
- ✅ 值：个人、企业

### 5. 客户等级
- ✅ 可选
- ✅ 值：普通、VIP、重要

---

## 📝 使用示例

### 前端上传示例（Vue3）

```vue
<template>
  <div>
    <!-- 下载模板 -->
    <el-button @click="downloadTemplate">
      下载模板
    </el-button>

    <!-- 上传文件 -->
    <el-upload
      drag
      action="/api/customers/import"
      :headers="{ Authorization: token }"
      :on-success="handleSuccess"
      :on-error="handleError"
    >
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">
        拖拽文件到此处或<em>点击上传</em>
      </div>
    </el-upload>
  </div>
</template>

<script setup>
import { downloadTemplate } from '@/api/customer'

const downloadTemplate = () => {
  window.open('/api/customers/download-template')
}

const handleSuccess = (response) => {
  ElMessage.success(`导入完成，成功${response.data.success}条`)
}

const handleError = (error) => {
  ElMessage.error('导入失败')
}
</script>
```

---

### 批量导出示例

```javascript
// 导出选中的客户
function exportSelectedCustomers(selectedIds) {
  const ids = selectedIds.join(',')
  const url = `/api/customers/export?ids=${ids}`
  
  // 创建下载链接
  const link = document.createElement('a')
  link.href = url
  link.target = '_blank'
  link.click()
}

// 导出全部客户
function exportAllCustomers() {
  const url = '/api/customers/export'
  window.open(url)
}
```

---

## 🎯 最佳实践

### 1. 导入前检查

```java
// 检查文件格式
if (!file.getOriginalFilename().endsWith(".xlsx")) {
    throw new RuntimeException("只支持 Excel 2007+ 格式（.xlsx）");
}

// 检查文件大小
if (file.getSize() > 10 * 1024 * 1024) {
    throw new RuntimeException("文件大小不能超过 10MB");
}
```

### 2. 分批导入

```java
// 大量数据分批处理
List<CustomerExcelDTO> batch = new ArrayList<>();
int batchSize = 100;

for (CustomerExcelDTO dto : allData) {
    batch.add(dto);
    if (batch.size() >= batchSize) {
        saveBatch(batch);
        batch.clear();
    }
}
// 处理剩余数据
if (!batch.isEmpty()) {
    saveBatch(batch);
}
```

### 3. 错误日志记录

```java
// 记录详细错误信息
log.error("导入失败 - 第{}行：{}", rowNum, e.getMessage());
errorMessages.add("第" + rowNum + "行：" + e.getMessage());
```

### 4. 导出文件名规范

```java
// 使用日期命名
String fileName = "客户数据_" + 
    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
    ".xlsx";
```

---

## 🔧 故障排查

### 问题 1：导入失败 "不支持的文件格式"

**解决**：
- 确保文件是 .xlsx 格式（Excel 2007+）
- 不要使用 .xls 格式（Excel 97-2003）

### 问题 2：导入后中文乱码

**解决**：
- EasyExcel 默认使用 UTF-8 编码
- 确保 Excel 文件保存为 UTF-8 格式

### 问题 3：导出文件打不开

**解决**：
- 检查响应头 Content-Type 是否正确
- 检查文件是否完整下载

### 问题 4：大数据量导出超时

**解决**：
- 增加超时时间
- 使用异步导出
- 分批导出（每次 1000 条）

---

## 📚 相关文件

| 文件 | 说明 |
|------|------|
| `CustomerExcelDTO.java` | Excel 数据对象 |
| `CustomerImportExportService.java` | 导入导出服务接口 |
| `CustomerImportExportServiceImpl.java` | 服务实现 |
| `CustomerImportExportController.java` | 控制器 |

---

## 🎉 完成！

现在你的 CRM 系统支持完整的 Excel 导入导出功能！

**核心价值**：
1. ✅ 批量导入客户数据
2. ✅ 快速导出客户列表
3. ✅ 数据验证和错误提示
4. ✅ 提高数据录入效率

---

*文档版本：v1.11.0*  
*最后更新：2026-03-14*
