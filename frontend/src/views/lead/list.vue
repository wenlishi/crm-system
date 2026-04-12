<template>
  <div class="lead-list">
    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <el-form :model="filterForm" inline>
        <el-form-item label="线索状态">
          <el-select v-model="filterForm.status" placeholder="请选择" clearable>
            <el-option label="待联系" :value="1" />
            <el-option label="联系中" :value="2" />
            <el-option label="已转化" :value="3" />
            <el-option label="已关闭" :value="4" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="线索级别">
          <el-select v-model="filterForm.level" placeholder="请选择" clearable>
            <el-option label="普通" :value="1" />
            <el-option label="意向" :value="2" />
            <el-option label="高意向" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="线索来源">
          <el-select v-model="filterForm.source" placeholder="请选择" clearable>
            <el-option label="网络营销" value="网络营销" />
            <el-option label="电话咨询" value="电话咨询" />
            <el-option label="客户推荐" value="客户推荐" />
            <el-option label="展会活动" value="展会活动" />
            <el-option label="其他" value="其他" />
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
        <i class="el-icon-plus"></i> 新建线索
      </el-button>
      <el-button 
        type="success" 
        @click="handleBatchConvert"
        :disabled="selectedRows.length === 0"
      >
        <i class="el-icon-refresh"></i> 批量转化
      </el-button>
      <el-button 
        type="warning" 
        @click="handleBatchClose"
        :disabled="selectedRows.length === 0"
      >
        <i class="el-icon-close"></i> 批量关闭
      </el-button>
    </el-card>

    <!-- 线索列表 -->
    <el-card class="table-card">
      <el-table 
        :data="tableData" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="leadName" label="线索名称" min-width="120" />
        <el-table-column prop="leadType" label="类型" width="80">
          <template #default="{ row }">
            <el-tag>{{ row.leadType === 1 ? '个人' : '企业' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="leadStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.leadStatus)">
              {{ getStatusText(row.leadStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="leadLevel" label="级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.leadLevel)">
              {{ getLevelText(row.leadLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="source" label="来源" width="120" />
        <el-table-column prop="ownerName" label="负责人" width="100" />
        <el-table-column prop="contact" label="联系人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="120" />
        <el-table-column prop="nextFollowTime" label="下次跟进时间" width="160">
          <template #default="{ row }">
            <el-tag v-if="row.nextFollowTime" type="warning" size="small">
              {{ formatDate(row.nextFollowTime) }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
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
              type="success" 
              @click="handleConvert(row)"
              :disabled="row.leadStatus === 3 || row.leadStatus === 4"
            >
              转化
            </el-button>
            <el-button 
              size="small" 
              type="warning" 
              @click="handleClose(row)"
              :disabled="row.leadStatus === 3 || row.leadStatus === 4"
            >
              关闭
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

    <!-- 线索转化弹窗 -->
    <el-dialog
      v-model="convertDialogVisible"
      title="线索转化为客户"
      width="600px"
      @close="handleConvertDialogClose"
    >
      <el-form :model="convertForm" label-width="100px">
        <el-form-item label="线索信息">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="线索名称">
              {{ convertForm.leadName }}
            </el-descriptions-item>
            <el-descriptions-item label="线索级别">
              {{ getLevelText(convertForm.leadLevel) }}
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">
              {{ convertForm.phone }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ convertForm.email }}
            </el-descriptions-item>
          </el-descriptions>
        </el-form-item>

        <el-form-item label="客户名称" required>
          <el-input v-model="convertForm.customerName" placeholder="请输入客户名称" />
        </el-form-item>

        <el-form-item label="客户类型" required>
          <el-radio-group v-model="convertForm.customerType">
            <el-radio :label="1">个人</el-radio>
            <el-radio :label="2">企业</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="客户级别" required>
          <el-select v-model="convertForm.customerLevel" placeholder="请选择客户级别">
            <el-option label="普通" :value="1" />
            <el-option label="VIP" :value="2" />
            <el-option label="重要" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="转化备注">
          <el-input
            v-model="convertForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入转化备注"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="convertDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitConvert" :loading="converting">
          确认转化
        </el-button>
      </template>
    </el-dialog>

    <!-- 线索关闭弹窗 -->
    <el-dialog
      v-model="closeDialogVisible"
      title="关闭线索"
      width="500px"
      @close="handleCloseDialogClose"
    >
      <el-form :model="closeForm" label-width="80px">
        <el-form-item label="线索名称">
          <span>{{ closeForm.leadName }}</span>
        </el-form-item>
        <el-form-item label="关闭原因" required>
          <el-select
            v-model="closeForm.reason"
            placeholder="请选择关闭原因"
            allow-create
            filterable
            style="width: 100%"
          >
            <el-option label="预算不足" value="预算不足" />
            <el-option label="需求不匹配" value="需求不匹配" />
            <el-option label="已选择其他供应商" value="已选择其他供应商" />
            <el-option label="联系方式无效" value="联系方式无效" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="closeDialogVisible = false">取消</el-button>
        <el-button type="warning" @click="submitClose" :loading="closing">
          确认关闭
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
  getLeadList,
  deleteLead,
  convertToCustomer,
  closeLead
} from '@/api/lead'

const router = useRouter()

// 加载状态
const loading = ref(false)
const converting = ref(false)
const closing = ref(false)

// 表格数据
const tableData = ref([])

// 筛选表单
const filterForm = reactive({
  status: null,
  level: null,
  source: null
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 选中的行
const selectedRows = ref([])

// 转化弹窗
const convertDialogVisible = ref(false)
const convertForm = reactive({
  leadId: null,
  leadName: '',
  leadLevel: 1,
  phone: '',
  email: '',
  customerName: '',
  customerType: 1,
  customerLevel: 1,
  remark: ''
})

// 关闭弹窗
const closeDialogVisible = ref(false)
const closeForm = reactive({
  leadId: null,
  leadName: '',
  reason: ''
})

// 挂载时加载数据
onMounted(() => {
  fetchData()
})

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getLeadList({
      current: pagination.current,
      size: pagination.size,
      ...filterForm
    })
    tableData.value = res.data.records
    pagination.total = Number(res.data.total) || 0
  } catch (error) {
    ElMessage.error('获取线索列表失败')
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
  filterForm.status = null
  filterForm.level = null
  filterForm.source = null
  handleSearch()
}

// 创建线索
const handleCreate = () => {
  router.push('/leads/create')
}

// 查看详情
const handleView = (row) => {
  router.push(`/leads/detail/${row.leadId}`)
}

// 编辑线索
const handleEdit = (row) => {
  router.push(`/leads/edit/${row.leadId}`)
}

// 转化线索
const handleConvert = (row) => {
  convertForm.leadId = row.leadId
  convertForm.leadName = row.leadName
  convertForm.leadLevel = row.leadLevel
  convertForm.phone = row.phone
  convertForm.email = row.email
  convertForm.customerName = row.leadName
  convertForm.customerType = row.leadType
  convertForm.customerLevel = 1
  convertForm.remark = ''
  convertDialogVisible.value = true
}

// 提交转化
const submitConvert = async () => {
  if (!convertForm.customerName) {
    ElMessage.warning('请输入客户名称')
    return
  }
  if (!convertForm.customerLevel) {
    ElMessage.warning('请选择客户级别')
    return
  }

  converting.value = true
  try {
    await convertToCustomer(convertForm.leadId, {
      customerName: convertForm.customerName,
      customerType: convertForm.customerType,
      customerLevel: convertForm.customerLevel,
      phone: convertForm.phone,
      email: convertForm.email,
      remark: convertForm.remark
    })
    ElMessage.success('转化成功')
    convertDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '转化失败')
  } finally {
    converting.value = false
  }
}

// 转化弹窗关闭
const handleConvertDialogClose = () => {
  convertForm.leadId = null
  convertForm.leadName = ''
  convertForm.leadLevel = 1
  convertForm.phone = ''
  convertForm.email = ''
  convertForm.customerName = ''
  convertForm.customerType = 1
  convertForm.customerLevel = 1
  convertForm.remark = ''
}

// 关闭线索
const handleClose = (row) => {
  closeForm.leadId = row.leadId
  closeForm.leadName = row.leadName
  closeForm.reason = ''
  closeDialogVisible.value = true
}

// 提交关闭
const submitClose = async () => {
  if (!closeForm.reason) {
    ElMessage.warning('请选择关闭原因')
    return
  }

  closing.value = true
  try {
    await closeLead(closeForm.leadId, closeForm.reason)
    ElMessage.success('关闭成功')
    closeDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '关闭失败')
  } finally {
    closing.value = false
  }
}

// 关闭弹窗关闭
const handleCloseDialogClose = () => {
  closeForm.leadId = null
  closeForm.leadName = ''
  closeForm.reason = ''
}

// 删除线索
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该线索吗？', '提示', {
      type: 'warning'
    })
    await deleteLead(row.leadId)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量转化
const handleBatchConvert = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要转化的线索')
    return
  }
  ElMessage.info('批量转化功能开发中...')
}

// 批量关闭
const handleBatchClose = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要关闭的线索')
    return
  }
  ElMessage.info('批量关闭功能开发中...')
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection.map(item => item.leadId)
}

// 分页变化
const handleSizeChange = () => {
  fetchData()
}

const handlePageChange = () => {
  fetchData()
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    1: 'info',
    2: 'warning',
    3: 'success',
    4: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    1: '待联系',
    2: '联系中',
    3: '已转化',
    4: '已关闭'
  }
  return texts[status] || '未知'
}

// 获取级别类型
const getLevelType = (level) => {
  const types = {
    1: '',
    2: 'warning',
    3: 'danger'
  }
  return types[level] || ''
}

// 获取级别文本
const getLevelText = (level) => {
  const texts = {
    1: '普通',
    2: '意向',
    3: '高意向'
  }
  return texts[level] || '未知'
}
</script>

<style scoped>
.lead-list {
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
