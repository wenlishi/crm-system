<template>
  <div class="followup-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div>
          <h1 class="page-title">跟进记录</h1>
          <p class="page-subtitle">记录客户跟进情况，提升销售转化</p>
        </div>
        <el-button type="primary" class="add-btn" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增跟进
        </el-button>
      </div>
    </div>

    <!-- 筛选区 -->
    <div class="filter-section">
      <el-form :inline="true" :model="searchForm" class="filter-form">
        <el-form-item label="跟进标题">
          <el-input
            v-model="searchForm.title"
            placeholder="搜索跟进标题"
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
        
        <el-form-item label="跟进方式">
          <el-select v-model="searchForm.contactType" placeholder="全部方式" clearable class="filter-select">
            <el-option label="电话" value="phone" />
            <el-option label="微信" value="wechat" />
            <el-option label="邮件" value="email" />
            <el-option label="拜访" value="visit" />
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
        <el-table-column prop="title" label="跟进标题" min-width="200">
          <template #default="{ row }">
            <span class="title-text">{{ row.title }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="customerName" label="客户名称" min-width="140" />
        
        <el-table-column prop="contactType" label="跟进方式" width="100">
          <template #default="{ row }">
            <el-tag :type="getContactTypeType(row.contactType)" size="small" effect="plain" class="contact-tag">
              {{ getContactTypeText(row.contactType) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="nextFollowTime" label="下次联系时间" width="150">
          <template #default="{ row }">
            <span class="time-text">{{ row.nextFollowTime || '未安排' }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="ownerName" label="负责人" width="120" />
        
        <el-table-column prop="createTime" label="创建时间" width="150" />
        
        <el-table-column label="操作" width="160" fixed="right" class="action-column">
          <template #default="{ row }">
            <div class="action-buttons">
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
      class="followup-dialog"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="followup-form"
        label-width="86px"
      >
        <el-form-item label="跟进标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入跟进标题" />
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
        
        <el-form-item label="跟进内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="请输入跟进内容"
          />
        </el-form-item>
        
        <el-form-item label="跟进方式" prop="contactType">
          <el-select v-model="form.contactType" placeholder="请选择方式" style="width: 100%">
            <el-option label="电话" value="phone" />
            <el-option label="微信" value="wechat" />
            <el-option label="邮件" value="email" />
            <el-option label="拜访" value="visit" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="下次联系时间" prop="nextFollowTime">
          <el-date-picker
            v-model="form.nextFollowTime"
            type="datetime"
            placeholder="选择下次联系时间"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
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
import { Search, Refresh, Plus, Phone, ChatDotRound, Message, Location } from '@element-plus/icons-vue'
import { getFollowUpList, createFollowUp, updateFollowUp, deleteFollowUp } from '@/api/follow'
import { getAllCustomers } from '@/api/customer'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增跟进')
const formRef = ref(null)
const customers = ref([])

const searchForm = reactive({
  title: '',
  customerName: '',
  contactType: ''
})

const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

const form = reactive({
  followUpId: null,
  title: '',
  customerId: null,
  content: '',
  contactType: 'phone',
  nextFollowTime: ''
})

const rules = {
  title: [
    { required: true, message: '请输入跟进标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  customerId: [
    { required: true, message: '请选择客户', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入跟进内容', trigger: 'blur' },
    { min: 5, message: '内容不能少于 5 个字符', trigger: 'blur' }
  ],
  contactType: [
    { required: true, message: '请选择跟进方式', trigger: 'change' }
  ]
}

const getContactTypeType = (type) => {
  const types = { phone: 'primary', wechat: 'success', email: 'warning', visit: 'danger' }
  return types[type] || 'info'
}

const getContactTypeIcon = (type) => {
  const icons = { phone: Phone, wechat: ChatDotRound, email: Message, visit: Location }
  return icons[type] || Phone
}

const getContactTypeText = (type) => {
  const texts = { phone: '电话', wechat: '微信', email: '邮件', visit: '拜访' }
  return texts[type] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    const res = await getFollowUpList(params)
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
  searchForm.title = ''
  searchForm.customerName = ''
  searchForm.contactType = ''
  handleSearch()
}

const handleRefresh = () => {
  loadData()
  ElMessage.success('刷新成功')
}

const handleAdd = () => {
  dialogTitle.value = '新增跟进'
  Object.assign(form, {
    followUpId: null,
    title: '',
    customerId: null,
    content: '',
    contactType: 'phone',
    nextFollowTime: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑跟进'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除这条跟进记录吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await deleteFollowUp(row.followUpId)
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
        const api = form.followUpId ? updateFollowUp : createFollowUp
        const res = await api(form)
        if (res.code === 200) {
          ElMessage.success(form.followUpId ? '更新成功' : '创建成功')
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
.followup-page {
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

.title-text {
  font-weight: 500;
  color: #1a1a1a;
}

.contact-tag {
  border-radius: 6px;
  font-weight: 500;
  padding: 2px 10px;
  display: inline-flex;
  align-items: center;
}

.contact-icon {
  font-size: 14px;
}

.time-text {
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
.followup-dialog .el-dialog__header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.followup-dialog .el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.followup-dialog .el-dialog__body {
  padding: 24px;
}

.followup-form {
  padding-top: 8px;
}

:deep(.followup-form .el-form-item__label) {
  font-weight: 500;
  color: #666;
}

:deep(.followup-form .el-input__wrapper),
:deep(.followup-form .el-select .el-input__wrapper),
:deep(.followup-form .el-textarea__inner) {
  border-radius: 8px;
  background: #fafbfc;
}

.followup-dialog .el-dialog__footer {
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
  .followup-page {
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
