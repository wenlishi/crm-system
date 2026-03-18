<template>
  <div class="opportunity-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="商机名称">
          <el-input v-model="searchForm.opportunityName" placeholder="请输入名称" clearable />
        </el-form-item>
        <el-form-item label="客户名称">
          <el-input v-model="searchForm.customerName" placeholder="请输入客户" clearable />
        </el-form-item>
        <el-form-item label="商机阶段">
          <el-select v-model="searchForm.stage" placeholder="请选择阶段" clearable>
            <el-option label="初步接洽" value="initial" />
            <el-option label="需求分析" value="analysis" />
            <el-option label="方案报价" value="proposal" />
            <el-option label="谈判审核" value="negotiation" />
            <el-option label="赢单" value="won" />
            <el-option label="输单" value="lost" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <div class="table-header">
        <h3>商机列表</h3>
        <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增商机</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="opportunityName" label="商机名称" min-width="150" />
        <el-table-column prop="customerName" label="客户名称" min-width="120" />
        <el-table-column prop="amount" label="预计金额" width="120">
          <template #default="{ row }">¥{{ row.amount?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="stage" label="阶段" width="100">
          <template #default="{ row }">
            <el-tag :type="getStageType(row.stage)">{{ getStageText(row.stage) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="probability" label="成功率" width="80">
          <template #default="{ row }">{{ row.probability }}%</template>
        </el-table-column>
        <el-table-column prop="ownerName" label="负责人" width="100" />
        <el-table-column prop="expectedCloseDate" label="预计成交日期" width="120" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="handleDialogClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="商机名称" prop="opportunityName">
          <el-input v-model="form.opportunityName" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="form.customerId" placeholder="请选择客户" style="width: 100%">
            <el-option v-for="item in customers" :key="item.customerId" :label="item.customerName" :value="item.customerId" />
          </el-select>
        </el-form-item>
        <el-form-item label="预计金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商机阶段" prop="stage">
          <el-select v-model="form.stage" style="width: 100%">
            <el-option label="初步接洽" value="initial" />
            <el-option label="需求分析" value="analysis" />
            <el-option label="方案报价" value="proposal" />
            <el-option label="谈判审核" value="negotiation" />
            <el-option label="赢单" value="won" />
            <el-option label="输单" value="lost" />
          </el-select>
        </el-form-item>
        <el-form-item label="成功率" prop="probability">
          <el-input-number v-model="form.probability" :min="0" :max="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预计成交日期" prop="expectedCloseDate">
          <el-date-picker v-model="form.expectedCloseDate" type="date" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOpportunityList, createOpportunity, updateOpportunity, deleteOpportunity } from '@/api/opportunity'
import { getAllCustomers } from '@/api/customer'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增商机')
const formRef = ref(null)
const customers = ref([])

const searchForm = reactive({ opportunityName: '', customerName: '', stage: '' })
const pagination = reactive({ current: 1, size: 10, total: 0 })

const form = reactive({
  opportunityId: null, opportunityName: '', customerId: null, amount: 0, stage: 'initial', probability: 50, expectedCloseDate: '', remark: ''
})

const rules = {
  opportunityName: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  stage: [{ required: true, message: '请选择阶段', trigger: 'change' }]
}

const getStageType = (stage) => {
  const types = { initial: 'info', analysis: 'primary', proposal: 'warning', negotiation: 'danger', won: 'success', lost: 'info' }
  return types[stage] || 'info'
}

const getStageText = (stage) => {
  const texts = { initial: '初步接洽', analysis: '需求分析', proposal: '方案报价', negotiation: '谈判审核', won: '赢单', lost: '输单' }
  return texts[stage] || stage
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { current: pagination.current, size: pagination.size, ...searchForm }
    const res = await getOpportunityList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) { console.error('加载失败:', error) } finally { loading.value = false }
}

const loadCustomers = async () => {
  try { const res = await getAllCustomers(); customers.value = res.data || [] } catch (error) { console.error('加载客户失败:', error) }
}

const handleSearch = () => { pagination.current = 1; loadData() }
const handleReset = () => { searchForm.opportunityName = ''; searchForm.customerName = ''; searchForm.stage = ''; handleSearch() }
const handleAdd = () => { dialogTitle.value = '新增商机'; dialogVisible.value = true }
const handleEdit = (row) => { dialogTitle.value = '编辑商机'; Object.assign(form, row); dialogVisible.value = true }

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除吗？', '提示', { type: 'warning' })
    await deleteOpportunity(row.opportunityId)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) { if (error !== 'cancel') console.error('删除失败:', error) }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (form.opportunityId) { await updateOpportunity(form); ElMessage.success('修改成功') }
        else { await createOpportunity(form); ElMessage.success('创建成功') }
        dialogVisible.value = false
        loadData()
      } catch (error) { console.error('提交失败:', error) } finally { submitLoading.value = false }
    }
  })
}

const handleDialogClose = () => { formRef.value?.resetFields(); Object.assign(form, { opportunityId: null, opportunityName: '', customerId: null, amount: 0, stage: 'initial', probability: 50, expectedCloseDate: '', remark: '' }) }
const handleSizeChange = () => { loadData() }
const handleCurrentChange = () => { loadData() }

onMounted(() => { loadData(); loadCustomers() })
</script>

<style scoped>
.opportunity-list { padding: 0; }
.search-card { margin-bottom: 20px; border-radius: 8px; }
.table-card { border-radius: 8px; }
.table-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.table-header h3 { margin: 0; font-size: 16px; color: #333; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
