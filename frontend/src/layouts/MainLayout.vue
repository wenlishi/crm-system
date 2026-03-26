<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '72px' : '240px'" class="sidebar">
      <div class="logo-wrapper">
        <div class="logo">
          <div class="logo-icon">
            <el-icon><DataLine /></el-icon>
          </div>
          <span v-if="!isCollapse" class="logo-text">CRM 系统</span>
        </div>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        class="sidebar-menu"
        router
      >
        <el-menu-item
          v-for="route in menuRoutes"
          :key="route.path"
          :index="'/' + route.path"
          class="menu-item"
        >
          <div class="menu-item-content">
            <div class="menu-icon-wrapper">
              <el-icon class="menu-icon"><component :is="route.meta.icon" /></el-icon>
            </div>
            <span v-if="!isCollapse" class="menu-title">{{ route.meta.title }}</span>
          </div>
          <div v-if="!isCollapse" class="menu-active-indicator"></div>
        </el-menu-item>
      </el-menu>
      
      <div class="sidebar-footer">
        <div class="version-info" v-if="!isCollapse">
          <span class="version-label">版本</span>
          <span class="version-number">v1.0.0</span>
        </div>
      </div>
    </el-aside>

    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <div class="collapse-btn-wrapper" @click="toggleCollapse">
            <el-icon class="collapse-btn"><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
          </div>
          <div class="breadcrumb-wrapper">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="currentRoute">{{ currentRoute.meta.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
        </div>
        
        <div class="header-right">
          <div class="header-action">
            <el-badge :value="3" :max="99" class="action-item">
              <el-icon><Bell /></el-icon>
            </el-badge>
          </div>
          <div class="divider"></div>
          <el-dropdown @command="handleCommand" class="user-dropdown">
            <div class="user-info">
              <el-avatar :size="36" :src="userStore.avatar || defaultAvatar" class="user-avatar" />
              <div class="user-details" v-if="!isCollapse">
                <span class="user-name">管理员</span>
                <span class="user-role">超级管理员</span>
              </div>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="user-dropdown-menu">
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  <span>系统设置</span>
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessageBox } from 'element-plus'
import { DataLine, Bell, User, Setting, SwitchButton, ArrowDown, Fold, Expand } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const menuRoutes = computed(() => {
  return router.options.routes[1]?.children?.filter(r => !r.meta?.hidden) || []
})

const activeMenu = computed(() => {
  return route.path
})

const currentRoute = computed(() => {
  return menuRoutes.value.find(r => r.path === route.path.substring(1))
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await userStore.logout()
    } catch {
      // 取消退出
    }
  } else if (command === 'profile') {
    console.log('个人中心')
  } else if (command === 'settings') {
    console.log('系统设置')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  background: #f5f7fa;
}

/* ========== 侧边栏 ========== */
.sidebar {
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  transition: width 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.15);
}

.logo-wrapper {
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo {
  display: flex;
  align-items: center;
  gap: 14px;
}

.logo-icon {
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, #409EFF 0%, #69c0ff 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.35);
}

.logo-icon .el-icon {
  font-size: 24px;
  color: #fff;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.5px;
  background: linear-gradient(135deg, #fff, #e6f7ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* ========== 菜单 ========== */
.sidebar-menu {
  flex: 1;
  border-right: none;
  background: transparent;
  padding: 12px 12px 0;
  overflow-y: auto;
}

.sidebar-menu::-webkit-scrollbar {
  width: 4px;
}

.sidebar-menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
}

.sidebar-menu::-webkit-scrollbar-track {
  background: transparent;
}

.menu-item {
  margin-bottom: 8px;
  border-radius: 10px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateX(4px);
}

.menu-item.is-active {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.2), rgba(105, 192, 255, 0.15));
}

.menu-item-content {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 0 4px;
}

.menu-icon-wrapper {
  width: 40px;
  height: 40px;
  min-width: 40px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.menu-item:hover .menu-icon-wrapper {
  background: rgba(255, 255, 255, 0.12);
}

.menu-item.is-active .menu-icon-wrapper {
  background: linear-gradient(135deg, #409EFF, #69c0ff);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.menu-icon {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.75);
  transition: all 0.3s ease;
}

.menu-item:hover .menu-icon {
  color: rgba(255, 255, 255, 0.95);
}

.menu-item.is-active .menu-icon {
  color: #fff;
}

.menu-title {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  font-weight: 500;
  letter-spacing: 0.3px;
  transition: all 0.3s ease;
}

.menu-item:hover .menu-title {
  color: #fff;
}

.menu-item.is-active .menu-title {
  color: #fff;
  font-weight: 600;
}

.menu-active-indicator {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: linear-gradient(180deg, #409EFF, #69c0ff);
  border-radius: 4px 0 0 4px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.menu-item.is-active .menu-active-indicator {
  opacity: 1;
}

/* ========== 侧边栏底部 ========== */
.sidebar-footer {
  padding: 16px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.version-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
}

.version-label {
  color: rgba(255, 255, 255, 0.5);
}

.version-number {
  color: rgba(255, 255, 255, 0.7);
  background: rgba(255, 255, 255, 0.08);
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 500;
}

/* ========== 顶部导航 ========== */
.header {
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-btn-wrapper {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.collapse-btn-wrapper:hover {
  background: #f5f7fa;
}

.collapse-btn {
  font-size: 20px;
  color: #606266;
  transition: color 0.3s ease;
}

.collapse-btn-wrapper:hover .collapse-btn {
  color: #409EFF;
}

.breadcrumb-wrapper {
  padding-left: 8px;
}

:deep(.el-breadcrumb__item) {
  font-size: 14px;
}

:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #606266;
  font-weight: 500;
}

:deep(.el-breadcrumb__inner) {
  color: #909399;
  transition: color 0.3s ease;
}

:deep(.el-breadcrumb__inner:hover) {
  color: #409EFF;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-action {
  display: flex;
  align-items: center;
}

.action-item {
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-item .el-icon {
  font-size: 20px;
  color: #606266;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.action-item:hover .el-icon {
  background: #f5f7fa;
  color: #409EFF;
}

.divider {
  width: 1px;
  height: 24px;
  background: #e4e7ed;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: #f5f7fa;
}

.user-avatar {
  border: 2px solid #e4e7ed;
  transition: border-color 0.3s ease;
}

.user-info:hover .user-avatar {
  border-color: #409EFF;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-size: 14px;
  color: #303133;
  font-weight: 600;
}

.user-role {
  font-size: 12px;
  color: #909399;
}

.dropdown-icon {
  font-size: 14px;
  color: #909399;
  transition: transform 0.3s ease;
}

.user-dropdown:hover .dropdown-icon {
  transform: rotate(180deg);
  color: #409EFF;
}

.user-dropdown-menu {
  padding: 8px 0;
}

:deep(.el-dropdown-menu__item) {
  padding: 10px 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
}

:deep(.el-dropdown-menu__item .el-icon) {
  font-size: 16px;
  color: #606266;
}

:deep(.el-dropdown-menu__item:hover) {
  background: #f5f7fa;
}

:deep(.el-dropdown-menu__item:hover .el-icon) {
  color: #409EFF;
}

/* ========== 主内容区 ========== */
.main-content {
  padding: 0;
  overflow-y: auto;
}

/* ========== 过渡动画 ========== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1000;
  }
  
  .header {
    padding: 0 16px;
  }
  
  .user-details {
    display: none;
  }
  
  .breadcrumb-wrapper {
    display: none;
  }
}
</style>
