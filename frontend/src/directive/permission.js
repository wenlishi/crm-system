import { hasPermission } from '@/utils/permission'

/**
 * 权限指令
 * 用法：<el-button v-permission="'customer:add'">新增</el-button>
 *      <el-button v-permission="['customer:add', 'customer:edit']">新增或编辑</el-button>
 */
export default {
  mounted(el, binding) {
    const { value } = binding
    
    if (value) {
      const permissions = Array.isArray(value) ? value : [value]
      const hasPerm = hasPermission(permissions[0]) || 
                     (permissions.length > 1 && hasAnyPermission(permissions))
      
      if (!hasPerm) {
        // 没有权限，移除元素
        el.parentNode && el.parentNode.removeChild(el)
      }
    }
  }
}

/**
 * 判断是否有任意一个权限
 */
function hasAnyPermission(permissions) {
  return permissions.some(permission => hasPermission(permission))
}
