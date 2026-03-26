<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- 左侧视觉区 -->
      <div class="visual-side">
        <!-- 背景装饰 -->
        <div class="bg-pattern"></div>
        <div class="bg-gradient"></div>
        
        <div class="visual-content">
          <div class="brand-logo">
            <div class="logo-glow"></div>
            <div class="logo-box">
              <el-icon><DataLine /></el-icon>
            </div>
          </div>
          <h1 class="brand-title">CRM 客户管理系统</h1>
          <p class="brand-desc">让客户关系管理更简单高效</p>
          
          <div class="features-list">
            <div class="feature-item">
              <div class="feature-icon-box">
                <el-icon><User /></el-icon>
              </div>
              <div class="feature-text">
                <div class="feature-name">客户管理</div>
                <div class="feature-desc">360° 客户视图</div>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon-box">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="feature-text">
                <div class="feature-name">销售跟进</div>
                <div class="feature-desc">全流程追踪</div>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon-box">
                <el-icon><DataAnalysis /></el-icon>
              </div>
              <div class="feature-text">
                <div class="feature-name">数据分析</div>
                <div class="feature-desc">智能决策支持</div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 右下角装饰 -->
        <div class="corner-decoration">
          <div class="dot dot-1"></div>
          <div class="dot dot-2"></div>
          <div class="dot dot-3"></div>
        </div>
      </div>

      <!-- 右侧登录区 -->
      <div class="login-side">
        <div class="login-inner">
          <div class="login-header">
            <h2 class="login-title">欢迎登录</h2>
            <p class="login-subtitle">使用您的账号继续</p>
          </div>

          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
          >
            <el-form-item prop="username">
              <div class="input-wrapper">
                <el-input
                  v-model="loginForm.username"
                  placeholder="请输入用户名"
                  size="large"
                  clearable
                >
                  <template #prefix>
                    <el-icon class="input-icon"><User /></el-icon>
                  </template>
                </el-input>
              </div>
            </el-form-item>

            <el-form-item prop="password">
              <div class="input-wrapper">
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="请输入密码"
                  size="large"
                  show-password
                >
                  <template #prefix>
                    <el-icon class="input-icon"><Lock /></el-icon>
                  </template>
                </el-input>
              </div>
            </el-form-item>

            <div class="form-row">
              <el-checkbox v-model="loginForm.remember" class="checkbox-custom">
                记住我
              </el-checkbox>
              <el-link type="primary" :underline="false" class="link-forgot">
                忘记密码？
              </el-link>
            </div>

            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="login-button"
              @click="handleLogin"
            >
              <span v-if="!loading">登录</span>
              <span v-else>
                <span class="loading-spinner"></span>
                登录中...
              </span>
            </el-button>
          </el-form>

          <div class="demo-section">
            <div class="demo-label">演示账号</div>
            <div class="demo-accounts">
              <div class="demo-item">
                <span class="demo-label-small">账号</span>
                <code class="demo-code">admin</code>
              </div>
              <div class="demo-item">
                <span class="demo-label-small">密码</span>
                <code class="demo-code">admin123</code>
              </div>
            </div>
          </div>
        </div>

        <div class="login-footer">
          <p>© 2026 CRM 客户管理系统</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { DataLine, User, Lock, TrendCharts, DataAnalysis } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: 'admin',
  password: 'admin123',
  remember: false
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度在 4 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login({
          username: loginForm.username,
          password: loginForm.password
        })
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 24px;
}

.login-wrapper {
  width: 100%;
  max-width: 1100px;
  height: 650px;
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  display: flex;
  box-shadow: 
    0 2px 8px rgba(0, 0, 0, 0.04),
    0 16px 48px rgba(0, 0, 0, 0.08);
}

/* ========== 左侧视觉区 ========== */
.visual-side {
  flex: 1;
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a87 100%);
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 背景纹理 */
.bg-pattern {
  position: absolute;
  inset: 0;
  background-image: 
    linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px);
  background-size: 40px 40px;
  opacity: 0.5;
  pointer-events: none;
}

/* 渐变光效 */
.bg-gradient {
  position: absolute;
  top: -50%;
  right: -20%;
  width: 80%;
  height: 200%;
  background: radial-gradient(ellipse, rgba(255,255,255,0.15) 0%, transparent 70%);
  animation: shimmer 8s infinite ease-in-out;
  pointer-events: none;
}

@keyframes shimmer {
  0%, 100% { transform: translateY(0) scale(1); opacity: 0.5; }
  50% { transform: translateY(-10%) scale(1.05); opacity: 0.8; }
}

.visual-content {
  position: relative;
  z-index: 2;
}

.brand-logo {
  margin-bottom: 24px;
  position: relative;
}

.logo-glow {
  position: absolute;
  width: 80px;
  height: 80px;
  background: radial-gradient(circle, rgba(255,255,255,0.2) 0%, transparent 70%);
  border-radius: 50%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: pulse 3s infinite ease-in-out;
  pointer-events: none;
}

@keyframes pulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.5; }
  50% { transform: translate(-50%, -50%) scale(1.15); opacity: 0.8; }
}

