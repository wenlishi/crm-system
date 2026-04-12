<template>
  <div class="pool-list">
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

        <el-form-item label="掉入原因">
          <el-select v-model="searchForm.reason" placeholder="请选择" clearable style="width: 140px">
            <el-option label="超期未跟进" value="overdue" />
            <el-option label="销售主动退回" value="return" />
            <el-option label="离职交接" value="resign" />
            <el-option label="其他" value="other" />
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
      <el-button type="primary" @click="handleClaim" :disabled="selectedRows.length === 0">
        <i class="el-icon-user"></i> 领取客户
      </el-button>
      <el-button type="warning" @click="handleBatchAssign" :disabled="selectedRows.length === 0">
        <i class="el-icon-user"></i> 批量分配
      </el-button>
    </el-card>

    <!-- 公海池列表 -->
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
        <el-table-column prop="level" label="级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)">
              {{ row.level }}类
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="previousOwner" label="原负责人" width="100" />
        <el-table-column prop="poolReason" label="掉入原因" width="120">
          <template #default="{ row }">
            <el-tag size="small" :type="getReasonType(row.poolReason)">
              {{ getReasonText(row.poolReason) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="poolTime" label="掉入时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              type="primary"
              @click="handleClaimSingle(row)"
            >
              领取
            </el-button>
            <el-button 
              size="small" 
              @click="handleView(row)"
            >
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container" v-if="pagination.total > 0">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 领取客户对话框 -->
    <el-dialog
      v-model="claimDialogVisible"
      :title="claimDialogTitle"
      width="500px"
      @close="handleClaimDialogClose"
    >
      <div class="claim-content">
        <el-alert
          title="领取须知"
          type="info"
          :closable="false"
          show-icon
          class="mb-4"
        >
          <p>1. 领取后客户将归属于您，由您负责跟进</p>
          <p>2. 每位销售员最多可领取 50 个客户</p>
          <p>3. 领取后 7 天内必须进行首次跟进</p>
          <p>4. 超期未跟进将自动退回公海池</p>
        </el-alert>

        <el-form :model="claimForm" label-width="100px">
          <el-form-item label="当前客户数">
            <el-tag type="info">{{ myCustomerCount }} / 50</el-tag>
          </el-form-item>
          <el-form-item label="领取数量">
            <el-tag type="success">{{ selectedRows.length }}</el-tag>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="claimDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitClaim" :loading="claimLoading">
          确认领取
        </el-button>
      </template>
    </el-dialog>

    <!-- 批量分配对话框 -->
    <el-dialog
      v-model="assignDialogVisible"
      title="批量分配客户"
      width="500px"
      @close="handleAssignDialogClose"
    >
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="分配给">
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
            placeholder="请输入分配说明"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign" :loading="assignLoading">
          确认分配
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPoolList, claimCustomer, batchAssignCustomer } from '@/api/customerPool'
import { getUserList } from '@/api/user'

const loading = ref(false)
const claimLoading = ref(false)
const assignLoading = ref(false)
const tableData = ref([])
const selectedRows = ref([])

const searchForm = reactive({
  customerName: '',
  level: '',
  reason: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 领取对话框
const claimDialogVisible = ref(false)
const claimDialogTitle = ref('领取客户')
const claimForm = reactive({
  myCustomerCount: 0
})

// 分配对话框
const assignDialogVisible = ref(false)
const assignForm = reactive({
  ownerId: null,
  remark: ''
})

// 销售员列表
const salesmen = ref([])

// 我的客户数
const myCustomerCount = ref(0)

onMounted(() => {
  fetchData()
  loadSalesmen()
  loadMyCustomerCount()
})

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    const res = await getPoolList(params)
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

const loadMyCustomerCount = async () => {
  try {
    // TODO: 调用 API 获取我的客户数
    myCustomerCount.value = 28 // 模拟数据
  } catch (error) {
    console.error('加载客户数失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

const handleReset = () => {
  searchForm.customerName = ''
  searchForm.level = ''
  searchForm.reason = ''
  handleSearch()
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection.map(item => item.customerId)
}

// 领取单个客户
const handleClaimSingle = (row) => {
  selectedRows.value = [row.customerId]
  handleClaim()
}

// 领取客户
const handleClaim = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要领取的客户')
    return
  }

  if (myCustomerCount.value + selectedRows.value.length > 50) {
    ElMessage.warning('领取后客户数将超过上限（50 个）')
    return
  }

  claimDialogVisible.value = true
}

// 提交领取
const submitClaim = async () => {
  claimLoading.value = true
  try {
    const res = await claimCustomer({
      customerIds: selectedRows.value
    })
    if (res.code === 200) {
      ElMessage.success('领取成功')
      claimDialogVisible.value = false
      fetchData()
      loadMyCustomerCount()
    }
  } catch (error) {
    console.error('领取失败:', error)
    ElMessage.error(error.response?.data?.message || '领取失败')
  } finally {
    claimLoading.value = false
  }
}

// 领取对话框关闭
const handleClaimDialogClose = () => {
  selectedRows.value = []
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
    const res = await batchAssignCustomer({
      customerIds: selectedRows.value,
      ownerId: assignForm.ownerId,
      remark: assignForm.remark
    })
    if (res.code === 200) {
      ElMessage.success('分配成功')
      assignDialogVisible.value = false
      fetchData()
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

const handleView = (row) => {
  ElMessage.info('详情功能开发中...')
}

const handleSizeChange = () => {
  fetchData()
}

const handleCurrentChange = () => {
  fetchData()
}

const getLevelType = (level) => {
  const types = {
    A: 'danger',
    B: 'warning',
    C: '',
    D: 'info'
  }
  return types[level] || ''
}

const getReasonType = (reason) => {
  const types = {
    overdue: 'warning',
    return: 'info',
    resign: 'danger',
    other: 'info'
  }
  return types[reason] || 'info'
}

const getReasonText = (reason) => {
  const texts = {
    overdue: '超期未跟进',
    return: '主动退回',
    resign: '离职交接',
    other: '其他'
  }
  return texts[reason] || reason
}
</script>

<style scoped>
.pool-list {
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

.claim-content {
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
</style>
