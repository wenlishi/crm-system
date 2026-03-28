<template>
  <div class="profile-container">
    <el-card class="profile-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="header-title">个人中心</span>
        </div>
      </template>

      <el-form :model="userForm" label-width="100px" label-position="left" class="profile-form">
        <!-- 头像区域 -->
        <div class="avatar-section">
          <el-avatar :size="100" :src="userForm.avatar || defaultAvatar" class="profile-avatar" />
          <div class="avatar-actions">
            <el-button type="primary" size="small" @click="handleUpdateAvatar" :loading="uploading">
              <el-icon><Upload /></el-icon>
              更换头像
            </el-button>
            <el-text type="info" size="small">支持 JPG/PNG/GIF 格式，最大 2MB</el-text>
          </div>
        </div>
        
        <!-- 隐藏的文件输入 -->
        <input
          ref="uploadRef"
          type="file"
          accept="image/*"
          style="display: none"
          @change="handleFileChange"
        />
        
        <!-- 头像裁剪对话框 -->
        <el-dialog
          v-model="avatarDialogVisible"
          title="裁剪头像"
          width="500px"
          :close-on-click-modal="false"
          @close="closeAvatarDialog"
        >
          <div class="crop-container">
            <img ref="cropper" :src="previewUrl" class="avatar-crop-image" style="max-width: 100%" />
          </div>
          <template #footer>
            <el-button @click="closeAvatarDialog">取消</el-button>
            <el-button type="primary" @click="uploadAvatar" :loading="uploading">
              确认上传
            </el-button>
          </template>
        </el-dialog>

        <!-- 基本信息 -->
        <el-divider content-position="left">基本信息</el-divider>
        
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" disabled class="disabled-input" />
        </el-form-item>

        <el-form-item label="昵称">
          <el-input v-model="userForm.nickname" placeholder="请输入昵称" maxlength="20" show-word-limit />
        </el-form-item>

        <el-form-item label="手机号">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="性别">
          <el-radio-group v-model="userForm.gender">
            <el-radio :label="0">保密</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="部门">
          <el-select v-model="userForm.deptId" placeholder="请选择部门" clearable style="width: 100%">
            <el-option
              v-for="dept in deptList"
              :key="dept.deptId"
              :label="dept.deptName"
              :value="dept.deptId"
            />
          </el-select>
        </el-form-item>

        <!-- 账号安全 -->
        <el-divider content-position="left">账号安全</el-divider>

        <el-form-item label="当前密码" v-if="showPasswordForm">
          <el-input 
            v-model="passwordForm.currentPassword" 
            type="password" 
            placeholder="请输入当前密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="新密码" v-if="showPasswordForm">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" v-if="showPasswordForm">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSaveProfile" :loading="saving">
            保存修改
          </el-button>
          <el-button @click="togglePasswordForm" v-if="!showPasswordForm">
            修改密码
          </el-button>
          <el-button @click="resetPasswordForm" v-else>
            取消修改密码
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload, Camera } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { Cropper } from 'cropperjs'

const userStore = useUserStore()

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const saving = ref(false)
const showPasswordForm = ref(false)
const uploading = ref(false)
const avatarDialogVisible = ref(false)
const cropper = ref(null)
const cropperInstance = ref(null)

const userForm = reactive({
  userId: null,
  username: '',
  nickname: '',
  phone: '',
  email: '',
  gender: 0,
  deptId: null,
  avatar: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const deptList = ref([])

// 头像上传相关
const uploadRef = ref(null)
const previewUrl = ref('')

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const user = userStore.user
    if (user) {
      userForm.userId = user.userId || user.id
      userForm.username = user.username || ''
      userForm.nickname = user.nickname || ''
      userForm.phone = user.phone || ''
      userForm.email = user.email || ''
      userForm.gender = user.gender ?? 0
      userForm.deptId = user.deptId
      userForm.avatar = user.avatar || ''
    }
    
    // 加载部门列表
    await loadDeptList()
  } catch (error) {
    console.error('加载用户信息失败:', error)
    ElMessage.error('加载用户信息失败')
  }
}

