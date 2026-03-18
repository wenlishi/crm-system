<template>
  <div class="followup-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="跟进标题">
          <el-input v-model="searchForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        
        <el-form-item label="客户名称">
          <el-input v-model="searchForm.customerName" placeholder="请输入客户名称" clearable />
        </el-form-item>
        
        <el-form-item label="跟进方式">
          <el-select v-model="searchForm.contactType" placeholder="请选择方式" clearable>
            <el-option label="电话" value="phone" />
            <el-option label="微信" value="wechat" />
            <el-option label="邮件" value="email" />
            <el-option label="拜访" value="visit" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
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
    </el-card>

    <el-card class="table-card">
      <div class="table-header">
        <h3>跟进记录列表</h3>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增跟进
        </el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="customerName" label="客户名称" min-width="120" />
        <el-table-column prop="contactType" label="跟进方式" width="100">
          <template #default="{ row }">
            <el-tag>{{ getContactTypeText(row.contactType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="nextContactTime" label="下次联系时间" width="160" />
        <el-table-column prop="ownerName" label="负责人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
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
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="handleDialogClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="跟进标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
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
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入内容" />
        </el-form-item>
        
        <el-form-item label="跟进方式" prop="contactType">
          <el-select v-model="form.contactType" placeholder="请选择方式" style="width: 100%">
            <el-option label="电话" value="phone" />
            <el-option label="微信" value="wechat" />
            <el-option label="邮件" value="email" />
            <el-option label="拜访" value="visit" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="下次联系时间" prop="nextContactTime">
          <el-date-picker
            v-model="form.nextContactTime"
            type="datetime"
            placeholder="选择时间"
            style="width: 100%"
          />
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
import { getFollowUpList, createFollowUp, updateFollowUp, deleteFollowUp } from '@/api/follow'
import { getAllCustomers } from '@/api/customer'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增跟进')
const formRef = ref(null)
const customers = ref([])

const searchForm = reactive({ title: '', customerName: '', contactType: '' })

const pagination = reactive({ current: 1, size: 10, total: 0 })

const form = reactive({
  followUpId: null,
  customerId: null,
  title: '',
  content: '',
  contactType: 'phone',
  nextContactTime: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const getContactTypeText = (type) => {
  const texts = { phone: '电话', wechat: '微信', email: '邮件', visit: '拜访' }
  return texts[type] || type
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { current: pagination.current, size: pagination.size, ...searchForm }
    const res = await getFollowUpList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

const loadCustomers = async () => {
  try {
    const res = await getAllCustomers()
    customers.value = res.data || []
  } catch (error) {
    console.error('加载客户失败:', error)
  }
}

const handleSearch = () => { pagination.current = 1; loadData() }
const handleReset = () => { searchForm.title = ''; searchForm.customerName = ''; searchForm.contactType = ''; handleSearch() }
const handleAdd = () => { dialogTitle.value = '新增跟进'; dialogVisible.value = true }
const handleEdit = (row) => { dialogTitle.value = '编辑跟进'; Object.assign(form, row); dialogVisible.value = true }

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除吗？`, '提示', { type: 'warning' })
    await deleteFollowUp(row.followUpId)
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
        if (form.followUpId) {
          await updateFollowUp(form)
          ElMessage.success('修改成功')
        } else {
          await createFollowUp(form)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) { console.error('提交失败:', error) } finally { submitLoading.value = false }
    }
  })
}

const handleDialogClose = () => { formRef.value?.resetFields(); Object.assign(form, { followUpId: null, customerId: null, title: '', content: '', contactType: 'phone', nextContactTime: '' }) }
const handleSizeChange = () => { loadData() }
const handleCurrentChange = () => { loadData() }

onMounted(() => { loadData(); loadCustomers() })
</script>

<style scoped>
.followup-list { padding: 0; }
.search-card { margin-bottom: 20px; border-radius: 8px; }
.table-card { border-radius: 8px; }
.table-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.table-header h3 { margin: 0; font-size: 16px; color: #333; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
