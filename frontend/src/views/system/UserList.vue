<template>
  <div class="user-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
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
        <h3>用户列表</h3>
        <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增用户</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="100" />
        <el-table-column prop="email" label="邮箱" min-width="150" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
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
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="handleDialogClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!form.userId" label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
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
import { getUserList, createUser, updateUser, deleteUser } from '@/api/user'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const formRef = ref(null)

const searchForm = reactive({ username: '', phone: '', status: '' })
const pagination = reactive({ current: 1, size: 10, total: 0 })
const form = reactive({ userId: null, username: '', password: '', nickname: '', email: '', phone: '', status: 1 })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 4, max: 20, message: '长度 4-20 位', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码不少于 6 位', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { current: pagination.current, size: pagination.size, ...searchForm }
    const res = await getUserList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) { console.error('加载失败:', error) } finally { loading.value = false }
}

const handleSearch = () => { pagination.current = 1; loadData() }
const handleReset = () => { searchForm.username = ''; searchForm.phone = ''; searchForm.status = ''; handleSearch() }
const handleAdd = () => { dialogTitle.value = '新增用户'; dialogVisible.value = true }
const handleEdit = (row) => { dialogTitle.value = '编辑用户'; Object.assign(form, row); dialogVisible.value = true }

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户"${row.username}"吗？`, '提示', { type: 'warning' })
    await deleteUser(row.userId)
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
        if (form.userId) { await updateUser(form); ElMessage.success('修改成功') }
        else { await createUser(form); ElMessage.success('创建成功') }
        dialogVisible.value = false
        loadData()
      } catch (error) { console.error('提交失败:', error) } finally { submitLoading.value = false }
    }
  })
}

const handleDialogClose = () => { formRef.value?.resetFields(); Object.assign(form, { userId: null, username: '', password: '', nickname: '', email: '', phone: '', status: 1 }) }
const handleSizeChange = () => { loadData() }
const handleCurrentChange = () => { loadData() }

onMounted(() => { loadData() })
</script>

<style scoped>
.user-list { padding: 0; }
.search-card { margin-bottom: 20px; border-radius: 8px; }
.table-card { border-radius: 8px; }
.table-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.table-header h3 { margin: 0; font-size: 16px; color: #333; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