// 加载部门列表
const loadDeptList = async () => {
  try {
    // TODO: 调用后端 API 获取部门列表
    // const res = await axios.get('/api/system/dept/list')
    // deptList.value = res.data.data || []
    
    // 临时模拟数据
    deptList.value = [
      { deptId: 1, deptName: '技术部' },
      { deptId: 2, deptName: '销售部' },
      { deptId: 3, deptName: '市场部' },
      { deptId: 4, deptName: '客服部' }
    ]
  } catch (error) {
    console.error('加载部门列表失败:', error)
  }
}

// 保存个人信息
const handleSaveProfile = async () => {
  if (!userForm.nickname.trim()) {
    ElMessage.warning('请输入昵称')
    return
  }

  if (userForm.phone && !/^1[3-9]\d{9}$/.test(userForm.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }

  if (userForm.email && !/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(userForm.email)) {
    ElMessage.warning('请输入正确的邮箱地址')
    return
  }

  // 如果需要修改密码
  if (showPasswordForm.value) {
    if (!passwordForm.currentPassword) {
      ElMessage.warning('请输入当前密码')
      return
    }
    if (!passwordForm.newPassword) {
      ElMessage.warning('请输入新密码')
      return
    }
    if (passwordForm.newPassword.length < 6) {
      ElMessage.warning('密码长度不能少于 6 位')
      return
    }
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      ElMessage.error('两次输入的密码不一致')
      return
    }
  }

  try {
    saving.value = true

    // TODO: 调用后端 API 保存
    // await axios.put('/api/system/user/profile', userForm)
    
    // 模拟保存
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 更新 store 中的用户信息
    userStore.setUser({
      ...userStore.user,
      nickname: userForm.nickname,
      phone: userForm.phone,
      email: userForm.email,
      gender: userForm.gender,
      deptId: userForm.deptId
    })

    ElMessage.success('保存成功')
    
    // 如果修改了密码，清空密码表单
    if (showPasswordForm.value) {
      resetPasswordForm()
      showPasswordForm.value = false
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

// 更换头像 - 触发文件选择
const handleUpdateAvatar = () => {
  uploadRef.value?.click()
}

// 文件选择后的处理
const handleFileChange = (event) => {
  const file = event.target.files[0]
  
  if (!file) {
    return
  }
  
  console.log('选择的文件:', file.name, file.type, file.size)
  
  // 验证文件类型
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!validTypes.includes(file.type)) {
    ElMessage.error('只能上传 JPG/PNG/GIF/WebP 格式的图片')
    // 清空 input，允许重新选择
    event.target.value = ''
    return
  }
  
  // 验证文件大小（最大 2MB）
  const maxSize = 2 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('头像大小不能超过 2MB')
    event.target.value = ''
    return
  }
  
  // 显示预览
  previewUrl.value = URL.createObjectURL(file)
  avatarDialogVisible.value = true
  
  // 初始化裁剪器
  setTimeout(() => {
    const image = document.querySelector('.avatar-crop-image')
    if (image) {
      if (cropperInstance.value) {
        cropperInstance.value.destroy()
      }
      cropperInstance.value = new Cropper(image, {
        aspectRatio: 1,
        viewMode: 1,
        dragMode: 'move',
        autoCropArea: 0.8,
        responsive: true,
        background: false,
      })
    }
  }, 100)
  
  // 清空 input，允许重新选择同一文件
  event.target.value = ''
}

// 上传头像
const uploadAvatar = async () => {
  console.log('开始上传头像，cropperInstance:', cropperInstance.value)
  
  if (!cropperInstance.value) {
    ElMessage.warning('请先选择图片')
    return
  }
  
  try {
    uploading.value = true
    
    // 获取裁剪后的 canvas
    const canvas = cropperInstance.value.getCroppedCanvas({
      width: 200,
      height: 200,
      imageSmoothingEnabled: true,
      imageSmoothingQuality: 'high',
    })
    
    console.log('获取到 canvas:', canvas)
    
    if (!canvas) {
      ElMessage.error('裁剪失败，请重试')
      return
    }
    
    // 转换为 Blob
    const blob = await new Promise(resolve => canvas.toBlob(resolve, 'image/jpeg', 0.9))
    
    console.log('转换为 Blob:', blob)
    
    if (!blob) {
      ElMessage.error('图片处理失败，请重试')
      return
    }
    
    // 创建 FormData
    const formData = new FormData()
    formData.append('file', blob, 'avatar.jpg')
    formData.append('bizType', 'avatar')
    formData.append('bizId', userForm.userId)
    
    console.log('准备上传，userId:', userForm.userId)
    console.log('FormData:', formData)
    
    // 调用后端上传接口（使用封装的 request，自动带 token）
    const response = await request.post('/files/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    
    console.log('上传成功，响应:', response)
    
    // request 拦截器已经处理了 code !== 200 的情况
    const fileUrl = response.data.fileUrl
    const fileId = response.data.fileId
    
    // 更新用户头像
    userForm.avatar = fileUrl
    
    // 更新 store
    userStore.setUser({
      ...userStore.user,
      avatar: fileUrl
    })
    
    ElMessage.success('头像更新成功')
    avatarDialogVisible.value = false
    
    // 清理
    if (previewUrl.value) {
      URL.revokeObjectURL(previewUrl.value)
      previewUrl.value = ''
    }
  } catch (error) {
    console.error('上传失败:', error)
    // 错误已经在拦截器中显示
  } finally {
    uploading.value = false
  }
}

// 关闭裁剪对话框
const closeAvatarDialog = () => {
  avatarDialogVisible.value = false
  if (cropperInstance.value) {
    cropperInstance.value.destroy()
    cropperInstance.value = null
  }
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value)
    previewUrl.value = ''
  }
}

