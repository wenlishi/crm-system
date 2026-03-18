<template>
  <div class="contract-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="合同名称">
          <el-input v-model="searchForm.contractName" placeholder="请输入名称" clearable />
        </el-form-item>
        <el-form-item label="客户名称">
          <el-input v-model="searchForm.customerName" placeholder="请输入客户" clearable />
        </el-form-item>
        <el-form-item label="合同状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" value="draft" />
            <el-option label="审核中" value="reviewing" />
            <el-option label="已生效" value="active" />
            <el-option label="已完成" value="completed" />
            <el-option label="已终止" value="terminated" />
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
        <h3>合同列表</h3>
        <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增合同</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="contractName" label="合同名称" min-width="150" />
        <el-table-column prop="customerName" label="客户名称" min-width="120" />
        <el-table-column prop="amount" label="合同金额" width="120">
          <template #default="{ row }">¥{{ row.amount?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="signDate" label="签订日期" width="120" />
        <el-table-column prop="ownerName" label="负责人" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" @click="handleUpdateStatus(row)">状态</el-button>
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
        <el-form-item label="合同名称" prop="contractName">
          <el-input v-model="form.contractName" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="form.customerId" placeholder="请选择客户" style="width: 100%">
            <el-option v-for="item in customers" :key="item.customerId" :label="item.customerName" :value="item.customerId" />
          </el-select>
        </el-form-item>
        <el-form-item label="合同金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="签订日期" prop="signDate">
          <el-date-picker v-model="form.signDate" type="date" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="生效日期" prop="effectiveDate">
          <el-date-picker v-model="form.effectiveDate" type="date" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="到期日期" prop="expiryDate">
          <el-date-picker v-model="form.expiryDate" type="date" placeholder="选择日期" style="width: 100%" />
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

    <el-dialog v-model="statusDialogVisible" title="更新合同状态" width="400px">
      <el-form :model="statusForm" label-width="80px">
        <el-form-item label="合同状态">
          <el-select v-model="statusForm.status" style="width: 100%">
            <el-option label="草稿" value="draft" />
            <el-option label="审核中" value="reviewing" />
            <el-option label="已生效" value="active" />
            <el-option label="已完成" value="completed" />
            <el-option label="已终止" value="terminated" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleStatusSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getContractList, createContract, updateContract, deleteContract, updateContractStatus } from '@/api/contract'
import { getAllCustomers } from '@/api/customer'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增合同')
const formRef = ref(null)
const customers = ref([])
const statusDialogVisible = ref(false)
const currentContract = ref(null)

const searchForm = reactive({ contractName: '', customerName: '', status: '' })
const pagination = reactive({ current: 1, size: 10, total: 0 })
const form = reactive({ contractId: null, contractName: '', customerId: null, amount: 0, signDate: '', effectiveDate: '', expiryDate: '', remark: '' })
const statusForm = reactive({ status: '' })

const rules = {
  contractName: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }]
}

const getStatusType = (status) => {
  const types = { draft: 'info', reviewing: 'warning', active: 'primary', completed: 'success', terminated: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { draft: '草稿', reviewing: '审核中', active: '已生效', completed: '已完成', terminated: '已终止' }
  return texts[status] || status
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { current: pagination.current, size: pagination.size, ...searchForm }
    const res = await getContractList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) { console.error('加载失败:', error) } finally { loading.value = false }
}

const loadCustomers = async () => {
  try { const res = await getAllCustomers(); customers.value = res.data || [] } catch (error) { console.error('加载客户失败:', error) }
}

const handleSearch = () => { pagination.current = 1; loadData() }
const handleReset = () => { searchForm.contractName = ''; searchForm.customerName = ''; searchForm.status = ''; handleSearch() }
const handleAdd = () => { dialogTitle.value = '新增合同'; dialogVisible.value = true }
const handleEdit = (row) => { dialogTitle.value = '编辑合同'; Object.assign(form, row); dialogVisible.value = true }

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除吗？', '提示', { type: 'warning' })
    await deleteContract(row.contractId)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) { if (error !== 'cancel') console.error('删除失败:', error) }
}

const handleUpdateStatus = (row) => {
  currentContract.value = row
  statusForm.status = row.status
  statusDialogVisible.value = true
}

const handleStatusSubmit = async () => {
  try {
    await updateContractStatus(currentContract.value.contractId, statusForm.status)
    ElMessage.success('状态更新成功')
    statusDialogVisible.value = false
    loadData()
  } catch (error) { console.error('更新失败:', error) }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (form.contractId) { await updateContract(form); ElMessage.success('修改成功') }
        else { await createContract(form); ElMessage.success('创建成功') }
        dialogVisible.value = false
        loadData()
      } catch (error) { console.error('提交失败:', error) } finally { submitLoading.value = false }
    }
  })
}

const handleDialogClose = () => { formRef.value?.resetFields(); Object.assign(form, { contractId: null, contractName: '', customerId: null, amount: 0, signDate: '', effectiveDate: '', expiryDate: '', remark: '' }) }
const handleSizeChange = () => { loadData() }
const handleCurrentChange = () => { loadData() }

onMounted(() => { loadData(); loadCustomers() })
</script>

<style scoped>
.contract-list { padding: 0; }
.search-card { margin-bottom: 20px; border-radius: 8px; }
.table-card { border-radius: 8px; }
.table-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.table-header h3 { margin: 0; font-size: 16px; color: #333; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
