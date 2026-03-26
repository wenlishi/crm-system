<template>
  <div class="contract-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div>
          <h1 class="page-title">合同管理</h1>
          <p class="page-subtitle">管理销售合同，跟踪执行进度</p>
        </div>
        <el-button type="primary" class="add-btn" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增合同
        </el-button>
      </div>
    </div>

    <!-- 筛选区 -->
    <div class="filter-section">
      <el-form :inline="true" :model="searchForm" class="filter-form">
        <el-form-item label="合同名称">
          <el-input
            v-model="searchForm.contractName"
            placeholder="搜索合同名称"
            clearable
            class="filter-input"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
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
        
        <el-form-item label="合同状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable class="filter-select">
            <el-option label="草稿" :value="0" />
            <el-option label="待审核" :value="1" />
            <el-option label="已审核" :value="2" />
            <el-option label="执行中" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已终止" :value="5" />
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
        <el-table-column prop="contractName" label="合同名称" min-width="200">
          <template #default="{ row }">
            <span class="name-text">{{ row.contractName }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="customerName" label="客户名称" min-width="140" />
        
        <el-table-column prop="amount" label="合同金额" width="150">
          <template #default="{ row }">
            <span class="amount-text">¥{{ formatAmount(row.amount) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small" effect="plain" class="status-tag">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="signDate" label="签订日期" width="130">
          <template #default="{ row }">
            <span class="date-text">{{ row.signDate || '未设置' }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="ownerName" label="负责人" width="120" />
        
        <el-table-column label="操作" width="200" fixed="right" class="action-column">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
              <el-button link type="success" @click="handleUpdateStatus(row)">状态</el-button>
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
      class="contract-dialog"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="contract-form"
        label-width="100px"
      >
        <el-form-item label="合同名称" prop="contractName">
          <el-input v-model="form.contractName" placeholder="请输入合同名称" />
        </el-form-item>
        
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="form.customerId" placeholder="请选择客户" style="width: 100%">
            <el-option
              v-for="item in customers"
              :key="item.customerId"
              :label="item.customerName"
              :value="item.customerId"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="合同金额" prop="amount">
          <el-input-number
            v-model="form.amount"
            :min="0"
            :precision="2"
            :step="1000"
            placeholder="请输入金额"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="签订日期" prop="signDate">
          <el-date-picker
            v-model="form.signDate"
            type="date"
            placeholder="选择签订日期"
            style="width: 100%"
            format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item label="生效日期" prop="effectiveDate">
          <el-date-picker
            v-model="form.effectiveDate"
            type="date"
            placeholder="选择生效日期"
            style="width: 100%"
            format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item label="到期日期" prop="expiryDate">
          <el-date-picker
            v-model="form.expiryDate"
            type="date"
            placeholder="选择到期日期"
            style="width: 100%"
            format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
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

    <!-- 状态更新对话框 -->
    <el-dialog
      v-model="statusDialogVisible"
      title="更新合同状态"
      width="400px"
      :close-on-click-modal="false"
      class="status-dialog"
    >
      <el-form :model="statusForm" label-width="80px">
        <el-form-item label="合同状态">
          <el-select v-model="statusForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="草稿" :value="0" />
            <el-option label="待审核" :value="1" />
            <el-option label="已审核" :value="2" />
            <el-option label="执行中" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已终止" :value="5" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleStatusSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getContractList, createContract, updateContract, deleteContract, updateContractStatus } from '@/api/contract'
import { getAllCustomers } from '@/api/customer'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增合同')
const statusDialogVisible = ref(false)
const formRef = ref(null)
const customers = ref([])
const currentContract = ref(null)

const searchForm = reactive({
  contractName: '',
  customerName: '',
  status: ''
})

const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

const form = reactive({
  contractId: null,
  contractName: '',
  customerId: null,
  amount: 0,
  signDate: '',
  effectiveDate: '',
  expiryDate: '',
  remark: ''
})

const statusForm = reactive({
  status: ''
})

const rules = {
  contractName: [
    { required: true, message: '请输入合同名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  customerId: [
    { required: true, message: '请选择客户', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入合同金额', trigger: 'blur' }
  ],
  signDate: [
    { required: true, message: '请选择签订日期', trigger: 'change' }
  ]
}

const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'warning', 2: 'primary', 3: 'success', 4: 'success', 5: 'info' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '草稿', 1: '待审核', 2: '已审核', 3: '执行中', 4: '已完成', 5: '已终止' }
  return texts[status] || '未知'
}

const formatAmount = (value) => {
  if (!value) return '0.00'
  return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    const res = await getContractList(params)
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

const loadCustomers = async () => {
  try {
    const res = await getAllCustomers()
    if (res.code === 200) {
      customers.value = res.data || []
    }
  } catch (error) {
    console.error('加载客户失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.contractName = ''
  searchForm.customerName = ''
  searchForm.status = ''
  handleSearch()
}

const handleRefresh = () => {
  loadData()
  ElMessage.success('刷新成功')
}

const handleAdd = () => {
  dialogTitle.value = '新增合同'
  Object.assign(form, {
    contractId: null,
    contractName: '',
    customerId: null,
    amount: 0,
    signDate: '',
    effectiveDate: '',
    expiryDate: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑合同'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleUpdateStatus = (row) => {
  currentContract.value = row
  statusForm.status = row.status
  statusDialogVisible.value = true
}

const handleStatusSubmit = async () => {
  try {
    const res = await updateContractStatus({
      contractId: currentContract.value.contractId,
      status: statusForm.status
    })
    if (res.code === 200) {
      ElMessage.success('状态更新成功')
      statusDialogVisible.value = false
      loadData()
    }
  } catch (error) {
    console.error('更新状态失败:', error)
    ElMessage.error('更新状态失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除合同"${row.contractName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteContract(row.contractId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch {
    // 取消删除
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const api = form.contractId ? updateContract : createContract
        const res = await api(form)
        if (res.code === 200) {
          ElMessage.success(form.contractId ? '更新成功' : '创建成功')
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
  loadCustomers()
})
</script>

<style scoped>
/* ========== 页面布局 ========== */
.contract-page {
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

.amount-text {
  font-weight: 600;
  color: #1e3a5f;
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', monospace;
}

.status-tag {
  border-radius: 6px;
  font-weight: 500;
  padding: 2px 10px;
}

.date-text {
  color: #666;
  font-size: 13px;
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
.contract-dialog .el-dialog__header,
.status-dialog .el-dialog__header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.contract-dialog .el-dialog__title,
.status-dialog .el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.contract-dialog .el-dialog__body,
.status-dialog .el-dialog__body {
  padding: 24px;
}

.contract-form,
.status-form {
  padding-top: 8px;
}

:deep(.contract-form .el-form-item__label),
:deep(.status-form .el-form-item__label) {
  font-weight: 500;
  color: #666;
}

:deep(.contract-form .el-input__wrapper),
:deep(.contract-form .el-select .el-input__wrapper),
:deep(.contract-form .el-textarea__inner),
:deep(.contract-form .el-input-number .el-input__wrapper) {
  border-radius: 8px;
  background: #fafbfc;
}

.contract-dialog .el-dialog__footer,
.status-dialog .el-dialog__footer {
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
  .contract-page {
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
