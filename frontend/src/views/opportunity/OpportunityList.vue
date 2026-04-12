<template>
  <div class="opportunity-list">
    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <el-form :model="filterForm" inline>
        <el-form-item label="商机名称">
          <el-input 
            v-model="filterForm.opportunityName" 
            placeholder="请输入商机名称" 
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

        <el-form-item label="商机阶段">
          <el-select v-model="filterForm.stage" placeholder="请选择" clearable>
            <el-option label="初步接洽" :value="1" />
            <el-option label="需求分析" :value="2" />
            <el-option label="方案报价" :value="3" />
            <el-option label="谈判审核" :value="4" />
            <el-option label="赢单" :value="5" />
            <el-option label="输单" :value="6" />
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
        <i class="el-icon-plus"></i> 新建商机
      </el-button>
    </el-card>

    <!-- 商机列表 -->
    <el-card class="table-card">
      <el-table 
        :data="tableData" 
        v-loading="loading"
      >
        <el-table-column prop="opportunityName" label="商机名称" min-width="150" />
        <el-table-column prop="customerName" label="客户名称" min-width="140" />
        <el-table-column prop="amount" label="预计金额" width="120">
          <template #default="{ row }">
            <span style="color: #1e3a5f; font-weight: 600">¥{{ formatAmount(row.amount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stage" label="阶段" width="110">
          <template #default="{ row }">
            <el-tag :type="getStageType(row.stage)">
              {{ getStageText(row.stage) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="probability" label="成功率" width="90">
          <template #default="{ row }">
            <span>{{ row.probability }}%</span>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="负责人" width="100" />
        <el-table-column prop="expectedCloseDate" label="预计成交日期" width="140">
          <template #default="{ row }">
            <span v-if="row.expectedCloseDate">{{ row.expectedCloseDate }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
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
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
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
        <el-form-item label="商机名称" prop="opportunityName">
          <el-input v-model="form.opportunityName" placeholder="请输入商机名称" />
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
        
        <el-form-item label="预计金额" prop="amount">
          <el-input-number
            v-model="form.amount"
            :min="0"
            :precision="2"
            :step="1000"
            placeholder="请输入金额"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="商机阶段" prop="stage">
          <el-select v-model="form.stage" placeholder="请选择阶段" style="width: 100%">
            <el-option label="初步接洽" :value="1" />
            <el-option label="需求分析" :value="2" />
            <el-option label="方案报价" :value="3" />
            <el-option label="谈判审核" :value="4" />
            <el-option label="赢单" :value="5" />
            <el-option label="输单" :value="6" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="成功率" prop="probability">
          <el-slider v-model="form.probability" :min="0" :max="100" :step="5" show-input />
        </el-form-item>
        
        <el-form-item label="预计成交日期" prop="expectedCloseDate">
          <el-date-picker
            v-model="form.expectedCloseDate"
            type="date"
            placeholder="选择预计成交日期"
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
  getOpportunityList,
  createOpportunity,
  updateOpportunity,
  deleteOpportunity
} from '@/api/opportunity'
import { getAllCustomers } from '@/api/customer'

const router = useRouter()

// 加载状态
const loading = ref(false)
const submitLoading = ref(false)

// 表格数据
const tableData = ref([])

// 筛选表单
const filterForm = reactive({
  opportunityName: '',
  customerName: '',
  stage: null
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
  opportunityId: null,
  opportunityName: '',
  customerId: null,
  amount: 0,
  stage: 1,
  probability: 50,
  expectedCloseDate: '',
  remark: ''
})

// 验证规则
const rules = {
  opportunityName: [
    { required: true, message: '请输入商机名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  customerId: [
    { required: true, message: '请选择客户', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入预计金额', trigger: 'blur' }
  ],
  stage: [
    { required: true, message: '请选择商机阶段', trigger: 'change' }
  ],
  probability: [
    { required: true, message: '请输入成功率', trigger: 'blur' }
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
    const res = await getOpportunityList({
      current: pagination.current,
      size: pagination.size,
      ...filterForm
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      pagination.total = Number(res.data.total) || 0
    }
  } catch (error) {
    ElMessage.error('获取商机列表失败')
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
  filterForm.opportunityName = ''
  filterForm.customerName = ''
  filterForm.stage = null
  handleSearch()
}

// 创建商机
const handleCreate = () => {
  dialogTitle.value = '新建商机'
  Object.assign(form, {
    opportunityId: null,
    opportunityName: '',
    customerId: null,
    amount: 0,
    stage: 1,
    probability: 50,
    expectedCloseDate: '',
    remark: ''
  })
  dialogVisible.value = true
}

// 查看详情
const handleView = (row) => {
  // TODO: 如果有详情页，可以跳转
  ElMessage.info('详情页开发中...')
}

// 编辑商机
const handleEdit = (row) => {
  dialogTitle.value = '编辑商机'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

// 删除商机
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该商机吗？', '提示', {
      type: 'warning'
    })
    const res = await deleteOpportunity(row.opportunityId)
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
        const api = form.opportunityId ? updateOpportunity : createOpportunity
        const res = await api(form)
        if (res.code === 200) {
          ElMessage.success(form.opportunityId ? '更新成功' : '创建成功')
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

const handlePageChange = () => {
  fetchData()
}

// 格式化金额
const formatAmount = (value) => {
  if (!value) return '0.00'
  return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 获取阶段类型
const getStageType = (stage) => {
  const types = {
    1: 'info',
    2: 'warning',
    3: 'primary',
    4: 'success',
    5: 'danger',
    6: 'info'
  }
  return types[stage] || 'info'
}

// 获取阶段文本
const getStageText = (stage) => {
  const texts = {
    1: '初步接洽',
    2: '需求分析',
    3: '方案报价',
    4: '谈判审核',
    5: '赢单',
    6: '输单'
  }
  return texts[stage] || '未知'
}
</script>

<style scoped>
.opportunity-list {
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
