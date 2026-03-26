<template>
  <div class="customer-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div>
          <h1 class="page-title">客户管理</h1>
          <p class="page-subtitle">管理和维护客户关系，提升销售转化</p>
        </div>
        <el-button type="primary" class="add-btn" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增客户
        </el-button>
      </div>
    </div>

    <!-- 筛选区 -->
    <div class="filter-section">
      <el-form :inline="true" :model="searchForm" class="filter-form">
        <el-form-item label="客户名称">
          <el-input
            v-model="searchForm.customerName"
            placeholder="搜索客户名称"
            clearable
            class="filter-input"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="客户级别">
          <el-select v-model="searchForm.level" placeholder="全部级别" clearable class="filter-select">
            <el-option label="A 类客户" value="A" />
            <el-option label="B 类客户" value="B" />
            <el-option label="C 类客户" value="C" />
            <el-option label="D 类客户" value="D" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="客户状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable class="filter-select">
            <el-option label="跟进中" :value="0" />
            <el-option label="已成交" :value="1" />
            <el-option label="已流失" :value="2" />
          </el-select>
        </el-form-item>
        
        <el-form-item class="filter-actions">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格区 -->
    <div class="table-section">
      <div class="table-toolbar">
        <div class="toolbar-left">
          <span class="table-count">共 <strong>{{ pagination.total }}</strong> 条记录</span>
        </div>
        <div class="toolbar-right">
          <el-button @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        class="data-table"
        :header-cell-style="{ background: '#fafbfc', color: '#666', fontWeight: '500' }"
      >
        <el-table-column prop="customerName" label="客户名称" min-width="180">
          <template #default="{ row }">
            <span class="name-text">{{ row.customerName }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="contact" label="联系人" width="120" />
        
        <el-table-column prop="phone" label="联系电话" width="140">
          <template #default="{ row }">
            <span class="phone-text">{{ row.phone }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="email" label="邮箱" min-width="180" />
        
        <el-table-column prop="level" label="级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)" size="small" effect="plain" class="level-tag">
              {{ row.level }}类
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small" effect="plain" class="status-tag">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="ownerName" label="负责人" width="120" />
        
        <el-table-column label="操作" width="220" fixed="right" class="action-column">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button link type="primary" @click="handleView(row)">详情</el-button>
              <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
              <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-footer" v-if="pagination.total > 0">
        <el-pagination
          :current-page="pagination.current"
          :page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @update:current-page="(val) => pagination.current = val"
          @update:page-size="(val) => pagination.size = val"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="640px"
      :close-on-click-modal="false"
      class="customer-dialog"
    >
      <el-form
        ref="customerFormRef"
        :model="customerForm"
        :rules="customerRules"
        class="customer-form"
        label-width="86px"
      >
        <el-form-item label="客户名称" prop="customerName">
          <el-input v-model="customerForm.customerName" placeholder="请输入客户名称" />
        </el-form-item>
        
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="customerForm.contact" placeholder="请输入联系人姓名" />
        </el-form-item>
        
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="customerForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="customerForm.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        
        <el-form-item label="客户级别" prop="level">
          <el-select v-model="customerForm.level" placeholder="请选择级别" style="width: 100%">
            <el-option label="A 类客户" value="A" />
            <el-option label="B 类客户" value="B" />
            <el-option label="C 类客户" value="C" />
            <el-option label="D 类客户" value="D" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="客户状态" prop="status">
          <el-select v-model="customerForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="跟进中" :value="0" />
            <el-option label="已成交" :value="1" />
            <el-option label="已流失" :value="2" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="customerForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, User } from '@element-plus/icons-vue'
import { getCustomerList, createCustomer, updateCustomer, deleteCustomer } from '@/api/customer'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增客户')
const customerFormRef = ref(null)

const searchForm = reactive({
  customerName: '',
  level: '',
  status: ''
})

const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

const customerForm = reactive({
  customerId: null,
  customerName: '',
  contact: '',
  phone: '',
  email: '',
  level: 'B',
  status: 0,
  remark: ''
})

const customerRules = {
  customerName: [
    { required: true, message: '请输入客户名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }
  ],
  level: [
    { required: true, message: '请选择客户级别', trigger: 'change' }
  ]
}

const getLevelType = (level) => {
  const types = { A: 'danger', B: 'primary', C: 'success', D: 'info' }
  return types[level] || 'info'
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'info' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '跟进中', 1: '已成交', 2: '已流失' }
  return texts[status] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    const res = await getCustomerList(params)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = Number(res.data.total) || 0
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.customerName = ''
  searchForm.level = ''
  searchForm.status = ''
  handleSearch()
}

const handleRefresh = () => {
  loadData()
  ElMessage.success('刷新成功')
}

const handleAdd = () => {
  dialogTitle.value = '新增客户'
  Object.assign(customerForm, {
    customerId: null,
    customerName: '',
    contact: '',
    phone: '',
    email: '',
    level: 'B',
    status: 0,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑客户'
  Object.assign(customerForm, row)
  dialogVisible.value = true
}

const handleView = (row) => {
  console.log('查看详情:', row)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除客户"${row.customerName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteCustomer(row.customerId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch {
    // 取消删除
  }
}

const handleSubmit = async () => {
  if (!customerFormRef.value) return
  
  await customerFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const api = customerForm.customerId ? updateCustomer : createCustomer
        const res = await api(customerForm)
        if (res.code === 200) {
          ElMessage.success(customerForm.customerId ? '更新成功' : '创建成功')
          dialogVisible.value = false
          loadData()
        }
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleSizeChange = () => {
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
/* ========== 页面布局 ========== */
.customer-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
}

/* ========== 页面头部 ========== */
.page-header {
  margin-bottom: 24px;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 6px 0;
  letter-spacing: -0.3px;
}

.page-subtitle {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.add-btn {
  height: 40px;
  padding: 0 20px;
  font-weight: 500;
  border-radius: 8px;
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a87 100%);
  border: none;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(30, 58, 95, 0.3);
}

/* ========== 筛选区 ========== */
.filter-section {
  margin-bottom: 24px;
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-input,
.filter-select {
  width: 240px;
}

:deep(.filter-input .el-input__wrapper),
:deep(.filter-select .el-input__wrapper) {
  border-radius: 8px;
  background: #fafbfc;
}

.filter-actions {
  margin-left: auto;
  display: flex;
  gap: 12px;
}

.filter-actions .el-button--primary {
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a87 100%);
  border: none;
  padding: 10px 20px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.filter-actions .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(30, 58, 95, 0.3);
}

.filter-actions .el-button--default {
  background: #f5f7fa;
  border-color: #e4e7ed;
  color: #606266;
  padding: 10px 20px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.filter-actions .el-button--default:hover {
  background: #fff;
  border-color: #1e3a5f;
  color: #1e3a5f;
}

/* ========== 表格区 ========== */
.table-section {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.table-count {
  font-size: 14px;
  color: #666;
}

.table-count strong {
  color: #1e3a5f;
  font-weight: 600;
}

.toolbar-right {
  display: flex;
  gap: 12px;
}

.data-table {
  --el-table-border-color: #f0f0f0;
  --el-table-header-bg-color: #fafbfc;
  --el-table-text-color: #333;
  --el-table-header-text-color: #666;
  --el-table-row-hover-bg-color: #fafbfc;
}

:deep(.data-table) {
  margin-bottom: 0;
}

:deep(.data-table .el-table__header th) {
  font-weight: 500;
  padding: 14px 0;
}

:deep(.data-table .el-table__row td) {
  padding: 14px 0;
  border-bottom-color: #f0f0f0;
}

.name-text {
  font-weight: 500;
  color: #1a1a1a;
}

.phone-text {
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', monospace;
  color: #666;
}

.level-tag,
.status-tag {
  border-radius: 6px;
  font-weight: 500;
  padding: 2px 10px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.action-buttons .el-button {
  padding: 4px 0;
  font-weight: 500;
}

.table-footer {
  display: block !important;
  padding: 20px 24px;
  border-top: 1px solid #f0f0f0;
  background: #fff;
}

.table-footer :deep(.el-pagination) {
  display: flex !important;
  justify-content: flex-end !important;
  align-items: center;
  gap: 8px;
}

/* ========== 对话框 ========== */
.customer-dialog .el-dialog__header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.customer-dialog .el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.customer-dialog .el-dialog__body {
  padding: 24px;
}

.customer-form {
  padding-top: 8px;
}

:deep(.customer-form .el-form-item__label) {
  font-weight: 500;
  color: #666;
}

:deep(.customer-form .el-input__wrapper),
:deep(.customer-form .el-select .el-input__wrapper),
:deep(.customer-form .el-textarea__inner) {
  border-radius: 8px;
  background: #fafbfc;
}

.customer-dialog .el-dialog__footer {
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .customer-page {
    padding: 16px;
  }
  
  .page-header,
  .filter-section,
  .table-section {
    border-radius: 8px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .add-btn {
    width: 100%;
  }
  
  .filter-form {
    flex-direction: column;
  }
  
  .filter-input,
  .filter-select {
    width: 100%;
  }
  
  .filter-actions {
    width: 100%;
  }
  
  .filter-actions .el-button {
    flex: 1;
  }
}
</style>