.logo-box {
  width: 56px;
  height: 56px;
  background: rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(12px);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.25);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2), inset 0 2px 8px rgba(255, 255, 255, 0.2);
  transition: transform 0.3s ease;
}

.visual-side:hover .logo-box {
  transform: scale(1.05);
}

.logo-box .el-icon {
  font-size: 28px;
  color: #fff;
}

.brand-title {
  font-size: 26px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 8px 0;
  line-height: 1.3;
}

.brand-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  margin: 0 0 32px 0;
  line-height: 1.5;
}

.features-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  overflow: hidden;
}

.feature-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 3px;
  height: 100%;
  background: linear-gradient(180deg, rgba(255,255,255,0.4), rgba(255,255,255,0.1));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.14);
  transform: translateX(6px);
  border-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.feature-item:hover::before {
  opacity: 1;
}

.feature-icon-box {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.feature-icon-box .el-icon {
  font-size: 20px;
  color: #fff;
}

.feature-text {
  flex: 1;
}

.feature-name {
  font-size: 14px;
  color: #fff;
  font-weight: 500;
  margin-bottom: 2px;
}

.feature-desc {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

/* ========== 右侧登录区 ========== */
.login-side {
  width: 480px;
  padding: 48px 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.login-inner {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-header {
  margin-bottom: 32px;
}

.login-title {
  font-size: 26px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 6px 0;
}

.login-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-wrapper {
  width: 100%;
}

:deep(.el-input__wrapper) {
  background: #f8f9fa !important;
  border: 1px solid #e9ecef !important;
  border-radius: 10px !important;
  padding: 0 14px !important;
  height: 46px !important;
  transition: all 0.3s ease !important;
  box-shadow: none !important;
}

:deep(.el-input__wrapper:hover) {
  background: #fff !important;
  border-color: #1e3a5f !important;
}

:deep(.el-input__wrapper.is-focus) {
  background: #fff !important;
  border-color: #1e3a5f !important;
  box-shadow: 0 0 0 3px rgba(30, 58, 95, 0.1) !important;
}

:deep(.el-input__inner) {
  color: #1a1a1a !important;
  font-size: 15px !important;
}

:deep(.el-input__inner::placeholder) {
  color: #999 !important;
}

.input-icon {
  color: #999;
  font-size: 18px;
  transition: color 0.3s ease;
}

:deep(.el-input__wrapper:hover .input-icon),
:deep(.el-input__wrapper.is-focus .input-icon) {
  color: #1e3a5f;
}

.form-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 4px;
}

.checkbox-custom {
  font-size: 14px;
  color: #666;
}

:deep(.checkbox-custom .el-checkbox__label) {
  color: #666;
}

:deep(.checkbox-custom .el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #1e3a5f;
  border-color: #1e3a5f;
}

.link-forgot {
  font-size: 14px;
  color: #1e3a5f;
}

.link-forgot:hover {
  color: #2d5a87;
}

.login-button {
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #1e3a5f 0%, #2d5a87 50%, #3a6fa5 100%);
  border: none;
  margin-top: 8px;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  overflow: hidden;
}

.login-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: left 0.6s ease;
}

.login-button:hover::before {
  left: 100%;
}

.login-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 32px rgba(30, 58, 95, 0.4), 0 4px 12px rgba(30, 58, 95, 0.2);
}

.loading-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-right: 8px;
  vertical-align: middle;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.demo-section {
  margin-top: 24px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 10px;
  border: 1px solid #e9ecef;
}

.demo-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 10px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.demo-accounts {
  display: flex;
  gap: 12px;
}

.demo-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.demo-label-small {
  font-size: 11px;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.demo-code {
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', monospace;
  font-size: 13px;
  color: #1e3a5f;
  background: rgba(30, 58, 95, 0.08);
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid rgba(30, 58, 95, 0.15);
}

.login-footer {
  text-align: center;
}

.login-footer p {
  font-size: 13px;
  color: #999;
  margin: 0;
}

/* 右下角装饰 */
.corner-decoration {
  position: absolute;
  bottom: 24px;
  right: 24px;
  display: flex;
  gap: 8px;
  align-items: flex-end;
  opacity: 0.4;
}

.dot {
  width: 6px;
  height: 6px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: dotPulse 2s infinite ease-in-out;
}

.dot-1 {
  animation-delay: 0s;
}

.dot-2 {
  animation-delay: 0.2s;
}

.dot-3 {
  animation-delay: 0.4s;
}

@keyframes dotPulse {
  0%, 100% { transform: scale(1); opacity: 0.4; }
  50% { transform: scale(1.3); opacity: 0.8; }
}

/* ========== 响应式 ========== */
@media (max-width: 900px) {
  .login-wrapper {
    flex-direction: column;
    height: auto;
    max-width: 480px;
  }

  .visual-side {
    padding: 32px 24px;
    min-height: 280px;
  }

  .brand-title {
    font-size: 22px;
  }

  .login-side {
    width: 100%;
    padding: 32px 24px;
  }
}
</style>
