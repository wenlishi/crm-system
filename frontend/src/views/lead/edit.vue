<template>
  <div class="lead-edit">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>编辑线索</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="线索名称" prop="leadName">
              <el-input v-model="form.leadName" placeholder="请输入线索名称" />
            </el-form-item>

            <el-form-item label="线索类型" prop="leadType">
              <el-radio-group v-model="form.leadType">
                <el-radio :label="1">个人</el-radio>
                <el-radio :label="2">企业</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="线索来源" prop="source">
              <el-select v-model="form.source" placeholder="请选择" style="width: 100%">
                <el-option label="网络营销" value="网络营销" />
                <el-option label="电话咨询" value="电话咨询" />
                <el-option label="客户推荐" value="客户推荐" />
                <el-option label="展会活动" value="展会活动" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>

            <el-form-item label="线索级别" prop="leadLevel">
              <el-select v-model="form.leadLevel" placeholder="请选择" style="width: 100%">
                <el-option label="普通" :value="1" />
                <el-option label="意向" :value="2" />
                <el-option label="高意向" :value="3" />
              </el-select>
            </el-form-item>

            <el-form-item label="联系人" prop="contact">
              <el-input v-model="form.contact" placeholder="请输入联系人" />
            </el-form-item>

            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="公司名称" prop="companyName">
              <el-input v-model="form.companyName" placeholder="请输入公司名称" />
            </el-form-item>

            <el-form-item label="所属行业" prop="industry">
              <el-input v-model="form.industry" placeholder="请输入所属行业" />
            </el-form-item>

            <el-form-item label="公司规模" prop="companyScale">
              <el-select v-model="form.companyScale" placeholder="请选择" style="width: 100%">
                <el-option label="少于 20 人" value="少于 20 人" />
                <el-option label="20-99 人" value="20-99 人" />
                <el-option label="100-499 人" value="100-499 人" />
                <el-option label="500-999 人" value="500-999 人" />
                <el-option label="1000 人以上" value="1000 人以上" />
              </el-select>
            </el-form-item>

            <el-form-item label="预算" prop="budget">
              <el-input-number
                v-model="form.budget"
                :min="0"
                :precision="2"
                :step="10000"
                placeholder="请输入预算"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="下次跟进时间" prop="nextFollowTime">
              <el-date-picker
                v-model="form.nextFollowTime"
                type="datetime"
                placeholder="选择下次跟进时间"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="负责人" prop="ownerId">
              <el-select v-model="form.ownerId" placeholder="请选择" style="width: 100%">
                <el-option label="张三" :value="1" />
                <el-option label="李四" :value="2" />
                <el-option label="王五" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入备注"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">
            保存
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getLeadDetail, updateLead } from '@/api/lead'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const leadId = route.params.id

const form = reactive({
  leadId: null,
  leadName: '',
  leadType: 1,
  source: '',
  leadLevel: 1,
  contact: '',
  phone: '',
  email: '',
  companyName: '',
  industry: '',
  companyScale: '',
  budget: null,
  nextFollowTime: null,
  ownerId: null,
  remark: ''
})

const rules = {
  leadName: [{ required: true, message: '请输入线索名称', trigger: 'blur' }],
  leadType: [{ required: true, message: '请选择线索类型', trigger: 'change' }],
  source: [{ required: true, message: '请选择线索来源', trigger: 'change' }],
  leadLevel: [{ required: true, message: '请选择线索级别', trigger: 'change' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

onMounted(async () => {
  await loadLeadDetail()
})

const loadLeadDetail = async () => {
  loading.value = true
  try {
    const res = await getLeadDetail(leadId)
    Object.assign(form, res.data)
  } catch (error) {
    ElMessage.error('获取线索详情失败')
  } finally {
    loading.value = false
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await updateLead(leadId, form)
      ElMessage.success('保存成功')
      router.push('/leads/list')
    } catch (error) {
      ElMessage.error(error.response?.data?.message || '保存失败')
    } finally {
      loading.value = false
    }
  })
}

const resetForm = () => {
  if (!formRef.value) return
  formRef.value.resetFields()
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.lead-edit {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
