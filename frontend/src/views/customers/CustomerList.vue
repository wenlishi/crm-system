<template>
  <div class="customer-page">
    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="客户名称">
          <el-input
            v-model="searchForm.customerName"
            placeholder="请输入客户名称"
            clearable
            style="width: 160px"
          />
        </el-form-item>
        
        <el-form-item label="客户级别">
          <el-select v-model="searchForm.level" placeholder="请选择" clearable style="width: 120px">
            <el-option label="A 类客户" value="A" />
            <el-option label="B 类客户" value="B" />
            <el-option label="C 类客户" value="C" />
            <el-option label="D 类客户" value="D" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="客户状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="跟进中" :value="0" />
            <el-option label="已成交" :value="1" />
            <el-option label="已流失" :value="2" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <i class="el-icon-search"></i> 查询
          </el-button>
          <el-button @click="handleReset">
            <i class="el-icon-refresh"></i> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <el-card class="toolbar-card">
      <el-button type="primary" @click="handleAdd">
        <i class="el-icon-plus"></i> 新建客户
      </el-button>
      <el-button type="success" @click="handleExport" :disabled="tableData.length === 0">
        <i class="el-icon-download"></i> 导出客户
      </el-button>
      <el-button type="warning" @click="handleBatchAssign" :disabled="selectedRows.length === 0">
        <i class="el-icon-user"></i> 批量分配
      </el-button>
    </el-card>

    <!-- 批量分配对话框 -->
    <el-dialog
      v-model="assignDialogVisible"
      title="批量分配客户"
      width="500px"
      @close="handleAssignDialogClose"
    >
      <div class="assign-content">
        <el-alert
          title="分配须知"
          type="warning"
          :closable="false"
          show-icon
          class="mb-4"
        >
          <p>1. 分配后客户将归属于所选销售员</p>
          <p>2. 原负责人将失去客户权限</p>
          <p>3. 请谨慎选择分配对象</p>
        </el-alert>

        <el-form :model="assignForm" label-width="100px">
          <el-form-item label="当前客户数">
            <el-tag type="info">{{ selectedRows.length }} 个</el-tag>
          </el-form-item>
          <el-form-item label="分配给" required>
            <el-select v-model="assignForm.ownerId" placeholder="请选择销售员" style="width: 100%">
              <el-option
                v-for="item in salesmen"
                :key="item.userId"
                :label="item.username"
                :value="item.userId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="分配说明">
            <el-input
              v-model="assignForm.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入分配说明（可选）"
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign" :loading="assignLoading">
          确认分配
        </el-button>
      </template>
    </el-dialog>

    <!-- 客户列表 -->
    <el-card class="table-card">

      <el-table 
        :data="tableData" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="customerName" label="客户名称" min-width="150" />
        <el-table-column prop="customerType" label="类型" width="80">
          <template #default="{ row }">
            <el-tag>{{ row.customerType === 1 ? '个人' : '企业' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contact" label="联系人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="email-text">{{ row.email || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)">
              {{ row.level }}类
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="负责人" width="100" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              @click="handleView(row)"
            >
              查看
            </el-button>
            <el-button 
              size="small" 
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="primary" 
              @click="handleAssign(row)"
            >
              分配
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
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
    </el-card>

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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, User } from '@element-plus/icons-vue'
import { getCustomerList, createCustomer, updateCustomer, deleteCustomer, batchAssignCustomer } from '@/api/customer'
import { getUserList } from '@/api/user'
import * as XLSX from 'xlsx'

const router = useRouter()

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增客户')
const customerFormRef = ref(null)
const selectedRows = ref([])

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

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 分配对话框相关
const assignDialogVisible = ref(false)
const assignLoading = ref(false)
const salesmen = ref([])
const assignForm = reactive({
  ownerId: null,
  remark: ''
})

// 加载销售员列表
const loadSalesmen = async () => {
  try {
    const res = await getUserList({ status: 1 })
    if (res.code === 200) {
      salesmen.value = res.data.records || []
    }
  } catch (error) {
    console.error('加载销售员失败:', error)
  }
}

// 批量分配
const handleBatchAssign = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要分配的客户')
    return
  }
  
  assignDialogVisible.value = true
}

// 提交分配
const submitAssign = async () => {
  if (!assignForm.ownerId) {
    ElMessage.warning('请选择销售员')
    return
  }
  
  assignLoading.value = true
  try {
    const customerIds = selectedRows.value.map(row => row.customerId)
    const res = await batchAssignCustomer({
      customerIds,
      ownerId: assignForm.ownerId,
      remark: assignForm.remark
    })
    
    if (res.code === 200) {
      ElMessage.success('分配成功')
      assignDialogVisible.value = false
      loadData()
    }
  } catch (error) {
    console.error('分配失败:', error)
    ElMessage.error(error.response?.data?.message || '分配失败')
  } finally {
    assignLoading.value = false
  }
}

// 分配对话框关闭
const handleAssignDialogClose = () => {
  assignForm.ownerId = null
  assignForm.remark = ''
}

const handleExport = async () => {
  try {
    // 获取所有客户数据（不分页）
    const { data } = await getCustomerList({ 
      current: 1, 
      size: 10000,
      customerName: searchForm.customerName,
      level: searchForm.level,
      status: searchForm.status
    })
    
    const customers = data.records || []
    
    if (customers.length === 0) {
      ElMessage.warning('没有可导出的数据')
      return
    }
    
    // 准备导出数据
    const exportData = customers.map(customer => ({
      '客户名称': customer.customerName,
      '客户类型': customer.customerType === 1 ? '个人' : '企业',
      '联系人': customer.contact,
      '联系电话': customer.phone,
      '邮箱': customer.email,
      '客户级别': `${customer.level}类`,
      '客户状态': getStatusText(customer.status),
      '客户来源': customer.source || '-',
      '负责人': customer.ownerName || '-',
      '创建时间': customer.createTime || '-'
    }))
    
    // 创建工作簿
    const worksheet = XLSX.utils.json_to_sheet(exportData)
    const workbook = XLSX.utils.book_new()
    
    // 设置列宽
    const colWidths = [
      { wch: 20 }, // 客户名称
      { wch: 10 }, // 客户类型
      { wch: 15 }, // 联系人
      { wch: 15 }, // 联系电话
      { wch: 25 }, // 邮箱
      { wch: 10 }, // 客户级别
      { wch: 10 }, // 客户状态
      { wch: 15 }, // 客户来源
      { wch: 15 }, // 负责人
      { wch: 20 }  // 创建时间
    ]
    worksheet['!cols'] = colWidths
    
    // 添加工作表
    XLSX.utils.book_append_sheet(workbook, worksheet, '客户列表')
    
    // 生成文件名
    const fileName = `客户列表_${new Date().toISOString().slice(0, 10)}.xlsx`
    
    // 下载文件
    XLSX.writeFile(workbook, fileName)
    
    ElMessage.success(`成功导出 ${customers.length} 条客户数据`)
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败，请重试')
  }
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
  router.push(`/customers/${row.customerId}`)
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
  loadSalesmen()
})
</script>

<style scoped>
.customer-page {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.toolbar-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.assign-content {
  padding: 10px 0;
}

.mb-4 {
  margin-bottom: 16px;
}

:deep(.el-alert__content) {
  font-size: 13px;
}

:deep(.el-alert__content p) {
  margin: 4px 0;
  line-height: 1.6;
}

.email-text {
  display: block;
  max-width: 170px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #666;
}
</style>
