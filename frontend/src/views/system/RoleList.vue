<template>
  <div class="role-list">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入名称" clearable />
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
        <h3>角色列表</h3>
        <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增角色</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe border>
        <el-table-column prop="roleName" label="角色名称" min-width="120" />
        <el-table-column prop="roleCode" label="角色编码" min-width="120" />
        <el-table-column prop="description" label="描述" min-width="150" />
        <el-table-column prop="dataScope" label="数据权限" width="120">
          <template #default="{ row }">
            <el-tag>{{ getDataScopeText(row.dataScope) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" @click="handlePermission(row)">权限</el-button>
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
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="数据权限" prop="dataScope">
          <el-select v-model="form.dataScope" style="width: 100%">
            <el-option label="全部数据" :value="1" />
            <el-option label="本部门及以下" :value="2" />
            <el-option label="本部门" :value="3" />
            <el-option label="仅本人" :value="4" />
          </el-select>
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

    <el-dialog v-model="permissionDialogVisible" title="分配权限" width="600px">
      <el-tree
        ref="permissionTreeRef"
        :data="permissionTree"
        :props="{ children: 'children', label: 'name' }"
        show-checkbox
        node-key="id"
      />
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePermissionSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, createRole, updateRole, deleteRole, getRolePermissions, assignRolePermissions } from '@/api/role'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const formRef = ref(null)
const permissionDialogVisible = ref(false)
const permissionTreeRef = ref(null)
const currentRoleId = ref(null)

const searchForm = reactive({ roleName: '', status: '' })
const pagination = reactive({ current: 1, size: 10, total: 0 })
const form = reactive({ roleId: null, roleName: '', roleCode: '', description: '', dataScope: 2, status: 1 })

const permissionTree = ref([
  { id: 1, name: '客户管理', children: [
    { id: 11, name: '查看客户' },
    { id: 12, name: '新增客户' },
    { id: 13, name: '编辑客户' },
    { id: 14, name: '删除客户' }
  ]},
  { id: 2, name: '跟进管理', children: [
    { id: 21, name: '查看跟进' },
    { id: 22, name: '新增跟进' },
    { id: 23, name: '编辑跟进' },
    { id: 24, name: '删除跟进' }
  ]},
  { id: 3, name: '商机管理', children: [
    { id: 31, name: '查看商机' },
    { id: 32, name: '新增商机' },
    { id: 33, name: '编辑商机' },
    { id: 34, name: '删除商机' }
  ]},
  { id: 4, name: '合同管理', children: [
    { id: 41, name: '查看合同' },
    { id: 42, name: '新增合同' },
    { id: 43, name: '编辑合同' },
    { id: 44, name: '删除合同' }
  ]},
  { id: 5, name: '系统管理', children: [
    { id: 51, name: '用户管理' },
    { id: 52, name: '角色管理' },
    { id: 53, name: '权限管理' }
  ]}
])

const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const getDataScopeText = (scope) => {
  const texts = { 1: '全部数据', 2: '本部门及以下', 3: '本部门', 4: '仅本人' }
  return texts[scope] || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { current: pagination.current, size: pagination.size, ...searchForm }
    const res = await getRoleList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) { console.error('加载失败:', error) } finally { loading.value = false }
}

const handleSearch = () => { pagination.current = 1; loadData() }
const handleReset = () => { searchForm.roleName = ''; searchForm.status = ''; handleSearch() }
const handleAdd = () => { dialogTitle.value = '新增角色'; dialogVisible.value = true }
const handleEdit = (row) => { dialogTitle.value = '编辑角色'; Object.assign(form, row); dialogVisible.value = true }

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除角色"${row.roleName}"吗？`, '提示', { type: 'warning' })
    await deleteRole(row.roleId)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) { if (error !== 'cancel') console.error('删除失败:', error) }
}

const handlePermission = async (row) => {
  currentRoleId.value = row.roleId
  try {
    const res = await getRolePermissions(row.roleId)
    const checkedIds = res.data?.map(p => p.id) || []
    permissionTreeRef.value?.setCheckedKeys(checkedIds)
    permissionDialogVisible.value = true
  } catch (error) { console.error('加载权限失败:', error) }
}

const handlePermissionSubmit = async () => {
  try {
    const checkedKeys = permissionTreeRef.value?.getCheckedKeys() || []
    await assignRolePermissions(currentRoleId.value, checkedKeys)
    ElMessage.success('权限分配成功')
    permissionDialogVisible.value = false
  } catch (error) { console.error('分配失败:', error) }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (form.roleId) { await updateRole(form); ElMessage.success('修改成功') }
        else { await createRole(form); ElMessage.success('创建成功') }
        dialogVisible.value = false
        loadData()
      } catch (error) { console.error('提交失败:', error) } finally { submitLoading.value = false }
    }
  })
}

const handleDialogClose = () => { formRef.value?.resetFields(); Object.assign(form, { roleId: null, roleName: '', roleCode: '', description: '', dataScope: 2, status: 1 }) }
const handleSizeChange = () => { loadData() }
const handleCurrentChange = () => { loadData() }

onMounted(() => { loadData() })
</script>

<style scoped>
.role-list { padding: 0; }
.search-card { margin-bottom: 20px; border-radius: 8px; }
.table-card { border-radius: 8px; }
.table-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.table-header h3 { margin: 0; font-size: 16px; color: #333; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