// 显示/隐藏密码表单
const togglePasswordForm = () => {
  showPasswordForm.value = true
}

// 重置密码表单
const resetPasswordForm = () => {
  showPasswordForm.value = false
  passwordForm.currentPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 24px;
  max-width: 800px;
  margin: 0 auto;
}

.profile-card {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-card__header) {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  padding: 18px 24px;
  border-bottom: none;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.profile-form {
  padding: 24px 24px 8px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 32px;
  padding: 24px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-radius: 12px;
}

.profile-avatar {
  border: 4px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 裁剪对话框 */
.crop-container {
  width: 100%;
  height: 400px;
  background: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-crop-image {
  max-width: 100%;
  max-height: 100%;
}

/* Cropper.js 样式 */
:deep(.cropper-container) {
  background: #f5f7fa;
}

:deep(.cropper-view-box) {
  outline: 2px solid #409EFF;
  border-radius: 50%;
}

:deep(.cropper-line) {
  background: #409EFF;
}

:deep(.cropper-point) {
  background: #409EFF;
}

:deep(.el-divider) {
  margin: 24px 0 16px;
}

:deep(.el-divider__text) {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

.disabled-input :deep(.el-input__wrapper) {
  background: #f5f7fa;
  cursor: not-allowed;
}

:deep(.el-input__wrapper),
:deep(.el-select .el-input__wrapper),
:deep(.el-textarea__wrapper) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover),
:deep(.el-select .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #409EFF inset;
}

:deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #409EFF;
  font-weight: 500;
}

:deep(.el-button--primary) {
  border-radius: 8px;
  padding: 10px 24px;
  font-weight: 500;
}

:deep(.el-button) {
  border-radius: 8px;
}

/* 响应式 */
@media (max-width: 768px) {
  .profile-container {
    padding: 16px;
  }
  
  .avatar-section {
    flex-direction: column;
    text-align: center;
  }
  
  .profile-form {
    padding: 16px 16px 8px;
  }
}
</style>
