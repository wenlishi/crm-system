<template>
  <div class="contract-list">
    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <el-form :model="filterForm" inline>
        <el-form-item label="合同名称">
          <el-input 
            v-model="filterForm.contractName" 
            placeholder="请输入合同名称" 
            clearable
            style="width: 200px"
          />
        </el-form-item>
        
        <el-form-item label="客户名称">
          <el-input 
            v-model="filterForm.customerName" 
            placeholder="请输入客户名称" 
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="合同状态">
          <el-select v-model="filterForm.status" placeholder="请选择" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="待审核" :value="1" />
            <el-option label="已审核" :value="2" />
            <el-option label="执行中" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已终止" :value="5" />
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
      <el-button type="primary" @click="handleCreate">
        <i class="el-icon-plus"></i> 新建合同
      </el-button>
    </el-card>

    <!-- 合同列表 -->
    <el-card class="table-card">
      <el-table 
        :data="tableData" 
        v-loading="loading"
      >
        <el-table-column prop="contractName" label="合同名称" min-width="150" />
        <el-table-column prop="customerName" label="客户名称" min-width="140" />
        <el-table-column prop="amount" label="合同金额" width="120">
          <template #default="{ row }">
            <span style="color: #1e3a5f; font-weight: 600">¥{{ formatAmount(row.amount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="signDate" label="签订日期" width="120">
          <template #default="{ row }">
            <span v-if="row.signDate">{{ row.signDate }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="负责人" width="100" />
        <el-table-column label="操作" width="240" fixed="right">
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
              type="danger" 
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container" v-if="pagination.total > 0">
        <el-pagination
          :current-page="pagination.current"
          :page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
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
        
        <el-form-item label="合同状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="草稿" :value="0" />
            <el-option label="待审核" :value="1" />
            <el-option label="已审核" :value="2" />
            <el-option label="执行中" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已终止" :value="5" />
          </el-select>
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
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getContractList,
  createContract,
  updateContract,
  deleteContract
} from '@/api/contract'
import { getAllCustomers } from '@/api/customer'

const router = useRouter()

// 加载状态
const loading = ref(false)
const submitLoading = ref(false)

// 表格数据
const tableData = ref([])

// 筛选表单
const filterForm = reactive({
  contractName: '',
  customerName: '',
  status: null
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

// 表单数据
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

// 验证规则
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
  status: [
    { required: true, message: '请选择合同状态', trigger: 'change' }
  ],
  signDate: [
    { required: true, message: '请选择签订日期', trigger: 'change' }
  ]
}

// 客户列表
const customers = ref([])

// 挂载时加载数据
onMounted(() => {
  fetchData()
  loadCustomers()
})

// 获取客户列表
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

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getContractList({
      current: pagination.current,
      size: pagination.size,
      ...filterForm
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = Number(res.data.total) || 0
    }
  } catch (error) {
    ElMessage.error('获取合同列表失败')
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  filterForm.contractName = ''
  filterForm.customerName = ''
  filterForm.status = null
  handleSearch()
}

// 创建合同
const handleCreate = () => {
  dialogTitle.value = '新建合同'
  Object.assign(form, {
    contractId: null,
    contractName: '',
    customerId: null,
    amount: 0,
    status: 0,
    signDate: '',
    effectiveDate: '',
    expiryDate: '',
    remark: ''
  })
  dialogVisible.value = true
}

// 查看详情
const handleView = (row) => {
  // TODO: 如果有详情页，可以跳转
  ElMessage.info('详情页开发中...')
}

// 编辑合同
const handleEdit = (row) => {
  dialogTitle.value = '编辑合同'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 删除合同
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该合同吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteContract(row.contractId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
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
          fetchData()
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 对话框关闭
const handleDialogClose = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 分页变化
const handleSizeChange = () => {
  fetchData()
}

const handleCurrentChange = () => {
  fetchData()
}

// 格式化金额
const formatAmount = (value) => {
  if (!value) return '0.00'
  return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'success',
    5: 'info'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    0: '草稿',
    1: '待审核',
    2: '已审核',
    3: '执行中',
    4: '已完成',
    5: '已终止'
  }
  return texts[status] || '未知'
}
</script>

<style scoped>
.contract-list {
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
</style>
