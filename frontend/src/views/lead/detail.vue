<template>
  <div class="lead-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>线索详情</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <el-row :gutter="20">
        <!-- 基本信息 -->
        <el-col :span="12">
          <el-descriptions title="基本信息" :column="1" border>
            <el-descriptions-item label="线索名称">
              {{ lead.leadName }}
            </el-descriptions-item>
            <el-descriptions-item label="线索类型">
              <el-tag>{{ lead.leadType === 1 ? '个人' : '企业' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="线索状态">
              <el-tag :type="getStatusType(lead.leadStatus)">
                {{ getStatusText(lead.leadStatus) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="线索级别">
              <el-tag :type="getLevelType(lead.leadLevel)">
                {{ getLevelText(lead.leadLevel) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="线索来源">
              {{ lead.source || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="所属行业">
              {{ lead.industry || '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-col>

        <!-- 联系信息 -->
        <el-col :span="12">
          <el-descriptions title="联系信息" :column="1" border>
            <el-descriptions-item label="联系人">
              {{ lead.contact || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">
              {{ lead.phone || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ lead.email || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="公司名称">
              {{ lead.companyName || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="公司规模">
              {{ lead.companyScale || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="预算">
              {{ lead.budget ? `¥${lead.budget.toLocaleString()}` : '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>

      <!-- 跟进信息 -->
      <el-divider>跟进信息</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="下次跟进时间">
              <el-tag v-if="lead.nextFollowTime" type="warning">
                {{ formatDate(lead.nextFollowTime) }}
              </el-tag>
              <span v-else>-</span>
            </el-descriptions-item>
            <el-descriptions-item label="负责人">
              {{ lead.ownerName || '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-col>
        <el-col :span="12">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="创建时间">
              {{ formatDate(lead.createTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="更新时间">
              {{ formatDate(lead.updateTime) }}
            </el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>

      <!-- 备注 -->
      <el-divider>备注</el-divider>
      <el-input
        v-model="lead.remark"
        type="textarea"
        :rows="3"
        readonly
      />

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button @click="handleEdit">编辑</el-button>
        <el-button 
          type="success" 
          @click="handleConvert"
          :disabled="lead.leadStatus === 3 || lead.leadStatus === 4"
        >
          转化为客户
        </el-button>
        <el-button 
          type="warning" 
          @click="handleClose"
          :disabled="lead.leadStatus === 3 || lead.leadStatus === 4"
        >
          关闭线索
        </el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </div>
    </el-card>

    <!-- 跟进历史 -->
    <el-card class="follow-up-card">
      <template #header>
        <div class="card-header">
          <span>跟进历史</span>
          <el-button type="primary" @click="showAddFollowUp">添加跟进</el-button>
        </div>
      </template>

      <el-timeline v-if="followUpList.length > 0">
        <el-timeline-item
          v-for="item in followUpList"
          :key="item.followId"
          :timestamp="formatDate(item.createTime)"
          placement="top"
        >
          <el-card>
            <el-descriptions :column="2" size="small">
              <el-descriptions-item label="跟进类型">
                <el-tag size="small">
                  {{ getFollowUpTypeText(item.followType) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="跟进人">
                {{ item.userName }}
              </el-descriptions-item>
            </el-descriptions>
            <p style="margin: 10px 0 5px;">
              <strong>跟进内容：</strong>
            </p>
            <p>{{ item.followContent }}</p>
            <div v-if="item.nextFollowTime" style="margin-top: 10px;">
              <el-tag size="small" type="warning">
                下次跟进：{{ formatDate(item.nextFollowTime) }}
              </el-tag>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>

      <el-empty v-else description="暂无跟进记录" />
    </el-card>

    <!-- 添加跟进弹窗 -->
    <el-dialog
      v-model="addFollowUpVisible"
      title="添加跟进记录"
      width="600px"
    >
      <el-form :model="followUpForm" label-width="100px">
        <el-form-item label="跟进类型" required>
          <el-select v-model="followUpForm.followType" placeholder="请选择">
            <el-option label="电话" :value="1" />
            <el-option label="微信" :value="2" />
            <el-option label="邮件" :value="3" />
            <el-option label="面谈" :value="4" />
            <el-option label="其他" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进内容" required>
          <el-input
            v-model="followUpForm.followContent"
            type="textarea"
            :rows="4"
            placeholder="请输入跟进内容"
          />
        </el-form-item>
        <el-form-item label="下次跟进时间">
          <el-date-picker
            v-model="followUpForm.nextFollowTime"
            type="datetime"
            placeholder="选择下次跟进时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="下次跟进计划">
          <el-input
            v-model="followUpForm.nextFollowPlan"
            type="textarea"
            :rows="2"
            placeholder="请输入下次跟进计划"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="addFollowUpVisible = false">取消</el-button>
        <el-button type="primary" @click="submitFollowUp" :loading="submitting">
          提交
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getLeadDetail,
  deleteLead,
  convertToCustomer,
  closeLead,
  getLeadFollowUpHistory,
  addLeadFollowUp
} from '@/api/lead'

const route = useRoute()
const router = useRouter()

const leadId = route.params.id
const loading = ref(false)
const submitting = ref(false)

const lead = reactive({
  leadId: null,
  leadName: '',
  leadType: 1,
  leadStatus: 1,
  leadLevel: 1,
  source: '',
  industry: '',
  contact: '',
  phone: '',
  email: '',
  companyName: '',
  companyScale: '',
  budget: null,
  nextFollowTime: null,
  ownerName: '',
  remark: '',
  createTime: null,
  updateTime: null
})

const followUpList = ref([])
const addFollowUpVisible = ref(false)
const followUpForm = reactive({
  leadId: null,
  followType: 1,
  followContent: '',
  nextFollowTime: null,
  nextFollowPlan: ''
})

onMounted(() => {
  loadLeadDetail()
  loadFollowUpHistory()
})

const loadLeadDetail = async () => {
  loading.value = true
  try {
    const res = await getLeadDetail(leadId)
    Object.assign(lead, res.data)
  } catch (error) {
    ElMessage.error('获取线索详情失败')
  } finally {
    loading.value = false
  }
}

const loadFollowUpHistory = async () => {
  try {
    // TODO: 后端 API 待实现，暂时使用空数组
    // const res = await getLeadFollowUpHistory(leadId)
    // followUpList.value = res.data || []
    followUpList.value = [] // 暂时使用空数组
  } catch (error) {
    console.error('获取跟进历史失败', error)
    followUpList.value = [] // 失败时使用空数组
  }
}

const showAddFollowUp = () => {
  followUpForm.leadId = leadId
  followUpForm.followType = 1
  followUpForm.followContent = ''
  followUpForm.nextFollowTime = null
  followUpForm.nextFollowPlan = ''
  addFollowUpVisible.value = true
}

const submitFollowUp = async () => {
  if (!followUpForm.followContent) {
    ElMessage.warning('请输入跟进内容')
    return
  }

  submitting.value = true
  try {
    await addLeadFollowUp(followUpForm)
    ElMessage.success('添加成功')
    addFollowUpVisible.value = false
    loadFollowUpHistory()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '添加失败')
  } finally {
    submitting.value = false
  }
}

const handleEdit = () => {
  router.push(`/leads/edit/${leadId}`)
}

const handleConvert = () => {
  router.push(`/leads/convert/${leadId}`)
}

const handleClose = async () => {
  try {
    const { value } = await ElMessageBox.prompt('请输入关闭原因', '关闭线索', {
      type: 'warning',
      inputPattern: /.+/,
      inputErrorMessage: '请输入关闭原因'
    })
    await closeLead(leadId, value)
    ElMessage.success('关闭成功')
    router.push('/leads/list')
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('关闭失败')
    }
  }
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除该线索吗？', '提示', {
      type: 'warning'
    })
    await deleteLead(leadId)
    ElMessage.success('删除成功')
    router.push('/leads/list')
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const goBack = () => {
  router.back()
}

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

const getStatusType = (status) => {
  const types = { 1: 'info', 2: 'warning', 3: 'success', 4: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 1: '待联系', 2: '联系中', 3: '已转化', 4: '已关闭' }
  return texts[status] || '未知'
}

const getLevelType = (level) => {
  const types = { 1: '', 2: 'warning', 3: 'danger' }
  return types[level] || ''
}

const getLevelText = (level) => {
  const texts = { 1: '普通', 2: '意向', 3: '高意向' }
  return texts[level] || '未知'
}

const getFollowUpTypeText = (type) => {
  const texts = { 1: '电话', 2: '微信', 3: '邮件', 4: '面谈', 5: '其他' }
  return texts[type] || '未知'
}
</script>

<style scoped>
.lead-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-buttons {
  margin-top: 20px;
  text-align: center;
}

.follow-up-card {
  margin-top: 20px;
}
</style>
