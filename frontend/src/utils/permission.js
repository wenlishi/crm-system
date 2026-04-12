import { useUserStore } from '@/store/user'

/**
 * 判断是否有权限
 * @param {string} permission 权限编码（如：customer:add, customer:edit）
 * @returns {boolean} 是否有权限
 */
export function hasPermission(permission) {
  const userStore = useUserStore()
  const permissions = userStore.permissions || []
  
  // 超级管理员拥有所有权限
  if (userStore.roleCode === 'super_admin') {
    return true
  }
  
  return permissions.includes(permission)
}

/**
 * 判断是否有任意一个权限
 * @param {Array<string>} permissions 权限编码数组
 * @returns {boolean} 是否有权限
 */
export function hasAnyPermission(permissions) {
  return permissions.some(permission => hasPermission(permission))
}

/**
 * 判断是否有所有权限
 * @param {Array<string>} permissions 权限编码数组
 * @returns {boolean} 是否有权限
 */
export function hasAllPermissions(permissions) {
  return permissions.every(permission => hasPermission(permission))
}
