<template>
  <div class="customer-detail">
    <el-card class="detail-card">
      <template #header>
        <div class="card-header">
          <el-button @click="handleBack">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <h3>客户详情</h3>
          <el-button type="primary" @click="handleEdit">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="客户名称">{{ customer.customerName }}</el-descriptions-item>
        <el-descriptions-item label="客户级别">
          <el-tag :type="getLevelType(customer.level)">{{ customer.level }}类</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="联系人">{{ customer.contact }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ customer.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ customer.email }}</el-descriptions-item>
        <el-descriptions-item label="客户状态">
          <el-tag :type="getStatusType(customer.status)">
            {{ getStatusText(customer.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="客户来源">{{ getSourceText(customer.source) }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ customer.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ customer.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ customer.updateTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ customer.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 跟进记录 -->
    <el-card class="followup-card">
      <template #header>
        <div class="card-header">
          <h3>跟进记录</h3>
          <el-button type="primary" @click="handleAddFollowUp">
            <el-icon><Plus /></el-icon>
            新增跟进
          </el-button>
        </div>
      </template>

      <el-timeline>
        <el-timeline-item
          v-for="item in followUps"
          :key="item.followUpId"
          :timestamp="item.createTime"
          placement="top"
        >
          <el-card>
            <h4>{{ item.title }}</h4>
            <p>{{ item.content }}</p>
            <p class="next-time">下次联系时间：{{ item.nextContactTime || '未设置' }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>

      <el-empty v-if="followUps.length === 0" description="暂无跟进记录" />
    </el-card>

    <!-- 新增跟进对话框 -->
    <el-dialog v-model="followUpDialogVisible" title="新增跟进记录" width="600px">
      <el-form
        ref="followUpFormRef"
        :model="followUpForm"
        :rules="followUpRules"
        label-width="100px"
      >
        <el-form-item label="跟进标题" prop="title">
          <el-input v-model="followUpForm.title" placeholder="请输入跟进标题" />
        </el-form-item>
        
        <el-form-item label="跟进内容" prop="content">
          <el-input
            v-model="followUpForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入跟进内容"
          />
        </el-form-item>
        
        <el-form-item label="下次联系时间" prop="nextContactTime">
          <el-date-picker
            v-model="followUpForm.nextContactTime"
            type="datetime"
            placeholder="选择下次联系时间"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="followUpDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitFollowUp">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCustomerDetail } from '@/api/customer'
import { getFollowUpByCustomerId, createFollowUp } from '@/api/follow'

const route = useRoute()
const router = useRouter()

const customer = ref({})
const followUps = ref([])
const followUpDialogVisible = ref(false)
const followUpFormRef = ref(null)

const followUpForm = reactive({
  customerId: null,
  title: '',
  content: '',
  nextContactTime: ''
})

const followUpRules = {
  title: [{ required: true, message: '请输入跟进标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入跟进内容', trigger: 'blur' }]
}

const getLevelType = (level) => {
  const types = { A: 'danger', B: 'primary', C: 'success', D: 'info' }
  return types[level] || 'info'
}

const getStatusType = (status) => {
  const types = { 0: 'primary', 1: 'success', 2: 'info' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '跟进中', 1: '已成交', 2: '已流失' }
  return texts[status] || '未知'
}

const getSourceText = (source) => {
  const texts = {
    online: '线上咨询',
    offline: '线下活动',
    referral: '客户介绍',
    other: '其他'
  }
  return texts[source] || source
}

const loadCustomerDetail = async () => {
  try {
    const res = await getCustomerDetail(route.params.id)
    customer.value = res.data
  } catch (error) {
    console.error('加载客户详情失败:', error)
  }
}

const loadFollowUps = async () => {
  try {
    const res = await getFollowUpByCustomerId(route.params.id)
    followUps.value = res.data || []
  } catch (error) {
    console.error('加载跟进记录失败:', error)
  }
}

const handleBack = () => {
  router.back()
}

const handleEdit = () => {
  router.push(`/customers?id=${customer.value.customerId}&action=edit`)
}

const handleAddFollowUp = () => {
  followUpForm.customerId = route.params.id
  followUpDialogVisible.value = true
}

const handleSubmitFollowUp = async () => {
  if (!followUpFormRef.value) return
  
  await followUpFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await createFollowUp(followUpForm)
        ElMessage.success('添加成功')
        followUpDialogVisible.value = false
        loadFollowUps()
      } catch (error) {
        console.error('添加失败:', error)
      }
    }
  })
}

onMounted(() => {
  loadCustomerDetail()
  loadFollowUps()
})
</script>

<style scoped>
.customer-detail {
  padding: 0;
}

.detail-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.followup-card {
  border-radius: 8px;
}

.next-time {
  margin-top: 10px;
  color: #909399;
  font-size: 13px;
}
</style>
